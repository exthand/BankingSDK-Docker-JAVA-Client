package com.bankingsdk.docker.dto.requests;

public class PaymentStatusRequest {
    private int connectorId;
    private String paymentId;
    private String userContext;
    private BankSettingsRequest bankSettings;
    private TppContext tppContext;

    public int getConnectorId() {
        return connectorId;
    }

    public PaymentStatusRequest setConnectorId(int connectorId) {
        this.connectorId = connectorId;
        return this;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public PaymentStatusRequest setPaymentId(String paymentId) {
        this.paymentId = paymentId;
        return this;
    }

    public String getUserContext() {
        return userContext;
    }

    public PaymentStatusRequest setUserContext(String userContext) {
        this.userContext = userContext;
        return this;
    }

    public BankSettingsRequest getBankSettings() {
        return bankSettings;
    }

    public PaymentStatusRequest setBankSettings(BankSettingsRequest bankSettings) {
        this.bankSettings = bankSettings;
        return this;
    }

    public TppContext getTppContext() {
        return tppContext;
    }

    public PaymentStatusRequest setTppContext(TppContext tppContext) {
        this.tppContext = tppContext;
        return this;
    }
}
