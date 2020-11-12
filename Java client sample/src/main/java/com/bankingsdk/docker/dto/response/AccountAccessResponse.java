package com.bankingsdk.docker.dto.response;

public class AccountAccessResponse {
    private String redirectUrl;
    private String flowContext;

    public String getRedirectUrl() {
        return redirectUrl;
    }

    public void setRedirectUrl(String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }

    public String getFlowContext() {
        return flowContext;
    }

    public void setFlowContext(String flowContext) {
        this.flowContext = flowContext;
    }
}
