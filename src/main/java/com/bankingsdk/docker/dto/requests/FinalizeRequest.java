package com.bankingsdk.docker.dto.requests;

public class FinalizeRequest {
    private String flow = "";
    private String queryString = "";
    private String userContext = "";
    BankSettingsRequest bankSettings;
    private TppContext tppContext;

    public String getFlow() {
        return flow;
    }

    public FinalizeRequest setFlow(String flow) {
        this.flow = flow;
        return this;
    }

    public String getQueryString() {
        return queryString;
    }

    public FinalizeRequest setQueryString(String queryString) {
        this.queryString = queryString;
        return this;
    }

    public String getUserContext() {
        return userContext;
    }

    public FinalizeRequest setUserContext(String userContext) {
        this.userContext = userContext;
        return this;
    }

    public BankSettingsRequest getBankSettings() {
        return bankSettings;
    }

    public FinalizeRequest setBankSettings(BankSettingsRequest bankSettings) {
        this.bankSettings = bankSettings;
        return this;
    }

    public TppContext getTppContext() {
        return tppContext;
    }

    public FinalizeRequest setTppContext(TppContext tppContext) {
        this.tppContext = tppContext;
        return this;
    }
}
