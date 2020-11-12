package com.bankingsdk.docker.dto.requests;

public class AccountAccessRequest {
    private int connectorId = 0;
    private String userContext = "";
    private BankSettingsRequest bankSettings;
    private AccountAccessInnerRequest accountsAccessRequest;

    public int getConnectorId() {
        return connectorId;
    }

    public AccountAccessRequest setConnectorId(int connectorId) {
        this.connectorId = connectorId;
        return this;
    }

    public String getUserContext() {
        return userContext;
    }

    public AccountAccessRequest setUserContext(String userContext) {
        this.userContext = userContext;
        return this;
    }

    public BankSettingsRequest getBankSettings() {
        return bankSettings;
    }

    public AccountAccessRequest setBankSettings(BankSettingsRequest bankSettings) {
        this.bankSettings = bankSettings;
        return this;
    }

    public AccountAccessInnerRequest getAccountsAccessRequest() {
        return accountsAccessRequest;
    }

    public AccountAccessRequest setAccountsAccessRequest(AccountAccessInnerRequest accountsAccessRequest) {
        this.accountsAccessRequest = accountsAccessRequest;
        return this;
    }
}
