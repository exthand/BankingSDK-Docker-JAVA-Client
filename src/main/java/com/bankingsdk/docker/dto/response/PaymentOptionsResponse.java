package com.bankingsdk.docker.dto.response;

import com.bankingsdk.docker.models.TransferOptions;
import com.bankingsdk.docker.models.enums.PaymentRequestDebtorIbanOption;

public class PaymentOptionsResponse {
    private int DebtorIban;
    private TransferOptions SepaCreditTransfers = new TransferOptions();
    private TransferOptions InstantSepaCreditTransfers = new TransferOptions();
    private TransferOptions CrossborderPayments = new TransferOptions();
    private TransferOptions Target2Payment = new TransferOptions();

    public int getDebtorIban() {
        return DebtorIban;
    }

    public void setDebtorIban(int debtorIban) {
        DebtorIban = debtorIban;
    }

    public TransferOptions getSepaCreditTransfers() {
        return SepaCreditTransfers;
    }

    public void setSepaCreditTransfers(TransferOptions sepaCreditTransfers) {
        SepaCreditTransfers = sepaCreditTransfers;
    }

    public TransferOptions getInstantSepaCreditTransfers() {
        return InstantSepaCreditTransfers;
    }

    public void setInstantSepaCreditTransfers(TransferOptions instantSepaCreditTransfers) {
        InstantSepaCreditTransfers = instantSepaCreditTransfers;
    }

    public TransferOptions getCrossborderPayments() {
        return CrossborderPayments;
    }

    public void setCrossborderPayments(TransferOptions crossborderPayments) {
        CrossborderPayments = crossborderPayments;
    }

    public TransferOptions getTarget2Payment() {
        return Target2Payment;
    }

    public void setTarget2Payment(TransferOptions target2Payment) {
        Target2Payment = target2Payment;
    }
}
