package com.bankingsdk.docker.models;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Calendar;

public class BankConsent {
    private String consentId;
    private Calendar validUntil;

    public String getConsentId() {
        return consentId;
    }

    public void setConsentId(String consentId) {
        this.consentId = consentId;
    }

    public Calendar getValidUntil() {
        return validUntil;
    }

    public void setValidUntil(Calendar validUntil) {
        this.validUntil = validUntil;
    }
}
