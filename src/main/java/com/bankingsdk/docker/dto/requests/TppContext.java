package com.bankingsdk.docker.dto.requests;

public class TppContext
{
    private String tppId;
    private String app;
    private String flow;
    private String transaction;
    private String unit;

    public String getTppId() {
        return tppId;
    }

    public TppContext setTppId(String tppId) {
        this.tppId = tppId;
        return this;
    }

    public String getApp() {
        return app;
    }

    public TppContext setApp(String app) {
        this.app = app;
        return this;
    }

    public String getFlow() {
        return flow;
    }

    public TppContext setFlow(String flow) {
        this.flow = flow;
        return this;
    }

    public String getTransaction() {
        return transaction;
    }

    public TppContext setTransaction(String transaction) {
        this.transaction = transaction;
        return this;
    }

    public String getUnit() {
        return unit;
    }

    public TppContext setUnit(String unit) {
        this.unit = unit;
        return this;
    }
}
