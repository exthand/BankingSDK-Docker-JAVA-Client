package com.bankingsdk.docker.models;

public class BankAccount {
    private String id;
    private String currency;
    private String iban;
    private String description;
    private BankConsent transactionsConsent;
    private BankConsent balancesConsent;
    private String userContext;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getIban() {
        return iban;
    }

    public void setIban(String iban) {
        this.iban = iban;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BankConsent getTransactionsConsent() {
        return transactionsConsent;
    }

    public void setTransactionsConsent(BankConsent transactionsConsent) {
        this.transactionsConsent = transactionsConsent;
    }

    public BankConsent getBalancesConsent() {
        return balancesConsent;
    }

    public void setBalancesConsent(BankConsent balancesConsent) {
        this.balancesConsent = balancesConsent;
    }

    public String getUserContext() {
        return userContext;
    }

    public void setUserContext(String userContext) {
        this.userContext = userContext;
    }
}
