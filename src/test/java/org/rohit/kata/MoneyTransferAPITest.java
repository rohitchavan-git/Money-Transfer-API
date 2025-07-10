package org.rohit.kata;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.rohit.kata.domain.accounts.Account;
import org.rohit.kata.domain.accounts.exception.InsufficientFundsException;
import org.rohit.kata.domain.accounts.repo.AccountRepository;
import org.rohit.kata.domain.accounts.repo.InMemoryDatabaseRepository;
import org.rohit.kata.domain.transfer.TransactionalStatus;
import org.rohit.kata.domain.transfer.exception.InvalidAccountException;
import org.rohit.kata.domain.transfer.exception.InvalidAmountException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class MoneyTransferAPITest {

    private String ROHIT_ACCOUNT_ID ;
    private String JOHN_ACCOUNT_ID;
    private AccountRepository accountRepository;
    private TransferService transferService;


    @BeforeEach
    public void init() {
        ROHIT_ACCOUNT_ID = UUID.randomUUID().toString();
        JOHN_ACCOUNT_ID = UUID.randomUUID().toString();
        accountRepository = new InMemoryDatabaseRepository();
        transferService= new TransferService(accountRepository);

    }

    @Test
    public void
    test_add_new_account() {

       Account rohitAccount=  new Account(ROHIT_ACCOUNT_ID ,new BigDecimal(1000));
       accountRepository.save(rohitAccount);
       assertTrue(accountRepository.hasAccount(ROHIT_ACCOUNT_ID));
       assertFalse(accountRepository.hasAccount(JOHN_ACCOUNT_ID));
    }

    @Test
    public void
    test_transfer_throw_an_exception_when_destination_not_found() {


        Account rohitAccount=  new Account(ROHIT_ACCOUNT_ID ,new BigDecimal(1000));
        Account johnAccount=  new Account(JOHN_ACCOUNT_ID ,new BigDecimal(1000));
        accountRepository.save(rohitAccount);

        BigDecimal amount=new BigDecimal(500);
        assertThrows(InvalidAccountException.class,
                ()->transferService.transfer(rohitAccount,johnAccount,amount));
    }
    @Test
    public void
    test_transfer_throw_an_exception_when_source_not_found() {


        Account rohitAccount=  new Account(ROHIT_ACCOUNT_ID ,new BigDecimal(1000));
        Account johnAccount=  new Account(JOHN_ACCOUNT_ID ,new BigDecimal(1000));
        accountRepository.save(rohitAccount);

        BigDecimal amount=new BigDecimal(500);
        assertThrows(InvalidAccountException.class,
                ()->transferService.transfer(rohitAccount,johnAccount,amount));
    }

    @Test
    public void
    test_transfer_throw_an_exception_when_transfer_amount_negative() {


        Account rohitAccount=  new Account(ROHIT_ACCOUNT_ID ,new BigDecimal(1000));
        Account johnAccount=  new Account(JOHN_ACCOUNT_ID ,new BigDecimal(1000));
        accountRepository.save(rohitAccount);
        accountRepository.save(johnAccount);

        BigDecimal amount=new BigDecimal(-1);
        assertThrows(InvalidAmountException.class,
                ()->transferService.transfer(rohitAccount,johnAccount,amount));
    }

    @Test
    public void
    test_transfer_amount() throws InvalidAmountException, InsufficientFundsException {


        Account sourceAccount=  new Account(ROHIT_ACCOUNT_ID ,new BigDecimal(1000));
        Account destinationAccount=  new Account(JOHN_ACCOUNT_ID ,new BigDecimal(1000));
        accountRepository.save(sourceAccount);
        accountRepository.save(destinationAccount);

        BigDecimal amount=new BigDecimal(500);

        String status = transferService.transfer(sourceAccount , destinationAccount , amount);
        assertEquals(status, TransactionalStatus.SUCCEED.getName());
        assertEquals(sourceAccount.getBalance() , new BigDecimal(1000).subtract(amount));
        assertEquals(destinationAccount.getBalance() , new BigDecimal(1000).add(amount));
    }
    @Test
    public void
    test_transfer_amount_for_InsufficientFund() throws InvalidAmountException, InsufficientFundsException {


        Account sourceAccount=  new Account(ROHIT_ACCOUNT_ID ,new BigDecimal(400));
        Account destinationAccount=  new Account(JOHN_ACCOUNT_ID ,new BigDecimal(1000));
        accountRepository.save(sourceAccount);
        accountRepository.save(destinationAccount);

        BigDecimal amount=new BigDecimal(500);

        String status = transferService.transfer(sourceAccount , destinationAccount , amount);
        assertEquals(status, TransactionalStatus.FAILED.getName());

    }

    @Test
    public void
    test_transfer_amount_for_multitreaded_evn() throws InterruptedException {

        Account sourceAccount=  new Account(ROHIT_ACCOUNT_ID ,new BigDecimal(1000));
        Account destinationAccount=  new Account(JOHN_ACCOUNT_ID ,new BigDecimal(1000));
        List<Thread> threads = initiateTransfers(sourceAccount , destinationAccount);

        for (Thread t : threads) {
            t.join();
        }

        BigDecimal total = sourceAccount.getBalance().add(destinationAccount.getBalance());

        assertEquals(new BigDecimal("2000"), total);



    }

    private List<Thread> initiateTransfers(Account sourceAccount , Account destinationAccount) {
        Runnable transferTask = () -> {
            try {
                transferService.transfer(sourceAccount , destinationAccount , new BigDecimal("10.00"));
            } catch (Exception e) {
                System.err.println("Transfer failed: " + e.getMessage());
            }
        };

        int numThreads = 5;
        List<Thread> threads = new ArrayList<>();

        for (int i = 0; i < numThreads; i++) {
            Thread t = new Thread(transferTask);
            threads.add(t);
            t.start(); // start immediately
        }
        return threads;
    }


}
