package com.bankingsdk.docker.dto.requests;

public class PaymentRecipient {
    private String name;
    private String iban;

    public String getName() {
        return name;
    }

    public PaymentRecipient setName(String name) {
        this.name = name;
        return this;
    }

    public String getIban() {
        return iban;
    }

    public PaymentRecipient setIban(String iban) {
        this.iban = iban;
        return this;
    }
}
