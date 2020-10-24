package com.bankingsdk.docker.models;

public enum BankConnectorOptions {
    MULTIPLE_ACCOUNTS,
    NO_ACCOUNTS,
    SINGLE_ACCOUNT,
    UNKNOWN;

    public static BankConnectorOptions fromOrdinal(int n) {
        if ((n < 0) || (n > (values().length-1))) {
            return UNKNOWN;
        }

        return values()[n];
    }
}
