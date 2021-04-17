package com.bankingsdk.docker.models;

import com.bankingsdk.docker.models.enums.PaymentStatusISO20022;

public class PaymentStatus {
    private String paymentId;
    private BankAccount debtor;
    private String creditorName;
    private BankAccount creditor;
    private BankAccountInstructedAmount amount;
    private PaymentStatusISO20022 status;
    private String statusCodeRaw;
    private String endToEndIdentification;

    public String getPaymentId() {
        return paymentId;
    }

    public PaymentStatus setPaymentId(String paymentId) {
        this.paymentId = paymentId;
        return this;
    }

    public BankAccount getDebtor() {
        return debtor;
    }

    public void setDebtor(BankAccount debtor) {
        this.debtor = debtor;
    }

    public String getCreditorName() {
        return creditorName;
    }

    public void setCreditorName(String creditorName) {
        this.creditorName = creditorName;
    }

    public BankAccount getCreditor() {
        return creditor;
    }

    public void setCreditor(BankAccount creditor) {
        this.creditor = creditor;
    }

    public BankAccountInstructedAmount getAmount() {
        return amount;
    }

    public void setAmount(BankAccountInstructedAmount amount) {
        this.amount = amount;
    }

    public PaymentStatusISO20022 getStatus() {
        return status;
    }

    public void setStatus(PaymentStatusISO20022 status) {
        this.status = status;
    }

    public String getStatusCodeRaw() {
        return statusCodeRaw;
    }

    public PaymentStatus setStatusCodeRaw(String statusCodeRaw) {
        this.statusCodeRaw = statusCodeRaw;
        return this;
    }

    public String getEndToEndIdentification() {
        return endToEndIdentification;
    }

    public void setEndToEndIdentification(String endToEndIdentification) {
        this.endToEndIdentification = endToEndIdentification;
    }
}