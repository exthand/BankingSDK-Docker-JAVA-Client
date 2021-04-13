package com.bankingsdk.docker.models.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum PaymentStatusISO20022 {
    /// <summary>
    /// AcceptedSettlementCompleted
    /// </summary>
    ACCC,
    /// <summary>
    /// AcceptedCustomerProfile
    /// </summary>
    ACCP,
    /// <summary>
    /// AcceptedFundsChecked
    /// </summary>
    ACFC,
    /// <summary>
    /// AcceptedSettlementCompleted
    /// </summary>
    ACSC,
    /// <summary>
    /// AcceptedSettlementInProcess
    /// </summary>
    ACSP,
    /// <summary>
    /// AcceptedTechnicalValidation
    /// </summary>
    ACTC,
    /// <summary>
    /// AcceptedWithChange
    /// </summary>
    ACWC,
    /// <summary>
    /// AcceptedWithoutPosting
    /// </summary>
    ACWP,
    /// <summary>
    /// Cancelled
    /// </summary>
    CANC,
    /// <summary>
    /// PartiallyAcceptedTechnicalCorrect
    /// </summary>
    PATC,
    /// <summary>
    /// Pending
    /// </summary>
    PDNG,
    /// <summary>
    /// Presented
    /// </summary>
    PRES,
    /// <summary>
    /// Received
    /// </summary>
    RCVD,
    /// <summary>
    /// Rejected
    /// </summary>
    RJCT,
    /// <summary>
    /// Unknown
    /// </summary>
    UNKN;

    public static PaymentStatusISO20022 fromOrdinal(int n) {
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
