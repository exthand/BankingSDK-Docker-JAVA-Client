package com.bankingsdk.docker.models;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Calendar;

public class BankConsent {
    private String consentId;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSSSSXXX")
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

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSSSSXXX")
    public void setValidUntil(Calendar validUntil) {
        this.validUntil = validUntil;
    }
}
