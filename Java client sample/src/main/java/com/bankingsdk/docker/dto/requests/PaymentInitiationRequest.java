package com.bankingsdk.docker.dto.requests;

import java.util.Calendar;

public class PaymentInitiationRequest {
    private PaymentRecipient recipient;
    private PaymentDebtor debtor;
    private String psuIp;
    private String redirectUrl;
    private String endToEndId;
    private Calendar requestedExecutionDate;
    private double amount;
    private String currency;
    private String flowId;
    private String remittanceInformationUnstructured;

    public PaymentRecipient getRecipient() {
        return recipient;
    }

    public PaymentInitiationRequest setRecipient(PaymentRecipient recipient) {
        this.recipient = recipient;
        return this;
    }

    public PaymentDebtor getDebtor() {
        return debtor;
    }

    public PaymentInitiationRequest setDebtor(PaymentDebtor debtor) {
        this.debtor = debtor;
        return this;
    }

    public String getPsuIp() {
        return psuIp;
    }

    public PaymentInitiationRequest setPsuIp(String psuIp) {
        this.psuIp = psuIp;
        return this;
    }

    public String getRedirectUrl() {
        return redirectUrl;
    }

    public PaymentInitiationRequest setRedirectUrl(String redirectUrl) {
        this.redirectUrl = redirectUrl;
        return this;
    }

    public String getEndToEndId() {
        return endToEndId;
    }

    public PaymentInitiationRequest setEndToEndId(String endToEndId) {
        this.endToEndId = endToEndId;
        return this;
    }

    public Calendar getRequestedExecutionDate() {
        return requestedExecutionDate;
    }

    public PaymentInitiationRequest setRequestedExecutionDate(Calendar requestedExecutionDate) {
        this.requestedExecutionDate = requestedExecutionDate;
        return this;
    }

    public double getAmount() {
        return amount;
    }

    public PaymentInitiationRequest setAmount(double amount) {
        this.amount = amount;
        return this;
    }

    public String getCurrency() {
        return currency;
    }

    public PaymentInitiationRequest setCurrency(String currency) {
        this.currency = currency;
        return this;
    }

    public String getFlowId() {
        return flowId;
    }

    public PaymentInitiationRequest setFlowId(String flowId) {
        this.flowId = flowId;
        return this;
    }

    public String getRemittanceInformationUnstructured() {
        return remittanceInformationUnstructured;
    }

    public PaymentInitiationRequest setRemittanceInformationUnstructured(String remittanceInformationUnstructured) {
        this.remittanceInformationUnstructured = remittanceInformationUnstructured;
        return this;
    }
}
