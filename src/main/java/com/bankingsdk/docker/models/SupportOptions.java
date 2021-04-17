package com.bankingsdk.docker.models;

public class SupportOptions {
    private boolean supported;
    private boolean cancelSupported;

    public boolean isSupported() {
        return supported;
    }

    public void setSupported(boolean supported) {
        this.supported = supported;
    }

    public boolean isCancelSupported() {
        return cancelSupported;
    }

    public void setCancelSupported(boolean cancelSupported) {
        this.cancelSupported = cancelSupported;
    }
}
