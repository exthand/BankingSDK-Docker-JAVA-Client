package com.bankingsdk.docker.models;

public class TransferOptions {
    private SupportOptions singlePayments = new SupportOptions();
    private SupportOptions periodicPayments = new SupportOptions();
    private SupportOptions bulkPayments = new SupportOptions();

    public SupportOptions getSinglePayments() {
        return singlePayments;
    }

    public void setSinglePayments(SupportOptions singlePayments) {
        this.singlePayments = singlePayments;
    }

    public SupportOptions getPeriodicPayments() {
        return periodicPayments;
    }

    public void setPeriodicPayments(SupportOptions periodicPayments) {
        this.periodicPayments = periodicPayments;
    }

    public SupportOptions getBulkPayments() {
        return bulkPayments;
    }

    public void setBulkPayments(SupportOptions bulkPayments) {
        this.bulkPayments = bulkPayments;
    }
}
