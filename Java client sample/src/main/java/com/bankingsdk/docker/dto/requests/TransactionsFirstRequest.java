package com.bankingsdk.docker.dto.requests;

public class TransactionsFirstRequest extends SimpleRequest {
    private int limit;

    public int getLimit() {
        return limit;
    }

    public TransactionsFirstRequest setLimit(int limit) {
        this.limit = limit;
        return this;
    }

    @Override
    public TransactionsFirstRequest setConnectorId(int connectorId) {
        super.setConnectorId(connectorId);

        return this;
    }

    @Override
    public TransactionsFirstRequest setUserContext(String userContext) {
        super.setUserContext(userContext);

        return this;
    }

    @Override
    public TransactionsFirstRequest setBankSettings(BankSettingsRequest bankSettings) {
        super.setBankSettings(bankSettings);

        return this;
    }
}
