package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.security.jwt.InvalidTransferException;

import java.math.BigDecimal;
import java.util.List;

public interface TransferDao {

    Transfer sendMoney(Long senderId, Long receiverId, BigDecimal amount) throws InvalidTransferException;

    List<Transfer> getTransfersByAccountId(int id);

    Transfer getTransferDetails(int id);
}
