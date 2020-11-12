package com.bankingsdk.docker.dto.requests;

public class PaymentDebtor {
    private String name;
    private String iban;
    private String currency;

    public String getName() {
        return name;
    }

    public PaymentDebtor setName(String name) {
        this.name = name;
        return this;
    }

    public String getIban() {
        return iban;
    }

    public PaymentDebtor setIban(String iban) {
        this.iban = iban;
        return this;
    }

    public String getCurrency() {
        return currency;
    }

    public PaymentDebtor setCurrency(String currency) {
        this.currency = currency;
        return this;
    }
}
