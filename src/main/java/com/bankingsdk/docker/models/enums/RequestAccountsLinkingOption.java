package com.bankingsdk.docker.models.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum RequestAccountsLinkingOption {
    LINKED,
    SEPARATE;

    public static RequestAccountsLinkingOption fromOrdinal(int n) {
        if ((n < 0) || (n > (values().length-1))) {
            throw new IndexOutOfBoundsException();
        }

        return values()[n];
    }

    @JsonValue
    public int toValue() {
        return ordinal();
    }
}
