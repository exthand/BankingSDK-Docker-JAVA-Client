// Import dependencies
const fetch = require('node-fetch');
const { v4: uuidv4 } = require('uuid');
const readline = require('readline');

const scaCallBackUrl = "https://developer.bankingsdk.com/callback"; // This should be a callback to your application
const baseUrl = "https:\/\/bankingsdk-docker-test.azurewebsites.net"
const userId = "ABC123"
let flowId = uuidv4(); // random guid to identify a new flow

let userContext = null;

const bankSettings = {
    ncaId: "LEGAL_TPP_ID",
    tlsCertificateName: "eidas_tls.pfx",
    tlsCertificatePassword: "bankingsdk",
    signingCertificateName: "eidas_signing.pfx",
    signingCertificatePassword: "bankingsdk"
}

// Choose correct connectorId
let connectorId = 1; // 1=ING | 2=BNP

if (connectorId == 2) {
    // Special things for BNP Fortis
    bankSettings
        .setAppClientId("d43ec737-e831-43a2-906b-5620d75e879d") 
        .setAppClientSecret("a498103dcd8471b63f901263703a295139d43a13555a3d373f0e8415d6401b1d0235468cf4db1e1d0b9680970fb688ac");
}


(async () => {

    /*
        Get the options. 
        This can be used to ask the user for a accountreference or not
    */
    const accessOptionResponse = await fetch(baseUrl + "/Ais/access/option/" + connectorId);
    const accessOption = await accessOptionResponse.json()
	console.log("option: " + accessOption.option); // 0=MultipleAccounts|1=NoAccount|2=SingleAccount


    /*
        User registration
    */
    let UserRegistrationRequestBody = { userId, connectorId };
	const userRegistrationResponse = await fetch(baseUrl + "/Ais/user/register", {
		method: 'post',
		body: JSON.stringify(UserRegistrationRequestBody),
		headers: {'Content-Type': 'application/json', 'accept': 'text/plain'}
	});
	userContext = await userRegistrationResponse.text();
	console.log("user context created : " + userContext);

    /*
        Account access request
    */
    const innerAcountsAccessRequestBody = {
        flowId,
        redirectUrl: scaCallBackUrl,
        frequencyPerDay: 4,
        psuIp: "10.0.0.2",
        singleAccount: null, 
        transactionAccounts: [], 
        balanceAccounts: [] 
    }

    // next three can be set, the connector only take into account what he needs
    // no need to switch here on connector option
    innerAcountsAccessRequestBody.singleAccount = null
    innerAcountsAccessRequestBody.transactionAccounts = null
    innerAcountsAccessRequestBody.balanceAccounts = null

    const accountsAccessRequestBody = { userContext, connectorId, bankSettings, accountsAccessRequest: innerAcountsAccessRequestBody };

	const accountAccessRequestResponse = await fetch(baseUrl + "/Ais/access", {
		method: 'post',
		body: JSON.stringify(accountsAccessRequestBody),
		headers: {'Content-Type': 'application/json'}
    });
    
    const accountAccessRequestBody = await accountAccessRequestResponse.json();
    console.log("accountAccessRequest: " + JSON.stringify(accountAccessRequestBody))
           
    // here you should save the flow context in your DB with the flow id defined above
    let flow = accountAccessRequestBody.flowContext
    console.log("flow: " + flow)
    console.log("redirect the your app to: " + accountAccessRequestBody.redirectUrl)


    /*
        Getting back the flowid from the queryString received from the bank
    */
    const queryString = await askQuestion("please provide the querystring where the bank would redirect you to: ")
    const flowIdResponse = await fetch(baseUrl + "/Ais/access/findFlowId?queryString=" + encodeURIComponent(queryString), {
        method: 'get',
        headers: {'accept': 'text/plain'}
    });

    flowId = await flowIdResponse.text();  // In this sample of course the same as what we started with
   
    // With this flowid you should be able the retrieve the flow context string from your database
    // Here we use the flow we receive from previous call
    flow = accountAccessRequestBody.flowContext; 



    /*
        Finalizing the BankAccount Access Request
    */
    const accountAccessFinalizeRequestBody = {
        flow,
        queryString,
        userContext,
        bankSettings  
    }
    
    const accountAccessFinalizeRequestResponse = await fetch(baseUrl + "/Ais/access/finalize", {
        method: 'post',
        body: JSON.stringify(accountAccessFinalizeRequestBody),
        headers: {'Content-Type': 'application/json', 'accept': 'text/plain'}
    });

    userContext = await accountAccessFinalizeRequestResponse.text();
    console.log("new userContext: " + userContext)
    

    /*
        Getting Accounts
    */
   const gettingsAccountsRequestPayload = {
       connectorId,
       userContext,
       bankSettings
   } 

   const gettingAccountsResponse = await fetch(baseUrl + "/Ais/accounts", {
        method: 'post',
        body: JSON.stringify(gettingsAccountsRequestPayload),
        headers: {'Content-Type': 'application/json'}
    });
    
    const gettingAccountsResponseBody = await gettingAccountsResponse.json()
    const accounts = gettingAccountsResponseBody.accounts
    console.log("accounts: " + JSON.stringify(accounts))

    if (gettingAccountsResponseBody.userContext) {
        // not null means has changed, so you should save it for later reuse
        userContext = gettingAccountsResponseBody.userContext;
    }



    /*
        Getting Balances
    */
    for (let account of accounts) {

        // intentionally duplicated code for simplicity

        const getBalancesRequestBody = {
            connectorId,
            userContext,
            bankSettings
        }


        const getBalancesResponse = await fetch(baseUrl + "/Ais/accounts/" + account.id + "/balances", {
            method: 'post',
            body: JSON.stringify(getBalancesRequestBody),
            headers: {'Content-Type': 'application/json'}
        });
        
        const getBalancesResponseBody = await getBalancesResponse.json();
        console.log("balanceResponse: " + JSON.stringify(getBalancesResponseBody))

        if (getBalancesResponseBody.userContext != null) {
            // not null means has changed, so you should save it for later reuse
            userContext = getBalancesResponseBody.getUserContext();
        }
    }




     /*
        Getting Transactions
    */
    for (let account of accounts) {
        // intentionally duplicated code for simplicity
        const  transactionsFirstRequestBody = {
            limit: 1,
            connectorId,
            userContext,
            bankSettings
        }

        const transactionsFirstResponse = await fetch(baseUrl + "/Ais/accounts/" + account.id + "/transactions", {
            method: 'post',
            body: JSON.stringify(transactionsFirstRequestBody),
            headers: {'Content-Type': 'application/json'}
        });

        const transactionsFirstResponseBody = await transactionsFirstResponse.json()
        console.log("transactions on account " + account.id + " : " + JSON.stringify(transactionsFirstResponseBody))

        // get and save the user context which may have been modified
        if (transactionsFirstResponseBody.userContext) {
            // not null means has changed, so you should save it for later reuse
            userContext = transactionsFirstResponseBody.userContext;
        }

        let pagerContext = transactionsFirstResponseBody.pagerContext;
        let isLastPage = transactionsFirstResponseBody.isLastPage
        while (!isLastPage) {

            const  transactionsRequestBody = {
                connectorId,
                userContext,
                bankSettings,
                pagerContext
            }

            const transactionsResponse = await fetch(baseUrl + "/Ais/accounts/" + account.id + "/transactions/next", {
                method: 'post',
                body: JSON.stringify(transactionsRequestBody),
                headers: {'Content-Type': 'application/json'}
            });
            
            const transactionsResponseBody = await transactionsResponse.json();
            
            console.log("transactions on account " + account.id + " (other page): " + JSON.stringify(transactionsResponseBody))

            if (transactionsResponseBody.userContext) {
                // not null means has changed, so you should save it for later reuse
                userContext = transactionsResponseBody.userContext;
            }
            // save the page context for next iteration if any
            isLastPage = transactionsResponseBody.isLastPage;
        }
    }


    /*
        Payment Initiation Request
        In sandbox mode, don't expect the transaction to correspond to the data you gave, bank's sandbox often
        request the payment data to be fixed data... :'(
        Each PI requires a SCA by the PSU even if you have a consent for AIS.
    */
    if (accounts.length > 0) {
        const debtorAccount = accounts[0]
        console.log("Initiating payment");
        const paymentRequestPayload = {
            connectorId,
            bankSettings,
            userContext,
            paymentInitiationRequest: {
                recipient: {
                    name: 'Other business',
                    iban: "AT861921125678901234"
                },
                debtor: {
                    name: debtorAccount.description,
                    iban: debtorAccount.iban,
                    currency: debtorAccount.currency
                },
                psuIp: "10.0.0.1",
                redirectUrl: scaCallBackUrl,
                endToEndId: uuidv4().replace(/-/g, ''),
                requestedExecutionDate: (new Date()).toISOString(),
                amount: 1, // in cent
                currency: 'EUR',
                flowId: flowId.toString(),
                remittanceInformationUnstructured: "Test"
            }
        }

        const paymentRequestReponse = await fetch(baseUrl + "/Pis/payment", {
            method: 'post',
            body: JSON.stringify(paymentRequestPayload),
            headers: {'Content-Type': 'application/json'}
        });

        const paymentRequestReponseBody = await paymentRequestReponse.json()
        
        if (paymentRequestReponseBody.userContext) {
            // not null means has changed, so you should save it for later reuse
            userContext = paymentRequestReponseBody.userContext;
        }
        // here you should save the flow context with the flowid defined above
        flow = paymentRequestReponseBody.flowContext;

        console.log("Payment Initiation requested !");

        /*
            SCA REDIRECTION
            This must be handled by your app
        */
        console.log("We've got a SCA redirection URL : " + paymentRequestReponseBody.redirectUrl);
        const queryString = await askQuestion("Paste the querystring where the bank redirects you:");

        /*
            Getting back the flowid from the queryString received from the bank
        */
        const flowIdResponse = await fetch(baseUrl + "/Ais/access/findFlowId?queryString=" + encodeURIComponent(queryString), {
            method: 'get',
            headers: {'accept': 'text/plain'}
        });
        
        
        flowId = await flowIdResponse.text() // In this sample of course the same as what we started with
   
        // With this flowid you should be able the retrieve the flow context string from your database
        // Here we use the flow we receive from previous call
        flow = paymentRequestReponseBody.flowContext;

        /*
            Finalizing the payment
        */

        console.log("Finalizing the payment");
        const finalizePaymentPayload = {
            flow,
            queryString,
            userContext,
            bankSettings
        }

        const finalizePaymentResponse = await fetch(baseUrl + "/Pis/payment/finalize", {
            method: 'post',
            body: JSON.stringify(finalizePaymentPayload),
            headers: {'Content-Type': 'application/json'}
        });
        

        const finalizePaymentResponseText = await finalizePaymentResponse.text();
        console.log(finalizePaymentResponseText)
    }



})();

function askQuestion(query) {
    const rl = readline.createInterface({
        input: process.stdin,
        output: process.stdout,
    });

    return new Promise(resolve => rl.question(query, ans => {
        rl.close();
        resolve(ans);
    }))
}


