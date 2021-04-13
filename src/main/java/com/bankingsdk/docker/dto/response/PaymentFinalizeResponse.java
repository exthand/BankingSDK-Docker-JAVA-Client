package com.bankingsdk.docker.dto.response;

import com.bankingsdk.docker.models.PaymentStatus;

public class PaymentFinalizeResponse extends FinalizeResponse {
    private PaymentStatus paymentStatus;

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }
}
