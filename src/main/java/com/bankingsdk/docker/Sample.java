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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.UUID;

public class Sample {
    public static void main(String[] args) {

        System.out.println("Docker sample started");
        // String dockerBase = "https://bankingsdk-docker-test.azurewebsites.net";
        String dockerBase = "https://localhost:5001";
        // user context is linked to your user and a bank, but holds several consent for several accounts. It must be stored and pass through to the API as it is
        // could be modified by the API. So you have to check in response if not null => has changed => save it for later
        String userContext;
        // YOUR unique id of the PSU user in YOUR app
        String userId = "ABC1234";

        /*
        COMMON SETTINGS
        Usually common for every connector but could eventually be changed for one or more banks
        Some banks needs a client id, some client id and client secret, some only rely on the eIDAS certs...
        Of course they all need eIDAS certs and often the NCA id (you official id given by the central bank) !
         */
        BankSettingsRequest bankSettings = new BankSettingsRequest()
                .setNcaId("LEGAL_TPP_ID")
                .setSigningCertificateName("eidas_signing.pfx")
                .setSigningCertificatePassword("bankingsdk")
                .setTlsCertificateName("eidas_tls.pfx")
                .setTlsCertificatePassword("bankingsdk");
        /*
        BNP and it's settings
         */
        // int connectorId = 2;
        // bank specific settings
        // bankSettings
        //         .setAppClientId("c2b8a2da-2a04-446c-a810-165373564334")
        //         .setAppClientSecret("c9ebf4292aec99d26150d8124e84db57661e6a596cbed9e9952173750940d4b194c5615b6d3b8a0e3ea355c438e06bcb");

       /*
        BE ING
         */
        int connectorId = 1;

        try {
            int httpStatus;
            String payload;
            String content;
            UUID flowId = UUID.randomUUID();
            BankConnectorOptions connectorOption = BankConnectorOptions.UNKNOWN;

            /*
              BANK ACCOUNT OPTION == how does the bank accepts the accounts to be linked ?
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
                    .addHeader(HttpHeaders.ACCEPT, "text/plain") // MANDATORY, no application/json !!!
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
                            .setRedirectUrl("https://developer.bankingsdk.com/callback")
                            .setPsuIp("10.0.0.2")
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

            /*
              SCA REDIRECTION
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
            reader =
                    new BufferedReader(new InputStreamReader(System.in));
            String querystring = reader.readLine();

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
                    .addHeader(HttpHeaders.ACCEPT, "text/plain") // MANDATORY, no application/json !!!
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
            AccountsResponse accountsResponse = getJsonDeserializer().readValue(content, AccountsResponse.class);
            // update the userContext which could have been modified
            if (accountsResponse.getUserContext() != null) {
                // not null means has changed, so you should save it for later reuse
                userContext = accountsResponse.getUserContext();
            }

            /*
              Getting Balances
             */
            for (BankAccount account : accountsResponse.getAccounts()) {
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
                if (accountsResponse.getUserContext() != null) {
                    // not null means has changed, so you should save it for later reuse
                    userContext = balanceResponse.getUserContext();
                }
            }

            /*
              Getting Transactions
             */
            for (BankAccount account : accountsResponse.getAccounts()) {
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

            int debug = 10;

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
