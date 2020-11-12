package com.bankingsdk.docker.dto.requests;

public class BankSettingsRequest {
    private String ncaId;
    private String appClientId;
    private String appClientSecret;
    private String tlsCertificateName;
    private String tlsCertificatePassword;
    private String signingCertificateName;
    private String signingCertificatePassword;

    public String getNcaId() {
        return ncaId;
    }

    public BankSettingsRequest setNcaId(String ncaId) {
        this.ncaId = ncaId;
        return this;
    }

    public String getAppClientId() {
        return appClientId;
    }

    public BankSettingsRequest setAppClientId(String appClientId) {
        this.appClientId = appClientId;
        return this;
    }

    public String getAppClientSecret() {
        return appClientSecret;
    }

    public BankSettingsRequest setAppClientSecret(String appClientSecret) {
        this.appClientSecret = appClientSecret;
        return this;
    }

    public String getTlsCertificateName() {
        return tlsCertificateName;
    }

    public BankSettingsRequest setTlsCertificateName(String tlsCertificateName) {
        this.tlsCertificateName = tlsCertificateName;
        return this;
    }

    public String getTlsCertificatePassword() {
        return tlsCertificatePassword;
    }

    public BankSettingsRequest setTlsCertificatePassword(String tlsCertificatePassword) {
        this.tlsCertificatePassword = tlsCertificatePassword;
        return this;
    }

    public String getSigningCertificateName() {
        return signingCertificateName;
    }

    public BankSettingsRequest setSigningCertificateName(String signingCertificateName) {
        this.signingCertificateName = signingCertificateName;
        return this;
    }

    public String getSigningCertificatePassword() {
        return signingCertificatePassword;
    }

    public BankSettingsRequest setSigningCertificatePassword(String signingCertificatePassword) {
        this.signingCertificatePassword = signingCertificatePassword;
        return this;
    }
}
