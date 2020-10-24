package com.bankingsdk.docker.models;

import java.util.Calendar;

public class BankBalance {
    private String balanceType;
    private Calendar referenceDate;
    private Calendar lastChangeDateTime;
    private BankAccountInstructedAmount balanceAmount;

    public String getBalanceType() {
        return balanceType;
    }

    public void setBalanceType(String balanceType) {
        this.balanceType = balanceType;
    }

    public Calendar getReferenceDate() {
        return referenceDate;
    }

    public void setReferenceDate(Calendar referenceDate) {
        this.referenceDate = referenceDate;
    }

    public Calendar getLastChangeDateTime() {
        return lastChangeDateTime;
    }

    public void setLastChangeDateTime(Calendar lastChangeDateTime) {
        this.lastChangeDateTime = lastChangeDateTime;
    }

    public BankAccountInstructedAmount getBalanceAmount() {
        return balanceAmount;
    }

    public void setBalanceAmount(BankAccountInstructedAmount balanceAmount) {
        this.balanceAmount = balanceAmount;
    }
}
