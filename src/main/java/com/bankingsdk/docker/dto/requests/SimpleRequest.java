package com.bankingsdk.docker.dto.requests;

public class SimpleRequest {
    private int connectorId = 0;
    private String userContext = "";
    private BankSettingsRequest bankSettings;

    public int getConnectorId() {
        return connectorId;
    }

    public SimpleRequest setConnectorId(int connectorId) {
        this.connectorId = connectorId;
        return this;
    }

    public String getUserContext() {
        return userContext;
    }

    public SimpleRequest setUserContext(String userContext) {
        this.userContext = userContext;
        return this;
    }

    public BankSettingsRequest getBankSettings() {
        return bankSettings;
    }

    public SimpleRequest setBankSettings(BankSettingsRequest bankSettings) {
        this.bankSettings = bankSettings;
        return this;
    }
}
