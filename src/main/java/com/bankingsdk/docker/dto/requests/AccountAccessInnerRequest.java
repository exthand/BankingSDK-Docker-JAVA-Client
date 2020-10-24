package com.bankingsdk.docker.dto.requests;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AccountAccessInnerRequest {
    private String flowId = UUID.randomUUID().toString();
    private String redirectUrl = "";
    private int frequencyPerDay = 4;
    private String psuIp = "";
    private String singleAccount;
    private List<String> transactionAccounts = new ArrayList<>();
    private List<String> balanceAccounts = new ArrayList<>();

    public AccountAccessInnerRequest() {

    }

    public String getFlowId() {
        return flowId;
    }

    public AccountAccessInnerRequest setFlowId(String flowId) {
        this.flowId = flowId;
        return this;
    }

    public String getRedirectUrl() {
        return redirectUrl;
    }

    public AccountAccessInnerRequest setRedirectUrl(String redirectUrl) {
        this.redirectUrl = redirectUrl;
        return this;
    }

    public int getFrequencyPerDay() {
        return frequencyPerDay;
    }

    public AccountAccessInnerRequest setFrequencyPerDay(int frequencyPerDay) {
        this.frequencyPerDay = frequencyPerDay;
        return this;
    }

    public String getPsuIp() {
        return psuIp;
    }

    public AccountAccessInnerRequest setPsuIp(String psuIp) {
        this.psuIp = psuIp;
        return this;
    }

    public String getSingleAccount() {
        return singleAccount;
    }

    public AccountAccessInnerRequest setSingleAccount(String singleAccount) {
        this.singleAccount = singleAccount;
        return this;
    }

    public List<String> getTransactionAccounts() {
        return transactionAccounts;
    }

    public AccountAccessInnerRequest setTransactionAccounts(List<String> transactionAccounts) {
        this.transactionAccounts = transactionAccounts;
        return this;
    }

    public List<String> getBalanceAccounts() {
        return balanceAccounts;
    }

    public AccountAccessInnerRequest setBalanceAccounts(List<String> balanceAccounts) {
        this.balanceAccounts = balanceAccounts;
        return this;
    }
}
