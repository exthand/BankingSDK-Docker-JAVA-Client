package com.bankingsdk.docker;

import com.bankingsdk.docker.dto.requests.*;
import com.bankingsdk.docker.dto.response.*;
import com.bankingsdk.docker.exceptions.ApiCallException;
import com.bankingsdk.docker.exceptions.IdentificationException;
import com.bankingsdk.docker.helper.BankingHttpService;
import com.bankingsdk.docker.models.BankAccount;
import com.bankingsdk.docker.models.BankConnectorOptions;
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

        // Preparing the list of accounts or single account that will maybe be needed to be given to the bank connector
        // this depends on the bank. See documentation bank account option and here further bank account option call
        // depending on that option, you will need to ask the user to enter a single account IBAN, multiple account IBANs
        // or ask nothing, the accounts wil be selected during the bank SCA flow
        // for some banks, you may request only balance access, only transactions access or both.
        // if a bank don't allows separated access request, balance and transaction accounts will be merged before requesting them
        BankConnectorOptions connectorOption = BankConnectorOptions.UNKNOWN;
        List<String> accountsToRequestForBalance = null;
        List<String> accountsToRequestForTransaction = null;
        String singleAccountToRequest = null;


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
                .setTlsCertificatePassword("bankingsdk");

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
            AccountsResponse accountsList;

            /*
              BANK ACCOUNT OPTION == how does the bank accepts the accounts to be linked ?
              Not use in this example
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
            // Yes, user context is a string. Its structure depends on the connector ! Just save and provide it
            ConnectorOptionResponse connectorOptionResponse = getJsonDeserializer().readValue(httpRequest.getResponsePayloadAsString(), ConnectorOptionResponse.class);
            connectorOption = BankConnectorOptions.fromOrdinal(connectorOptionResponse.getOption());
            System.out.println(String.format("Connector %d, option %s", connectorId, connectorOption.toString()));

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
            AccountAccessRequest accountAccessRequest = new AccountAccessRequest()
                    .setConnectorId(connectorId)
                    .setUserContext(userContext)
                    .setBankSettings(bankSettings)
                    .setAccountsAccessRequest(new AccountAccessInnerRequest()
                            .setFlowId(flowId.toString())
                            .setFrequencyPerDay(4)
                            .setRedirectUrl(scaCallBackUrl)
                            .setPsuIp("10.0.0.2")
                            .setSingleAccount(singleAccountToRequest)
                            .setBalanceAccounts(accountsToRequestForBalance)
                            .setTransactionAccounts(accountsToRequestForTransaction)
                    );
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
            AccountAccessResponse accountAccessResponse = getJsonDeserializer().readValue(content, AccountAccessResponse.class);
            // here you should save the flow context with the flowid defined above

            /*
              SCA REDIRECTION
              This must be handled by your
             */
            System.out.println("We've got a SCA redirection URL : " + accountAccessResponse.getRedirectUrl());
            System.out.print("Open it ? [Y/n]");
            BufferedReader reader =
                    new BufferedReader(new InputStreamReader(System.in));
            String openUrl = reader.readLine();
            if (!openUrl.toUpperCase().equals("N")) {
                openUrl(accountAccessResponse.getRedirectUrl());
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
                    .setPayload(payload)
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

            /*
              Finalizing the BankAccount Access Request
             */
            AccountAccessFinalizeRequest accountAccessFinalizeRequest = new AccountAccessFinalizeRequest()
                    .setFlow(accountAccessResponse.getFlowContext())
                    .setQueryString(querystring)
                    .setUserContext(userContext)
                    .setBankSettings(bankSettings);
            payload = getJsonSerializer().writeValueAsString(accountAccessFinalizeRequest);
            httpRequest = new BankingHttpService();
            httpStatus = httpRequest
                    .setUri(dockerBase + "/Ais/access/finalize")
                    .addHeader(HttpHeaders.ACCEPT, "text/plain") // MANDATORY, not application/json !!!
                    .setPayload(payload)
                    .post()
                    .getRequestStatus();
            if (httpStatus == 401 || httpStatus == 403) {
                throw new IdentificationException(String.format("Security exception finalizing account access. HTTP status=%d, Server response=%s", httpRequest.getRequestStatus(), httpRequest.getResponsePayloadAsString()));
            }
            if (httpStatus < 200 || httpStatus > 299) {
                throw new ApiCallException(String.format("Execution exception finalizing account access. HTTP status=%d, Server response=%s", httpRequest.getRequestStatus(), httpRequest.getResponsePayloadAsString()));
            }
            // get and save the user context which have certainly been modified
            userContext = httpRequest.getResponsePayloadAsString();

            /*
              Getting Accounts
             */
            SimpleRequest simpleRequest = new SimpleRequest()
                    .setConnectorId(connectorId)
                    .setUserContext(userContext)
                    .setBankSettings(bankSettings);
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
            for (BankAccount account : accountsList.getAccounts()) {
                // intentionally duplicated code for simplicity
                simpleRequest = new SimpleRequest()
                        .setConnectorId(connectorId)
                        .setUserContext(userContext)
                        .setBankSettings(bankSettings);
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
                if (accountsList.getUserContext() != null) {
                    // not null means has changed, so you should save it for later reuse
                    userContext = balanceResponse.getUserContext();
                }
            }

            /*
              Getting Transactions
             */
            for (BankAccount account : accountsList.getAccounts()) {
                // intentionally duplicated code for simplicity
                TransactionsFirstRequest transactionsFirstRequest = new TransactionsFirstRequest()
                        .setLimit(1)
                        .setConnectorId(connectorId)
                        .setUserContext(userContext)
                        .setBankSettings(bankSettings);
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
                            .setPagerContext(pagerContext);
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
            Delete linked account
             */
            if (accountsList.getAccounts().size() > 0) {
                System.out.println("User context before account deletion : " + userContext);
                // Let's remove the first one
                String accountIdToDelete = accountsList.getAccounts().get(0).getId();
                DeleteAccountRequest deleteAccountRequest = new DeleteAccountRequest()
                        .setId(accountIdToDelete)
                        .setConnectorId(connectorId)
                        .setUserContext(userContext)
                        .setBankSettings(bankSettings);
                payload = getJsonSerializer().writeValueAsString(deleteAccountRequest);
                httpRequest = new BankingHttpService();
                httpStatus = httpRequest
                        .setUri(dockerBase + "/Ais/access/deleteAccount")
                        .addHeader(HttpHeaders.ACCEPT, "text/plain") // MANDATORY, not application/json !!!
                        .setPayload(payload)
                        .post()
                        .getRequestStatus();
                if (httpStatus == 401 || httpStatus == 403) {
                    throw new IdentificationException(String.format("Security exception deleting account id=%s. HTTP status=%d, Server response=%s", accountIdToDelete, httpRequest.getRequestStatus(), httpRequest.getResponsePayloadAsString()));
                }
                if (httpStatus < 200 || httpStatus > 299) {
                    throw new ApiCallException(String.format("Execution exception deleting account id=%s. HTTP status=%d, Server response=%s", accountIdToDelete, httpRequest.getRequestStatus(), httpRequest.getResponsePayloadAsString()));
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

    private static ObjectMapper getJsonDeserializer() {
        return new ObjectMapper()
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
