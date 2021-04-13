package com.bankingsdk.docker.dto.response;

import com.bankingsdk.docker.models.enums.ResultStatus;

import java.util.Dictionary;

public class FinalizeResponse {
    private ResultStatus ResultStatus;
    private String DataString;
    private Dictionary<String, String> Options;
    private String FlowContext;
    private String UserContext;

    public com.bankingsdk.docker.models.enums.ResultStatus getResultStatus() {
        return ResultStatus;
    }

    public void setResultStatus(com.bankingsdk.docker.models.enums.ResultStatus resultStatus) {
        ResultStatus = resultStatus;
    }

    public String getDataString() {
        return DataString;
    }

    public void setDataString(String dataString) {
        DataString = dataString;
    }

    public Dictionary<String, String> getOptions() {
        return Options;
    }

    public void setOptions(Dictionary<String, String> options) {
        Options = options;
    }

    public String getFlowContext() {
        return FlowContext;
    }

    public void setFlowContext(String flowContext) {
        FlowContext = flowContext;
    }

    public String getUserContext() {
        return UserContext;
    }

    public void setUserContext(String userContext) {
        UserContext = userContext;
    }
}
