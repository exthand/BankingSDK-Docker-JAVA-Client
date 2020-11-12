package com.bankingsdk.docker.dto.response;

import com.bankingsdk.docker.models.BankAccount;

import java.util.List;

public class AccountsResponse {
    private List<BankAccount> accounts;
    private String userContext;

    public List<BankAccount> getAccounts() {
        return accounts;
    }

    public void setAccounts(List<BankAccount> bankAccounts) {
        this.accounts = bankAccounts;
    }

    public String getUserContext() {
        return userContext;
    }

    public void setUserContext(String userContext) {
        this.userContext = userContext;
    }
}
