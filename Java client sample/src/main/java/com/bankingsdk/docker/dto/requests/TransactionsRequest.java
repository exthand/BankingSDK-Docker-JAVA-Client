package com.bankingsdk.docker.dto.requests;

public class TransactionsRequest extends SimpleRequest {
    private String pagerContext;

    public String getPagerContext() {
        return pagerContext;
    }

    public TransactionsRequest setPagerContext(String pagerContext) {
        this.pagerContext = pagerContext;
        return this;
    }

    @Override
    public TransactionsRequest setConnectorId(int connectorId) {
        super.setConnectorId(connectorId);

        return this;
    }

    @Override
    public TransactionsRequest setUserContext(String userContext) {
        super.setUserContext(userContext);

        return this;
    }

    @Override
    public TransactionsRequest setBankSettings(BankSettingsRequest bankSettings) {
        super.setBankSettings(bankSettings);

        return this;
    }
}
