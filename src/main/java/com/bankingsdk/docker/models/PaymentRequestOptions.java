package com.bankingsdk.docker.models;

import com.bankingsdk.docker.models.enums.PaymentRequestDebtorIbanOption;

public class PaymentRequestOptions {
    private PaymentRequestDebtorIbanOption debtorIban;
    private TransferOptions sepaCreditTransfers = new TransferOptions();
    private TransferOptions instantSepaCreditTransfers = new TransferOptions();
    private TransferOptions crossborderPayments = new TransferOptions();
    private TransferOptions target2Payment = new TransferOptions();

    public PaymentRequestDebtorIbanOption getDebtorIban() {
        return debtorIban;
    }

    public void setDebtorIban(PaymentRequestDebtorIbanOption debtorIban) {
        this.debtorIban = debtorIban;
    }

    public TransferOptions getSepaCreditTransfers() {
        return sepaCreditTransfers;
    }

    public void setSepaCreditTransfers(TransferOptions sepaCreditTransfers) {
        this.sepaCreditTransfers = sepaCreditTransfers;
    }

    public TransferOptions getInstantSepaCreditTransfers() {
        return instantSepaCreditTransfers;
    }

    public void setInstantSepaCreditTransfers(TransferOptions instantSepaCreditTransfers) {
        this.instantSepaCreditTransfers = instantSepaCreditTransfers;
    }

    public TransferOptions getCrossborderPayments() {
        return crossborderPayments;
    }

    public void setCrossborderPayments(TransferOptions crossborderPayments) {
        this.crossborderPayments = crossborderPayments;
    }

    public TransferOptions getTarget2Payment() {
        return target2Payment;
    }

    public void setTarget2Payment(TransferOptions target2Payment) {
        this.target2Payment = target2Payment;
    }

    public PaymentRequestOptions() {
    }

    public PaymentRequestOptions(PaymentRequestDebtorIbanOption debtorIban, TransferOptions sepaCreditTransfers, TransferOptions instantSepaCreditTransfers, TransferOptions crossborderPayments, TransferOptions target2Payment) {
        this.debtorIban = debtorIban;
        this.sepaCreditTransfers = sepaCreditTransfers;
        this.instantSepaCreditTransfers = instantSepaCreditTransfers;
        this.crossborderPayments = crossborderPayments;
        this.target2Payment = target2Payment;
    }
}
