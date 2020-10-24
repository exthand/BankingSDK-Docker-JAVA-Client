package com.bankingsdk.docker.models;

import java.util.Calendar;
import java.util.List;

public class BankTransaction {
    private String id;
    private double amount; // TODO: check if not instructed amount
    private String counterpartName;
    private String counterpartReference;
    private String description;
    private Calendar executionDate;
    private Calendar valueDate;

    public String getId() {
        return id;
    }

    public BankTransaction setId(String id) {
        this.id = id;
        return this;
    }

    public double getAmount() {
        return amount;
    }

    public BankTransaction setAmount(double amount) {
        this.amount = amount;
        return this;
    }

    public String getCounterpartName() {
        return counterpartName;
    }

    public BankTransaction setCounterpartName(String counterpartName) {
        this.counterpartName = counterpartName;
        return this;
    }

    public String getCounterpartReference() {
        return counterpartReference;
    }

    public BankTransaction setCounterpartReference(String counterpartReference) {
        this.counterpartReference = counterpartReference;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public BankTransaction setDescription(String description) {
        this.description = description;
        return this;
    }

    public Calendar getExecutionDate() {
        return executionDate;
    }

    public BankTransaction setExecutionDate(Calendar executionDate) {
        this.executionDate = executionDate;
        return this;
    }

    public Calendar getValueDate() {
        return valueDate;
    }

    public BankTransaction setValueDate(Calendar valueDate) {
        this.valueDate = valueDate;
        return this;
    }
}