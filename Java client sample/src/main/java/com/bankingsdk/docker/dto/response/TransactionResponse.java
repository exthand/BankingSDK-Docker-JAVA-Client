package com.bankingsdk.docker.dto.response;

import com.bankingsdk.docker.models.BankTransaction;

import java.util.List;

public class TransactionResponse {
    private List<BankTransaction> transactions;
    private String userContext;
    // Yes, string because the structure depends on the connector. Just have to pass it through
    private String pagerContext;
    private boolean isFirstPage;
    private boolean isLastPage;

    public List<BankTransaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<BankTransaction> transactions) {
        this.transactions = transactions;
    }

    public String getUserContext() {
        return userContext;
    }

    public void setUserContext(String userContext) {
        this.userContext = userContext;
    }

    public String getPagerContext() {
        return pagerContext;
    }

    public void setPagerContext(String pagerContext) {
        this.pagerContext = pagerContext;
    }

    public boolean isFirstPage() {
        return isFirstPage;
    }

    public void setFirstPage(boolean firstPage) {
        isFirstPage = firstPage;
    }

    public boolean isLastPage() {
        return isLastPage;
    }

    public void setLastPage(boolean lastPage) {
        isLastPage = lastPage;
    }
}
