package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.AuthenticatedUser;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import com.techelevator.util.BasicLogger;
import io.cucumber.java.en_old.Ac;
import org.springframework.http.*;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class AccountService {

    private final String baseUrl;
    private final RestTemplate restTemplate = new RestTemplate();

    public AccountService(String url) {
        this.baseUrl = url;
    }

    public BigDecimal getCurrentBalance(AuthenticatedUser authenticatedUser) {

        ResponseEntity<Account> response = null;

        try {
            response = restTemplate.exchange(baseUrl + "user/" + authenticatedUser.getUser().getId() + "/balance", HttpMethod.GET,
                    createAuthEntity(authenticatedUser), Account.class);

        } catch (RestClientResponseException e) {
            BasicLogger.log(e.getRawStatusCode() + " : " + e.getStatusText());
        } catch (ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        if (response == null) {
            return null;
        }
        return response.getBody().getBalance();
    }

    public Long getAccountByUser(AuthenticatedUser authenticatedUser, Long id) {
        Account account = new Account();
        ResponseEntity<Account> response = null;

        try {
            response = restTemplate.exchange(baseUrl + "user/" + id + "/account/", HttpMethod.GET, createAuthEntity(authenticatedUser), Account.class);
            account = response.getBody();

        } catch (RestClientResponseException e) {
            BasicLogger.log(e.getRawStatusCode() + " : " + e.getStatusText());
        } catch (ResourceAccessException e) {
            BasicLogger.log(e.getMessage());

        }
        if (response == null) {
            return null;
        }
        return account.getAccountId();
    }

    public void getUsers(AuthenticatedUser authenticatedUser) {
        User[] users = null;
        ResponseEntity<User[]> response = null;
        try {
            response = restTemplate.exchange(baseUrl + "users/", HttpMethod.GET, createAuthEntity(authenticatedUser), User[].class);
            users = response.getBody();
        } catch (RestClientResponseException e) {
            BasicLogger.log(e.getRawStatusCode() + " : " + e.getStatusText());
        } catch (ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        if (response == null) {
            System.out.println("Error: user not found");
        } else {

            for (User user : users) {
                System.out.println(user.getId() + "       " + user.getUsername());

            }
            System.out.println("---------\n");
        }
    }

    public void getTransfersByAccountId(AuthenticatedUser authenticatedUser) {
        Long id = authenticatedUser.getUser().getId();
        Transfer[] transfers = null;
        ResponseEntity<Transfer[]> response = null;

        try {
            response = restTemplate.exchange(baseUrl + "transfers/" + id, HttpMethod.GET,
                    createAuthEntity(authenticatedUser), Transfer[].class);

            transfers = response.getBody();

        } catch (RestClientResponseException e) {
            BasicLogger.log(e.getRawStatusCode() + " : " + e.getStatusText());
        } catch (ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }

        if (response == null) {
            System.out.println("Error: transfer not found");
        } else {
            System.out.println("-------------------------------------------\n" +
                    "Transfers\n" +
                    "ID       From/To          Amount\n" +
                    "-------------------------------------------\n");
            for (Transfer transfer : transfers) {
                String output;
                if (authenticatedUser.getUser().getUsername().equals(transfer.getReceiverName())) {
                    output= "     From: " + transfer.getSenderName();

                } else {
                    output =  "     To:   " + transfer.getReceiverName();
                }
                System.out.println(transfer.getTransferId() + output + "     $" + transfer.getAmount());
            }

            System.out.println("---------\n");

        }
    }

    public void getTransferByTransferId(AuthenticatedUser authenticatedUser,Long transferId){
        Transfer transfer = new Transfer();
        ResponseEntity<Transfer> response = null;
        try{
            response = restTemplate.exchange(baseUrl + "transfer/"+  transferId,
                    HttpMethod.GET, createAuthEntity(authenticatedUser), Transfer.class);

            transfer = response.getBody();

        }catch (RestClientResponseException e) {
            BasicLogger.log(e.getRawStatusCode() + " : " + e.getStatusText());
        } catch (ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }

        if(response == null){
            System.out.println("Error: transfer not found");
        } else {
            System.out.println("--------------------------------------------\n" +
                    "Transfer Details\n" +
                    "--------------------------------------------\n" +
                    " Id:     " + transfer.getTransferId() + " \n" +
                    " From:   " +  transfer.getSenderName() + " \n" +
                    " To:     " +  transfer.getReceiverName() + " \n" +
                    " Type:   " + transfer.getType() + " \n" +
                    " Status: " + transfer.getStatus() + " \n" +
                    " Amount: " +
                    "$" + transfer.getAmount() + " \n");
        }
    }

    public Transfer send(AuthenticatedUser authenticatedUser, Transfer newTransfer) {

        try {
            ResponseEntity<Transfer> result = restTemplate.exchange(baseUrl + "transfers/", HttpMethod.POST,
                    createTransferEntity(authenticatedUser, newTransfer), Transfer.class);

            System.out.println("\n    Transfer Complete :) ");

            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    private HttpEntity<Account> createAuthEntity(AuthenticatedUser authenticatedUser) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(authenticatedUser.getToken());
        return new HttpEntity<>(headers);
    }

    private HttpEntity<Transfer> createTransferEntity(AuthenticatedUser authenticatedUser, Transfer transfer) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(authenticatedUser.getToken());
        return new HttpEntity<>(transfer, headers);
    }


}


