package com.bankingsdk.docker.dto.requests;

public class AccountAccessFinalizeRequest {
    private String flow = "";
    private String queryString = "";
    private String userContext = "";
    BankSettingsRequest bankSettings;

    public String getFlow() {
        return flow;
    }

    public AccountAccessFinalizeRequest setFlow(String flow) {
        this.flow = flow;
        return this;
    }

    public String getQueryString() {
        return queryString;
    }

    public AccountAccessFinalizeRequest setQueryString(String queryString) {
        this.queryString = queryString;
        return this;
    }

    public String getUserContext() {
        return userContext;
    }

    public AccountAccessFinalizeRequest setUserContext(String userContext) {
        this.userContext = userContext;
        return this;
    }

    public BankSettingsRequest getBankSettings() {
        return bankSettings;
    }

    public AccountAccessFinalizeRequest setBankSettings(BankSettingsRequest bankSettings) {
        this.bankSettings = bankSettings;
        return this;
    }
}
