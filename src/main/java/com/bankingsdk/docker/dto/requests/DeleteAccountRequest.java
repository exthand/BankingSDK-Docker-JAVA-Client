package com.bankingsdk.docker.dto.requests;

public class DeleteAccountRequest extends SimpleRequest {
    private String id;

    public String getId() {
        return id;
    }

    public DeleteAccountRequest setId(String id) {
        this.id = id;
        return this;
    }

    @Override
    public int getConnectorId() {
        return super.getConnectorId();
    }

    @Override
    public DeleteAccountRequest setConnectorId(int connectorId) {
        super.setConnectorId(connectorId);

        return this;
    }

    @Override
    public String getUserContext() {
        return super.getUserContext();
    }

    @Override
    public DeleteAccountRequest setUserContext(String userContext) {
        super.setUserContext(userContext);

        return this;
    }

    @Override
    public BankSettingsRequest getBankSettings() {
        return super.getBankSettings();
    }

    @Override
    public DeleteAccountRequest setBankSettings(BankSettingsRequest bankSettings) {
        super.setBankSettings(bankSettings);

        return this;
    }

    @Override
    public TppContext getTppContext() {
        return super.getTppContext();
    }

    @Override
    public DeleteAccountRequest setTppContext(TppContext tppContext) {
        super.setTppContext(tppContext);

        return this;
    }
}
