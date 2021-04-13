package com.bankingsdk.docker.models;

public class TransferOptions {
    private SupportOptions SinglePayments = new SupportOptions();
    private SupportOptions PeriodicPayments = new SupportOptions();
    private SupportOptions BulkPayments = new SupportOptions();

    public SupportOptions getSinglePayments() {
        return SinglePayments;
    }

    public void setSinglePayments(SupportOptions singlePayments) {
        SinglePayments = singlePayments;
    }

    public SupportOptions getPeriodicPayments() {
        return PeriodicPayments;
    }

    public void setPeriodicPayments(SupportOptions periodicPayments) {
        PeriodicPayments = periodicPayments;
    }

    public SupportOptions getBulkPayments() {
        return BulkPayments;
    }

    public void setBulkPayments(SupportOptions bulkPayments) {
        BulkPayments = bulkPayments;
    }
}
