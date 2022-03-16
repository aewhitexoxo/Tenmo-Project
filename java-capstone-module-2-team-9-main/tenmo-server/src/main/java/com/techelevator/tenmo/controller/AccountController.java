package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.AccountDao;
import com.techelevator.tenmo.dao.TransferDao;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import com.techelevator.tenmo.security.jwt.InvalidTransferException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@PreAuthorize("isAuthenticated()")
public class AccountController {
    private AccountDao accountDao;
    private TransferDao transferDao;

    public AccountController(AccountDao accountDao, TransferDao transferDao) {
        this.accountDao = accountDao;
        this.transferDao = transferDao;
    }

    @RequestMapping(path = "user/{id}/balance", method = RequestMethod.GET)
    public Account balance(@PathVariable int id) {
        return accountDao.findByUserId(id);
    }

    @RequestMapping(path = "users/", method = RequestMethod.GET)
    public List<User> userList() {
        return accountDao.getListOfUsers();
    }


    @RequestMapping(path = "transfers/", method = RequestMethod.POST)
    public Transfer send(@Valid @RequestBody Transfer transfer) throws InvalidTransferException {
        Transfer transfer1 = transferDao.sendMoney(transfer.getSenderAccount(), transfer.getReceiverAccount(), transfer.getAmount());
        return transfer1;
    }

    @RequestMapping(path = "transfers/{id}", method = RequestMethod.GET)
    public List<Transfer> transfers (@PathVariable int id) {
        return transferDao.getTransfersByAccountId(id);
    }

    @RequestMapping(path = "user/{id}/account/", method = RequestMethod.GET)
    public Account account(@PathVariable int id) {
        return accountDao.findByUserId(id);
    }


    @RequestMapping(path = "transfer/{id}", method = RequestMethod.GET)
    public Transfer transfer (@PathVariable int id){
        return transferDao.getTransferDetails(id);
    }
}

