package com.techelevator.tenmo.model;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;

public class Transfer {

    private Long transferId;
    @NotNull
    private Long transferTypeId;
    @NotNull
    private Long transferStatusId;
    @NotNull
    private Long senderAccount;
    @NotNull
    private Long receiverAccount;
    @NotNull
    @Positive
    private BigDecimal amount;

    private String senderName;
    private String receiverName;
    private String type;
    private String status;



    public Transfer(){}

    public Transfer(Long transferId, Long transferTypeId, Long transferStatusId, Long senderAccount, Long receiverAccount, BigDecimal amount) {
        this.transferId = transferId;
        this.transferTypeId = transferTypeId;
        this.transferStatusId = transferStatusId;
        this.senderAccount = senderAccount;
        this.receiverAccount = receiverAccount;
        this.amount = amount;
    }

    public Long getTransferId() {
        return transferId;
    }

    public void setTransferId(Long transferId) {
        this.transferId = transferId;
    }

    public Long getTransferTypeId() {
        return transferTypeId;
    }

    public void setTransferTypeId(Long transferTypeId) {
        this.transferTypeId = transferTypeId;
    }

    public Long getTransferStatusId() {
        return transferStatusId;
    }

    public void setTransferStatusId(Long transferStatusId) {
        this.transferStatusId = transferStatusId;
    }

    public Long getSenderAccount() {
        return senderAccount;
    }

    public void setSenderAccount(Long senderAccount) {
        this.senderAccount = senderAccount;
    }

    public Long getReceiverAccount() {
        return receiverAccount;
    }

    public void setReceiverAccount(Long receiverAccount) {
        this.receiverAccount = receiverAccount;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
