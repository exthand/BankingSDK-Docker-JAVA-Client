package com.bankingsdk.docker.dto.requests;

public class PaymentRequest {
    private PaymentInitiationRequest paymentInitiationRequest;
    private int connectorId = 0;
    private BankSettingsRequest bankSettings;
    private String userContext;

    public int getConnectorId() {
        return connectorId;
    }

    public PaymentRequest setConnectorId(int connectorId) {
        this.connectorId = connectorId;
        return this;
    }

    public BankSettingsRequest getBankSettings() {
        return bankSettings;
    }

    public PaymentRequest setBankSettings(BankSettingsRequest bankSettings) {
        this.bankSettings = bankSettings;
        return this;
    }

    public String getUserContext() {
        return userContext;
    }

    public PaymentRequest setUserContext(String userContext) {
        this.userContext = userContext;
        return this;
    }

    public PaymentInitiationRequest getPaymentInitiationRequest() {
        return paymentInitiationRequest;
    }

    public PaymentRequest setPaymentInitiationRequest(PaymentInitiationRequest paymentInitiationRequest) {
        this.paymentInitiationRequest = paymentInitiationRequest;
        return this;
    }
}
