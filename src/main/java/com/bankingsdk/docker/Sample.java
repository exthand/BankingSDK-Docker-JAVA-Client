package com.bankingsdk.docker;

import com.bankingsdk.docker.dto.requests.*;
import com.bankingsdk.docker.dto.response.*;
import com.bankingsdk.docker.exceptions.ApiCallException;
import com.bankingsdk.docker.exceptions.IdentificationException;
import com.bankingsdk.docker.helper.BankingHttpService;
import com.bankingsdk.docker.models.BankAccount;
import com.bankingsdk.docker.models.PaymentRequestOptions;
import com.bankingsdk.docker.models.RequestAccountsOptions;
import com.bankingsdk.docker.models.enums.PaymentRequestDebtorIbanOption;
import com.bankingsdk.docker.models.enums.RequestAccountsAccessOption;
import com.bankingsdk.docker.models.enums.RequestAccountsCurrencyOption;
import com.bankingsdk.docker.models.enums.RequestAccountsLinkingOption;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.apache.http.HttpHeaders;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class Sample {
    public static void main(String[] args) {
        /*
        No optimization have been done for the sake of simplicity of the example
        The repeated code is therefore done intentionally
         */

        System.out.println("Docker sample started");
        String dockerBase = "https://bankingsdk-docker-test.azurewebsites.net";

        // user context is linked to your user and a bank, but holds several consent for several accounts. It must be stored
        // and pass through to the API as it is could be modified by the API. So you have to check in response if not
        // null => has changed => save it for later
        String userContext;
        // YOUR unique id of the PSU user in YOUR app
        String userId = "ABC1234";
        // this is the URL the bank will redirect to after the Strong Customer Authentication has been done successfully
        // or not. An end-to-end id (flow id, see later) given to the bank will be given back by the bank for flow
        // continuity. There is an end point to extract this flow id, see call for getting back the flow id after.
        // This callback URL, for many banks, is checked against the list of valid callback URL you provide when you onboard in the bank
        String scaCallBackUrl = "https://developer.bankingsdk.com/callback";

        // RequestAccountsOptions
        // This helps you to adapt your UI depending on the bank options

        // RequestAccountsOptions.accessOption:
        // Preparing the list of accounts or single account that will maybe be needed to be given to the bank connector
        // this depends on the bank. See documentation bank account option and here further bank account option call
        // depending on that option, you will need to ask the user to enter a single account IBAN, multiple account IBANs
        // or ask nothing, the accounts wil be selected during the bank SCA flow
        // for some banks, you may request only balance access, only transactions access or both.
        // if a bank don't allows separated access request, balance and transaction accounts will be merged before requesting them

        // RequestAccountsOptions.currencyOption:
        // Some bank requires you to provide the currency when requesting account, some don't allow and some don't care :). with this flag, you can adapt the UI or set it fixed in your code

        // RequestAccountsOptions.linkingOption:
        // Some banks accept to have multiple consent for the same user, some don't. This later means that, to get consent
        // for more bank accounts of the same user, you will need to ask the consent for all already connected account plus new ones
        RequestAccountsOptions connectorAccountOptions = null;

        // PaymentRequestOptions: contains the payment options, to determine type of payment available for the specific bank to adapt you UI.
        PaymentRequestOptions connectorPaymentOptions = null;

        List<String> accountsToRequestForBalance = null;
        List<String> accountsToRequestForTransaction = null;
        String singleAccountToRequest = null;

        // Your data about the context of the current execution. They will be saved in the BankingSDK Developer Portal log for your own usage.
        // When looking at the calls in dev portal, you will be able to identify where the call has been done in your app, in which context,
        // for who, group related calls sequence...
        // Those are completely optional.
        // There are no structure but we can imagine App > Unit > TppId > Flow > Transaction
        // App : the name of your app
        // Unit : The part of the app, API, Web...
        // Flow : Identification of the flow
        // Transaction : identification of the transaction
        // You fill the field you want with the value you want.
        // Example, preparing the base we will use all along this on thread run
        TppContext tppContext = new TppContext()
                .setApp("JAVA SAMPLE v1.0.0")
                .setUnit("CONSOLE")
                .setTppId("CURRENT USER ID");

        /*
        COMMON SETTINGS
        Usually common for every connector but could eventually be changed for one or more banks
        Some banks needs a client id, some client id and client secret, some only rely on the eIDAS certs...
        Of course they all need eIDAS certs and often the NCA id (you official id given by the central bank) !
         */
        BankSettingsRequest bankSettings = new BankSettingsRequest()
                .setNcaId("LEGAL_TPP_ID") // your central bank given id
                .setSigningCertificateName("eidas_signing.pfx") // signing aka qseal certificate
                .setSigningCertificatePassword("bankingsdk")
                .setTlsCertificateName("eidas_tls.pfx") // tls aka qwac certificate
                .setTlsCertificatePassword("bankingsdk")
                .setPemFileUrl("URL to your public signing PEM")
                .setAppApiKey("");
        /*
        BNP and it's settings
         */
        // int connectorId = 2;
        // // bank specific settings
        // bankSettings
        //         .setAppClientId("d43ec737-e831-43a2-906b-5620d75e879d") //test3
        //         .setAppClientSecret("a498103dcd8471b63f901263703a295139d43a13555a3d373f0e8415d6401b1d0235468cf4db1e1d0b9680970fb688ac");

       /*
        BE ING
         */
        int connectorId = 1;

        try {
            // simple temporary variables
            int httpStatus;
            String payload;
            String content;
            // when doing multiple step operation with a redirection to the bank in the middle of the flow, you will
            // need to save what we call flow context containing necessary data computed in the first call to
            // docker and give that flow to the second call to docker after redirection. The bank will accept an (flow)ID
            // in SCA redirection that it will transmitted back when it redirects to you to allow you to restore the
            // flow
            UUID flowId = UUID.randomUUID();
            // This variable will contain the flow (jsonified)
            String flow;
            AccountsResponse accountsList;

            /*
              BANK ACCOUNT OPTION == how does the bank accepts the accounts to be linked ?
              Not use in this example but your I/F should take care of this to prompt the right type of field to the user
             */
            BankingHttpService httpRequest = new BankingHttpService();
            httpStatus = httpRequest
                    .setUri(dockerBase + "/Ais/access/option/" + connectorId)
                    .get()
                    .getRequestStatus();
            if (httpStatus == 401 || httpStatus == 403) {
                throw new IdentificationException(String.format("Security exception getting connector option. HTTP status=%d, Server response=%s", httpRequest.getRequestStatus(), httpRequest.getResponsePayloadAsString()));
            }
            if (httpStatus < 200 || httpStatus > 299) {
                throw new ApiCallException(String.format("Execution exception getting connector option. HTTP status=%d, Server response=%s", httpRequest.getRequestStatus(), httpRequest.getResponsePayloadAsString()));
            }
            // Get connector access options to allow the UI to adapt to the situation
            // Hereafter we don't care of those options
            content = httpRequest.getResponsePayloadAsString();
            connectorAccountOptions = getJsonDeserializer().readValue(content, RequestAccountsOptions.class);
            // Just sysout the options
            System.out.printf("Connector %d, access options %s%n", connectorId, connectorAccountOptions.getAccessOption().toString());
            System.out.printf("Connector %d, currency options %s%n", connectorId, connectorAccountOptions.getCurrencyOption().toString());
            System.out.printf("Connector %d, linking options %s%n", connectorId, connectorAccountOptions.getLinkingOption().toString());

            /*
              USER REGISTRATION
             */
            httpRequest = new BankingHttpService();
            httpStatus = httpRequest
                    .setUri(dockerBase + "/Ais/user/register")
                    .addHeader(HttpHeaders.ACCEPT, "text/plain") // MANDATORY, not application/json !!!
                    .setPayload("{\"userId\": \"" + userId + "\", \"connectorId\": " + connectorId + " }")
                    .post()
                    .getRequestStatus();
            if (httpStatus == 401 || httpStatus == 403) {
                throw new IdentificationException(String.format("Security exception registering user. HTTP status=%d, Server response=%s", httpRequest.getRequestStatus(), httpRequest.getResponsePayloadAsString()));
            }
            if (httpStatus < 200 || httpStatus > 299) {
                throw new ApiCallException(String.format("Execution exception registering user. HTTP status=%d, Server response=%s", httpRequest.getRequestStatus(), httpRequest.getResponsePayloadAsString()));
            }
            // Yes, user context is a string. Its structure depends on the connector ! Just save and provide it
            userContext = httpRequest.getResponsePayloadAsString();
            System.out.print("user context created : ");

            /*
              ACCOUNT ACCESS REQUEST
             */
            tppContext.setFlow("Request Account Acc...")
                    .setTransaction(flowId.toString());
            AccountAccessRequest accountAccessRequest = new AccountAccessRequest()
                    .setConnectorId(connectorId)
                    .setUserContext(userContext)
                    .setBankSettings(bankSettings)
                    .setAccountsAccessRequest(new AccountAccessInnerRequest()
                            .setFlowId(flowId.toString())
                            .setFrequencyPerDay(4)
                            .setRedirectUrl(scaCallBackUrl)
                            .setPsuIp("10.0.0.2")
                            // next three can be set, the connector only take into account what he needs
                            // no need to switch here on connector option
                            .setSingleAccount(singleAccountToRequest)
                            .setBalanceAccounts(accountsToRequestForBalance)
                            .setTransactionAccounts(accountsToRequestForTransaction)
                    )
                    .setTppContext(tppContext);
            payload = getJsonSerializer().writeValueAsString(accountAccessRequest);
            httpRequest = new BankingHttpService();
            httpStatus = httpRequest
                    .setUri(dockerBase + "/Ais/access")
                    .setPayload(payload)
                    .post()
                    .getRequestStatus();
            if (httpStatus == 401 || httpStatus == 403) {
                throw new IdentificationException(String.format("Security exception requesting account access. HTTP status=%d, Server response=%s", httpRequest.getRequestStatus(), httpRequest.getResponsePayloadAsString()));
            }
            if (httpStatus < 200 || httpStatus > 299) {
                throw new ApiCallException(String.format("Execution exception requesting account access. HTTP status=%d, Server response=%s", httpRequest.getRequestStatus(), httpRequest.getResponsePayloadAsString()));
            }
            content = httpRequest.getResponsePayloadAsString();
            FinalizeResponse accountAccessResponse = getJsonDeserializer().readValue(content, FinalizeResponse.class);
            // here you should save the flow context with the flow id defined above to fetch it back later
            flow = accountAccessResponse.getFlowContext();
            if (accountAccessResponse.getUserContext() != null) {
                userContext = accountAccessResponse.getUserContext();
            }

            /*
              SCA REDIRECTION
              This must be handled by your app
             */
            System.out.println("We've got a SCA redirection URL : " + accountAccessResponse.getDataString());
            System.out.print("Open it ? [Y/n]");
            BufferedReader reader =
                    new BufferedReader(new InputStreamReader(System.in));
            String openUrl = reader.readLine();
            if (!openUrl.toUpperCase().equals("N")) {
                openUrl(accountAccessResponse.getDataString());
            }
            System.out.println("");
            System.out.print("Paste the querystring where the bank redirects you:");
            reader = new BufferedReader(new InputStreamReader(System.in));
            String querystring = reader.readLine();

            /*
            Getting back the flowid from the queryString received from the bank
             */
            httpStatus = httpRequest
                    .setUri(dockerBase + "/Ais/access/findFlowId?queryString=" + URLEncoder.encode(querystring, StandardCharsets.UTF_8.toString()))
                    .addHeader(HttpHeaders.ACCEPT, "text/plain") // MANDATORY, not application/json !!!
                    .get()
                    .getRequestStatus();
            if (httpStatus == 401 || httpStatus == 403) {
                throw new IdentificationException(String.format("Security exception extracting flow id from query string. HTTP status=%d, Server response=%s", httpRequest.getRequestStatus(), httpRequest.getResponsePayloadAsString()));
            }
            if (httpStatus < 200 || httpStatus > 299) {
                throw new ApiCallException(String.format("Execution exception extracting flow id from query string. HTTP status=%d, Server response=%s", httpRequest.getRequestStatus(), httpRequest.getResponsePayloadAsString()));
            }
            // get and save the user context which have certainly been modified
            content = httpRequest.getResponsePayloadAsString();
            flowId = UUID.fromString(content);
            // With this flowid you should be able the retrieve the flow context string from your database
            // Here we use the flow we receive from previous call
            flow = accountAccessResponse.getFlowContext();

            /*
              Finalizing the BankAccount Access Request
             */
            tppContext.setFlow("finalize Request Access...")
                    .setTransaction(flowId.toString());
            FinalizeRequest accountAccessFinalizeRequest = new FinalizeRequest()
                    .setFlow(flow)
                    .setDataString(querystring)
                    .setUserContext(userContext)
                    .setBankSettings(bankSettings)
                    .setTppContext(tppContext);
            payload = getJsonSerializer().writeValueAsString(accountAccessFinalizeRequest);
            httpRequest = new BankingHttpService();
            httpStatus = httpRequest
                    .setUri(dockerBase + "/Ais/access/finalize")
                    // .addHeader(HttpHeaders.ACCEPT, "text/plain") // MANDATORY, not application/json !!!
                    .setPayload(payload)
                    .post()
                    .getRequestStatus();
            if (httpStatus == 401 || httpStatus == 403) {
                throw new IdentificationException(String.format("Security exception finalizing account access. HTTP status=%d, Server response=%s", httpRequest.getRequestStatus(), httpRequest.getResponsePayloadAsString()));
            }
            if (httpStatus < 200 || httpStatus > 299) {
                throw new ApiCallException(String.format("Execution exception finalizing account access. HTTP status=%d, Server response=%s", httpRequest.getRequestStatus(), httpRequest.getResponsePayloadAsString()));
            }
            content = httpRequest.getResponsePayloadAsString();
            accountAccessResponse = getJsonDeserializer().readValue(content, FinalizeResponse.class);
            // get and save the user context which have certainly been modified
            if (accountAccessResponse.getUserContext() != null) {
                userContext = accountAccessResponse.getUserContext();
            }

            /*
              Getting Accounts
             */
            tppContext.setFlow("getting accounts")
                    .setTransaction(null);
            SimpleRequest simpleRequest = new SimpleRequest()
                    .setConnectorId(connectorId)
                    .setUserContext(userContext)
                    .setBankSettings(bankSettings)
                    .setTppContext(tppContext);
            payload = getJsonSerializer().writeValueAsString(simpleRequest);
            httpRequest = new BankingHttpService();
            httpStatus = httpRequest
                    .setUri(dockerBase + "/Ais/accounts")
                    .setPayload(payload)
                    .post()
                    .getRequestStatus();
            if (httpStatus == 401 || httpStatus == 403) {
                throw new IdentificationException(String.format("Security exception getting accounts. HTTP status=%d, Server response=%s", httpRequest.getRequestStatus(), httpRequest.getResponsePayloadAsString()));
            }
            if (httpStatus < 200 || httpStatus > 299) {
                throw new ApiCallException(String.format("Execution exception getting accounts. HTTP status=%d, Server response=%s", httpRequest.getRequestStatus(), httpRequest.getResponsePayloadAsString()));
            }
            content = httpRequest.getResponsePayloadAsString();
            accountsList = getJsonDeserializer().readValue(content, AccountsResponse.class);
            // update the userContext which could have been modified
            if (accountsList.getUserContext() != null) {
                // not null means has changed, so you should save it for later reuse
                userContext = accountsList.getUserContext();
            }

            /*
              Getting Balances
             */
            tppContext.setFlow("getting balance"); // sound stupid but for some banks, getting the balance is done by getting the account list... :)
            for (BankAccount account : accountsList.getAccounts()) {
                tppContext.setTransaction(account.getId() + ":" + account.getIban());
                // intentionally duplicated code for simplicity
                simpleRequest = new SimpleRequest()
                        .setConnectorId(connectorId)
                        .setUserContext(userContext)
                        .setBankSettings(bankSettings)
                        .setTppContext(tppContext);
                payload = getJsonSerializer().writeValueAsString(simpleRequest);
                httpRequest = new BankingHttpService();
                httpStatus = httpRequest
                        .setUri(dockerBase + "/Ais/accounts/" + account.getId() + "/balances")
                        .setPayload(payload)
                        .post()
                        .getRequestStatus();
                if (httpStatus == 401 || httpStatus == 403) {
                    throw new IdentificationException(String.format("Security exception getting balance of account id=%s, iban=%s. HTTP status=%d, Server response=%s", account.getId(), account.getIban(), httpRequest.getRequestStatus(), httpRequest.getResponsePayloadAsString()));
                }
                if (httpStatus < 200 || httpStatus > 299) {
                    throw new ApiCallException(String.format("Execution exception getting balance of account id=%s, iban=%s. HTTP status=%d, Server response=%s", account.getId(), account.getIban(), httpRequest.getRequestStatus(), httpRequest.getResponsePayloadAsString()));
                }
                content = httpRequest.getResponsePayloadAsString();
                BalanceResponse balanceResponse = getJsonDeserializer().readValue(content, BalanceResponse.class);
                // How lazy I can be :)
                System.out.println(getJsonSerializer().writeValueAsString(balanceResponse));
                // get and save the user context which may have been modified
                if (balanceResponse.getUserContext() != null) {
                    // not null means has changed, so you should save it for later reuse
                    userContext = balanceResponse.getUserContext();
                }
            }

            /*
              Getting Transactions
             */
            tppContext.setFlow("getting transactions");
            for (BankAccount account : accountsList.getAccounts()) {
                tppContext.setTransaction(account.getId() + ":" + account.getIban());
                // intentionally duplicated code for simplicity
                TransactionsFirstRequest transactionsFirstRequest = new TransactionsFirstRequest()
                        .setLimit(1)
                        .setConnectorId(connectorId)
                        .setUserContext(userContext)
                        .setBankSettings(bankSettings)
                        .setTppContext(tppContext);
                payload = getJsonSerializer().writeValueAsString(simpleRequest);
                httpRequest = new BankingHttpService();
                httpStatus = httpRequest
                        .setUri(dockerBase + "/Ais/accounts/" + account.getId() + "/transactions")
                        .setPayload(payload)
                        .post()
                        .getRequestStatus();
                if (httpStatus == 401 || httpStatus == 403) {
                    throw new IdentificationException(String.format("Security exception getting transactions of account id=%s, iban=%s. HTTP status=%d, Server response=%s", account.getId(), account.getIban(), httpRequest.getRequestStatus(), httpRequest.getResponsePayloadAsString()));
                }
                if (httpStatus < 200 || httpStatus > 299) {
                    throw new ApiCallException(String.format("Execution exception getting transactions of account id=%s, iban=%s. HTTP status=%d, Server response=%s", account.getId(), account.getIban(), httpRequest.getRequestStatus(), httpRequest.getResponsePayloadAsString()));
                }
                content = httpRequest.getResponsePayloadAsString();
                TransactionResponse transactionResponse = getJsonDeserializer().readValue(content, TransactionResponse.class);
                // Process records
                // How lazy I can be :)
                System.out.println(getJsonSerializer().writeValueAsString(transactionResponse));
                // get and save the user context which may have been modified
                if (transactionResponse.getUserContext() != null) {
                    // not null means has changed, so you should save it for later reuse
                    userContext = transactionResponse.getUserContext();
                }
                String pagerContext = transactionResponse.getPagerContext();
                while (!transactionResponse.isLastPage()) {
                    TransactionsRequest transactionsRequest = new TransactionsRequest()
                            .setConnectorId(connectorId)
                            .setUserContext(userContext)
                            .setBankSettings(bankSettings)
                            .setPagerContext(pagerContext)
                            .setTppContext(tppContext);
                    payload = getJsonSerializer().writeValueAsString(simpleRequest);
                    httpRequest = new BankingHttpService();
                    httpStatus = httpRequest
                            .setUri(dockerBase + "/Ais/accounts/" + account.getId() + "/transactions/next")
                            .setPayload(payload)
                            .post()
                            .getRequestStatus();
                    if (httpStatus == 401 || httpStatus == 403) {
                        throw new IdentificationException(String.format("Security exception getting transactions of account id=%s, iban=%s. HTTP status=%d, Server response=%s", account.getId(), account.getIban(), httpRequest.getRequestStatus(), httpRequest.getResponsePayloadAsString()));
                    }
                    if (httpStatus < 200 || httpStatus > 299) {
                        throw new ApiCallException(String.format("Execution exception getting transactions of account id=%s, iban=%s. HTTP status=%d, Server response=%s", account.getId(), account.getIban(), httpRequest.getRequestStatus(), httpRequest.getResponsePayloadAsString()));
                    }
                    content = httpRequest.getResponsePayloadAsString();
                    // Process records
                    // Here only dump it. How lazy I can be :)
                    System.out.println(getJsonSerializer().writeValueAsString(transactionResponse));
                    transactionResponse = getJsonDeserializer().readValue(content, TransactionResponse.class);
                    if (transactionResponse.getUserContext() != null) {
                        // not null means has changed, so you should save it for later reuse
                        userContext = transactionResponse.getUserContext();
                    }
                    // save the page context for next iteration if any
                    pagerContext = transactionResponse.getPagerContext();
                }
            }

            /*
            Payment Initiation Request
            In sandbox mode, don't expect the transaction to correspond to the data you gave, bank's sandbox often
            request the payment data to be fixed data... :'(
            Each PIR requires a SCA by the PSU even if you have a consent for AIS.
             */

            httpRequest = new BankingHttpService();
            httpStatus = httpRequest
                    .setUri(dockerBase + "/Pis/payment/options/" + connectorId)
                    .get()
                    .getRequestStatus();
            if (httpStatus == 401 || httpStatus == 403) {
                throw new IdentificationException(String.format("Security exception getting connector option. HTTP status=%d, Server response=%s", httpRequest.getRequestStatus(), httpRequest.getResponsePayloadAsString()));
            }
            if (httpStatus < 200 || httpStatus > 299) {
                throw new ApiCallException(String.format("Execution exception getting connector option. HTTP status=%d, Server response=%s", httpRequest.getRequestStatus(), httpRequest.getResponsePayloadAsString()));
            }
            // Get connector access options to allow the UI to adapt to the situation
            // Hereafter we don't care of those options
            // There is certainly a better way to deserialize the response to Enum fields :P
            PaymentOptionsResponse paymentOptionsResponse = getJsonDeserializer().readValue(httpRequest.getResponsePayloadAsString(), PaymentOptionsResponse.class);
            connectorPaymentOptions = new PaymentRequestOptions(
                    PaymentRequestDebtorIbanOption.fromOrdinal(paymentOptionsResponse.getDebtorIban()),
                    paymentOptionsResponse.getSepaCreditTransfers(),
                    paymentOptionsResponse.getInstantSepaCreditTransfers(),
                    paymentOptionsResponse.getCrossborderPayments(),
                    paymentOptionsResponse.getTarget2Payment()
            );
            System.out.println(getJsonSerializer().writeValueAsString(connectorPaymentOptions));

            if (accountsList.getAccounts().size() > 0) {
                BankAccount debtorAccount = accountsList.getAccounts().get(0);
                System.out.println("Initiating payment");
                tppContext.setFlow("payment:" + flowId.toString())
                        .setTransaction(debtorAccount.getId() + ":" + debtorAccount.getIban());
                PaymentRequest paymentRequest = new PaymentRequest()
                        .setConnectorId(connectorId)
                        .setBankSettings(bankSettings)
                        .setUserContext(userContext)
                        .setPaymentInitiationRequest(new PaymentInitiationRequest()
                                .setRecipient(new PaymentRecipient()
                                        .setName("Other Business ltd")
                                        .setIban("AT861921125678901234")
                                )
                                .setDebtor(new PaymentDebtor()
                                        .setName(debtorAccount.getDescription())
                                        .setIban(debtorAccount.getIban())
                                        .setCurrency(debtorAccount.getCurrency())
                                )
                                .setPsuIp("10.0.0.1")
                                .setRedirectUrl(scaCallBackUrl)
                                .setEndToEndId(UUID.randomUUID().toString().replaceAll("-", ""))
                                .setRequestedExecutionDate(Calendar.getInstance())
                                .setAmount(getRandomPaymentAmountInCent(50, 199))
                                .setCurrency("EUR")
                                .setFlowId(flowId.toString())
                                .setRemittanceInformationUnstructured("Test BankingSDK Docker payment")
                        )
                        .setTppContext(tppContext);
                payload = getJsonSerializer().writeValueAsString(paymentRequest);
                httpRequest = new BankingHttpService();
                httpStatus = httpRequest
                        .setUri(dockerBase + "/Pis/payment")
                        .setPayload(payload)
                        .post()
                        .getRequestStatus();
                if (httpStatus == 401 || httpStatus == 403) {
                    throw new IdentificationException(String.format("Security exception payment from account=%s. HTTP status=%d, Server response=%s", debtorAccount.getIban(), httpRequest.getRequestStatus(), httpRequest.getResponsePayloadAsString()));
                }
                if (httpStatus < 200 || httpStatus > 299) {
                    throw new ApiCallException(String.format("Execution exception payment from account=%s. HTTP status=%d, Server response=%s", debtorAccount.getIban(), httpRequest.getRequestStatus(), httpRequest.getResponsePayloadAsString()));
                }
                // update the userContext which could have been modified
                content = httpRequest.getResponsePayloadAsString();
                FinalizeResponse paymentInitiationResponse = getJsonDeserializer().readValue(content, FinalizeResponse.class);
                if (paymentInitiationResponse.getUserContext() != null) {
                    // not null means has changed, so you should save it for later reuse
                    userContext = paymentInitiationResponse.getUserContext();
                }
                // here you should save the flow context with the flowid defined above
                flow = paymentInitiationResponse.getFlowContext();

                System.out.println("Payment Initiation requested ! Flow Id=" + flowId);
                System.out.println("If the bank's sandbox doesn't allow SCA, try to copy/paste a self generated query string:");
                System.out.println("?state=" + flowId);

                /*
                  SCA REDIRECTION
                  This must be handled by your app
                 */
                System.out.println("We've got a SCA redirection URL : " + paymentInitiationResponse.getDataString());
                System.out.print("Open it ? [Y/n]");
                reader =
                        new BufferedReader(new InputStreamReader(System.in));
                openUrl = reader.readLine();
                if (!openUrl.toUpperCase().equals("N")) {
                    openUrl(paymentInitiationResponse.getDataString());
                }
                System.out.println("");
                System.out.print("Paste the querystring where the bank redirects you:");
                reader = new BufferedReader(new InputStreamReader(System.in));
                querystring = reader.readLine();

                /*
                Getting back the flowid from the queryString received from the bank
                 */
                httpStatus = httpRequest
                        .setUri(dockerBase + "/Ais/access/findFlowId?queryString=" + URLEncoder.encode(querystring, StandardCharsets.UTF_8.toString()))
                        .addHeader(HttpHeaders.ACCEPT, "text/plain") // MANDATORY, not application/json !!!
                        .get()
                        .getRequestStatus();
                if (httpStatus == 401 || httpStatus == 403) {
                    throw new IdentificationException(String.format("Security exception extracting flow id from query string. HTTP status=%d, Server response=%s", httpRequest.getRequestStatus(), httpRequest.getResponsePayloadAsString()));
                }
                if (httpStatus < 200 || httpStatus > 299) {
                    throw new ApiCallException(String.format("Execution exception extracting flow id from query string. HTTP status=%d, Server response=%s", httpRequest.getRequestStatus(), httpRequest.getResponsePayloadAsString()));
                }
                // get and save the user context which have certainly been modified
                flowId = UUID.fromString(httpRequest.getResponsePayloadAsString());
                // With this flowid you should be able the retrieve the flow context string from your database
                // Here we use the flow we recieve from previous call
                flow = paymentInitiationResponse.getFlowContext();

                /*
                Finalizing the payment
                 */
                System.out.println("Finalizing the payment");
                FinalizeRequest paymentFinalyseRequest = new FinalizeRequest()
                        .setFlow(flow)
                        .setDataString(querystring)
                        .setUserContext(userContext)
                        .setBankSettings(bankSettings)
                        .setTppContext(tppContext);
                payload = getJsonSerializer().writeValueAsString(paymentFinalyseRequest);
                httpStatus = httpRequest
                        .setUri(dockerBase + "/Pis/payment/finalize")
                        .setPayload(payload)
                        .post()
                        .getRequestStatus();
                if (httpStatus == 401 || httpStatus == 403) {
                    throw new IdentificationException(String.format("Security exception finalizing payment. HTTP status=%d, Server response=%s", httpRequest.getRequestStatus(), httpRequest.getResponsePayloadAsString()));
                }
                if (httpStatus < 200 || httpStatus > 299) {
                    throw new ApiCallException(String.format("Execution exception finalizing payment. HTTP status=%d, Server response=%s", httpRequest.getRequestStatus(), httpRequest.getResponsePayloadAsString()));
                }
                content = httpRequest.getResponsePayloadAsString();
                PaymentFinalizeResponse paymentFinalizeResponse = getJsonDeserializer().readValue(content, PaymentFinalizeResponse.class);
                System.out.println(getJsonSerializer().writeValueAsString(paymentFinalizeResponse));
            }

            /*
            Delete linked account
             */
            if (accountsList.getAccounts().size() > 0) {
                System.out.println("User context before account deletion : " + userContext);
                // Let's remove the first one
                BankAccount accountToDelete = accountsList.getAccounts().get(0);
                tppContext.setFlow("Delete:" + flowId.toString())
                        .setTransaction(accountToDelete.getId() + ":" + accountToDelete.getIban());
                DeleteAccountRequest deleteAccountRequest = new DeleteAccountRequest()
                        .setId(accountToDelete.getId())
                        .setConnectorId(connectorId)
                        .setUserContext(userContext)
                        .setBankSettings(bankSettings)
                        .setTppContext(tppContext);
                payload = getJsonSerializer().writeValueAsString(deleteAccountRequest);
                httpRequest = new BankingHttpService();
                httpStatus = httpRequest
                        .setUri(dockerBase + "/Ais/access/deleteAccount")
                        .addHeader(HttpHeaders.ACCEPT, "text/plain") // MANDATORY, not application/json !!!
                        .setPayload(payload)
                        .post()
                        .getRequestStatus();
                if (httpStatus == 401 || httpStatus == 403) {
                    throw new IdentificationException(String.format("Security exception deleting account id=%s. HTTP status=%d, Server response=%s", accountToDelete, httpRequest.getRequestStatus(), httpRequest.getResponsePayloadAsString()));
                }
                if (httpStatus < 200 || httpStatus > 299) {
                    throw new ApiCallException(String.format("Execution exception deleting account id=%s. HTTP status=%d, Server response=%s", accountToDelete, httpRequest.getRequestStatus(), httpRequest.getResponsePayloadAsString()));
                }
                // update the userContext which could have been modified
                userContext = httpRequest.getResponsePayloadAsString();
                System.out.println("User context after account deletion : " + userContext);
            }

            int stupidDebugStop = 10;

        } catch (ApiCallException | IdentificationException | IOException | UnsupportedOperationException e) {
            e.printStackTrace();
        }

    }

    private static double getRandomPaymentAmountInCent(int min, int max) {
        return ((double) new Random().nextInt(199 - 50 + 1) + 50) / 100.0;
    }

    private static ObjectMapper getJsonDeserializer() {
        return new ObjectMapper()
                .configure(DeserializationFeature.READ_ENUMS_USING_TO_STRING, true)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
    }

    private static ObjectWriter getJsonSerializer() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        return new ObjectMapper()
                .setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY)
                .setDateFormat(df)
                .writerWithDefaultPrettyPrinter();
    }

    private static void openUrl(String url) throws UnsupportedOperationException, IOException {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            Runtime rt = Runtime.getRuntime();
            rt.exec("rundll32 url.dll,FileProtocolHandler " + url);
        } else if (os.contains("mac")) {
            Runtime rt = Runtime.getRuntime();
            rt.exec("open " + url);
        } else if (os.contains("nix") || os.contains("nux")) {
            Runtime rt = Runtime.getRuntime();
            String[] browsers = {"epiphany", "firefox", "mozilla", "konqueror",
                    "netscape", "opera", "links", "lynx"};

            StringBuilder cmd = new StringBuilder();
            for (int i = 0; i < browsers.length; i++)
                if (i == 0)
                    cmd.append(String.format("%s \"%s\"", browsers[i], url));
                else
                    cmd.append(String.format(" || %s \"%s\"", browsers[i], url));
            // If the first didn't work, try the next browser and so on

            rt.exec(new String[]{"sh", "-c", cmd.toString()});
        } else {
            throw new UnsupportedOperationException("Unknown OS : " + os);
        }
    }

    private static class TestJsonDate {
        public Calendar validUntil;
    }
}
