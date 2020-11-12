package com.bankingsdk.docker.dto.response;

public class PaymentInitiationResponse {
    private String redirectUrl;
    private String flowContext;
    private String userContext;

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

    public String getUserContext() {
        return userContext;
    }

    public void setUserContext(String userContext) {
        this.userContext = userContext;
    }
}
