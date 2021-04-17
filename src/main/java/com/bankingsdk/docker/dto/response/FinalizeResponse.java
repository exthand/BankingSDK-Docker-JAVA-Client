package com.bankingsdk.docker.dto.response;

import com.bankingsdk.docker.models.enums.ResultStatus;

import java.util.Dictionary;

public class FinalizeResponse {
    private ResultStatus resultStatus;
    private String dataString;
    private Dictionary<String, String> options;
    private String flowContext;
    private String userContext;

    public com.bankingsdk.docker.models.enums.ResultStatus getResultStatus() {
        return resultStatus;
    }

    public void setResultStatus(com.bankingsdk.docker.models.enums.ResultStatus resultStatus) {
        this.resultStatus = resultStatus;
    }

    public String getDataString() {
        return dataString;
    }

    public void setDataString(String dataString) {
        this.dataString = dataString;
    }

    public Dictionary<String, String> getOptions() {
        return options;
    }

    public void setOptions(Dictionary<String, String> options) {
        this.options = options;
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
