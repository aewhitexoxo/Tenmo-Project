package com.techelevator.tenmo;

import com.techelevator.tenmo.model.AuthenticatedUser;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import com.techelevator.tenmo.model.UserCredentials;
import com.techelevator.tenmo.services.AccountService;
import com.techelevator.tenmo.services.AuthenticationService;
import com.techelevator.tenmo.services.ConsoleService;

import java.math.BigDecimal;
import java.sql.SQLOutput;

public class App {

    private static final String API_BASE_URL = "http://localhost:8080/";

    private final ConsoleService consoleService = new ConsoleService();
    private final AuthenticationService authenticationService = new AuthenticationService(API_BASE_URL);
    private final AccountService accountService = new AccountService(API_BASE_URL);

    private AuthenticatedUser currentUser;
    private Transfer currentTransfer = new Transfer();
    private User receiver;
    private final Long TRANSFER_STATUS_ID_APPROVED = 2L;
    private final Long TRANSFER_TYPE_ID_SEND = 2L;

    public static void main(String[] args) {
        App app = new App();
        app.run();
    }

    private void run() {
        consoleService.printGreeting();
        loginMenu();
        if (currentUser != null) {
            mainMenu();
        }
    }
    private void loginMenu() {
        int menuSelection = -1;
        while (menuSelection != 0 && currentUser == null) {
            consoleService.printLoginMenu();
            menuSelection = consoleService.promptForMenuSelection("Please choose an option: ");
            if (menuSelection == 1) {
                handleRegister();
            } else if (menuSelection == 2) {
                handleLogin();
            } else if (menuSelection != 0) {
                System.out.println("Invalid Selection");
                consoleService.pause();
            }
        }
    }

    private void handleRegister() {
        System.out.println("Please register a new user account");
        UserCredentials credentials = consoleService.promptForCredentials();
        if (authenticationService.register(credentials)) {
            System.out.println("Registration successful. You can now login.");
        } else {
            consoleService.printErrorMessage();
        }
    }

    private void handleLogin() {
        UserCredentials credentials = consoleService.promptForCredentials();
        currentUser = authenticationService.login(credentials);
        if (currentUser == null) {
            consoleService.printErrorMessage();
        }
    }

    private void mainMenu() {
        int menuSelection = -1;
        while (menuSelection != 0) {
            consoleService.printMainMenu();
            menuSelection = consoleService.promptForMenuSelection("Please choose an option: ");
            if (menuSelection == 1) {
                viewCurrentBalance();
            } else if (menuSelection == 2) {
                viewTransferHistory();
            } else if (menuSelection == 3) {
                viewPendingRequests();
            } else if (menuSelection == 4) {
                sendBucks();
            } else if (menuSelection == 5) {
                requestBucks();
            } else if (menuSelection == 0) {
                continue;
            } else {
                System.out.println("Invalid Selection");
            }
            consoleService.pause();
        }
    }

	private void viewCurrentBalance() {
		// TODO Auto-generated method stub

        System.out.println("Your current account balance is: $"+ accountService.getCurrentBalance(currentUser).setScale(2));
		
	}

	private void viewTransferHistory() {
		// TODO Auto-generated method stub
		accountService.getTransfersByAccountId(currentUser);
        Long transferId = Long.parseLong(consoleService.promptForString("Please enter transfer ID to view details (0 to cancel): "));

        if(transferId == 0){
            consoleService.printMainMenu();
        } else {
            accountService.getTransferByTransferId(currentUser, transferId);
        }

	}

	private void viewPendingRequests() {
		// TODO Auto-generated method stub
		
	}

	private void sendBucks() {
		// TODO Auto-generated method stub
        consoleService.printSendTEMessage();

        accountService.getUsers(currentUser);

        Long userId = Long.parseLong(consoleService.promptForString(currentUser.getUser().getUsername().toUpperCase() +", please enter ID of user you are sending to (0 to cancel): "));

       Long receiverAccount = accountService.getAccountByUser(currentUser,userId);

       Long senderAccount = accountService.getAccountByUser(currentUser,currentUser.getUser().getId());


        if(userId == 0){
            consoleService.printMainMenu();
        } else {

            BigDecimal amount = consoleService.promptForBigDecimal("Enter amount: ");
            currentTransfer.setAmount(amount);
            currentTransfer.setReceiverAccount(receiverAccount);
            currentTransfer.setSenderAccount(senderAccount);
            currentTransfer.setTransferStatusId(TRANSFER_STATUS_ID_APPROVED);
            currentTransfer.setTransferTypeId(TRANSFER_TYPE_ID_SEND);

            accountService.send(currentUser, currentTransfer);



        }
	}

	private void requestBucks() {
		// TODO Auto-generated method stub
		
	}

}
