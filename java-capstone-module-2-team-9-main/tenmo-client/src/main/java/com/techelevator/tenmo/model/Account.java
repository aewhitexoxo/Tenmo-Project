package com.techelevator.tenmo.model;

import java.math.BigDecimal;

public class Account {

    private Long accountId;
    private BigDecimal balance;
    private int userId;

    public Account() {};

    public Account(Long accountId, BigDecimal balance, int userId) {
        this.accountId = accountId;
        this.balance = balance;
        this.userId = userId;
    }

    public Long getAccountId() {
        return accountId;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public int getUserId() {
        return userId;
    }
}
