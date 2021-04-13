package com.bankingsdk.docker.models;

import com.bankingsdk.docker.models.enums.PaymentStatusISO20022;

public class PaymentStatus {
    private BankAccount Debtor;
    private String CreditorName;
    private BankAccount Creditor;
    private BankAccountInstructedAmount Amount;
    private PaymentStatusISO20022 Status;
    private String EndToEndIdentification;

    public BankAccount getDebtor() {
        return Debtor;
    }

    public void setDebtor(BankAccount debtor) {
        Debtor = debtor;
    }

    public String getCreditorName() {
        return CreditorName;
    }

    public void setCreditorName(String creditorName) {
        CreditorName = creditorName;
    }

    public BankAccount getCreditor() {
        return Creditor;
    }

    public void setCreditor(BankAccount creditor) {
        Creditor = creditor;
    }

    public BankAccountInstructedAmount getAmount() {
        return Amount;
    }

    public void setAmount(BankAccountInstructedAmount amount) {
        Amount = amount;
    }

    public PaymentStatusISO20022 getStatus() {
        return Status;
    }

    public void setStatus(PaymentStatusISO20022 status) {
        Status = status;
    }

    public String getEndToEndIdentification() {
        return EndToEndIdentification;
    }

    public void setEndToEndIdentification(String endToEndIdentification) {
        EndToEndIdentification = endToEndIdentification;
    }
}