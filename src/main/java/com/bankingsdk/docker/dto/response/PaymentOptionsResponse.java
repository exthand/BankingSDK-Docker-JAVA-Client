package com.bankingsdk.docker.dto.response;

import com.bankingsdk.docker.models.TransferOptions;

public class PaymentOptionsResponse {
    private int DebtorIban;
    private TransferOptions sepaCreditTransfers = new TransferOptions();
    private TransferOptions instantSepaCreditTransfers = new TransferOptions();
    private TransferOptions crossborderPayments = new TransferOptions();
    private TransferOptions target2Payment = new TransferOptions();

    public int getDebtorIban() {
        return DebtorIban;
    }

    public void setDebtorIban(int debtorIban) {
        DebtorIban = debtorIban;
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
}
