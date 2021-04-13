package com.bankingsdk.docker.models.enums;

public enum PaymentRequestDebtorIbanOption {
    REQUIRED,
    OPTIONAL;

    public static PaymentRequestDebtorIbanOption fromOrdinal(int n) {
        if ((n < 0) || (n > (values().length-1))) {
            throw new IndexOutOfBoundsException();
        }

        return values()[n];
    }
}
