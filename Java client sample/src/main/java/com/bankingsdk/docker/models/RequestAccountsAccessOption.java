package com.bankingsdk.docker.models;

public enum RequestAccountsAccessOption {
    MULTIPLE_ACCOUNTS,
    NO_ACCOUNTS,
    SINGLE_ACCOUNT;

    public static RequestAccountsAccessOption fromOrdinal(int n) {
        if ((n < 0) || (n > (values().length-1))) {
            throw new IndexOutOfBoundsException();
        }

        return values()[n];
    }
}
