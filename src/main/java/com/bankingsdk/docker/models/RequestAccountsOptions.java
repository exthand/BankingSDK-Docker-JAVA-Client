package com.bankingsdk.docker.models;

import com.bankingsdk.docker.models.enums.RequestAccountsAccessOption;
import com.bankingsdk.docker.models.enums.RequestAccountsCurrencyOption;
import com.bankingsdk.docker.models.enums.RequestAccountsLinkingOption;

public class RequestAccountsOptions {
    private RequestAccountsAccessOption accessOption;
    private RequestAccountsCurrencyOption currencyOption;
    private RequestAccountsLinkingOption linkingOption;

    public RequestAccountsAccessOption getAccessOption() {
        return accessOption;
    }

    public void setAccessOption(RequestAccountsAccessOption accessOption) {
        this.accessOption = accessOption;
    }

    public RequestAccountsCurrencyOption getCurrencyOption() {
        return currencyOption;
    }

    public void setCurrencyOption(RequestAccountsCurrencyOption currencyOption) {
        this.currencyOption = currencyOption;
    }

    public RequestAccountsLinkingOption getLinkingOption() {
        return linkingOption;
    }

    public void setLinkingOption(RequestAccountsLinkingOption linkingOption) {
        this.linkingOption = linkingOption;
    }

    public RequestAccountsOptions() {
    }

    public RequestAccountsOptions(RequestAccountsAccessOption accessOption, RequestAccountsCurrencyOption currencyOption, RequestAccountsLinkingOption linkingOption) {
        this.accessOption = accessOption;
        this.currencyOption = currencyOption;
        this.linkingOption = linkingOption;
    }
}
