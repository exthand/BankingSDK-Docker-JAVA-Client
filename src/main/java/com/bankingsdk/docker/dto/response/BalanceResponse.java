package com.bankingsdk.docker.dto.response;

import com.bankingsdk.docker.models.BankBalance;

import java.util.List;

public class BalanceResponse {
    private List<BankBalance> balances;
    private String userContext;

    public List<BankBalance> getBalances() {
        return balances;
    }

    public void setBalances(List<BankBalance> balances) {
        this.balances = balances;
    }

    public String getUserContext() {
        return userContext;
    }

    public void setUserContext(String userContext) {
        this.userContext = userContext;
    }
}
