package com.bankingsdk.docker.models.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ResultStatus {
    /// <summary>
    /// unknown means... unknown.
    /// </summary>
    UNKNOWN,
    /// <summary>
    /// everything went well, the results are avalable
    /// </summary>
    DONE,
    /// <summary>
    /// you must do a redirection to complete the flow
    /// </summary>
    REDIRECT,
    /// <summary>
    /// PSU have to authenticate in bank app
    /// </summary>
    DECOUPLED,
    /// <summary>
    /// PSU have to provide password
    /// </summary>
    PASSWORD,
    /// <summary>
    /// PSU have to provide more information
    /// </summary>
    MORE_INFO,
    /// <summary>
    /// PSU have to secect one of the provided options
    /// </summary>
    SELECT_OPTION,
    /// <summary>
    /// something went wrong with the given params, with the call to the bank or processing the response from the bank.
    /// </summary>
    ERROR;

    public static ResultStatus fromOrdinal(int n) {
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
