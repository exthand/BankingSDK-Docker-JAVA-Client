package com.bankingsdk.docker.models;

public class SupportOptions {
    private boolean Supported;
    private boolean CancelSupported;

    public boolean isSupported() {
        return Supported;
    }

    public void setSupported(boolean supported) {
        Supported = supported;
    }

    public boolean isCancelSupported() {
        return CancelSupported;
    }

    public void setCancelSupported(boolean cancelSupported) {
        CancelSupported = cancelSupported;
    }
}
