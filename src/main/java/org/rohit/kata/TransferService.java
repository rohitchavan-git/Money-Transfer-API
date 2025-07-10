package org.rohit.kata;

import org.rohit.kata.domain.accounts.Account;
import org.rohit.kata.domain.accounts.exception.InsufficientFundsException;
import org.rohit.kata.domain.accounts.repo.AccountRepository;
import org.rohit.kata.domain.transfer.TransactionalStatus;
import org.rohit.kata.domain.transfer.exception.InvalidAccountException;
import org.rohit.kata.domain.transfer.exception.InvalidAmountException;

import java.math.BigDecimal;
import java.util.Optional;

public class TransferService {

    final AccountRepository accountRepository;

    public TransferService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public String transfer(Account sourceAccount , Account destinationAccount
            , BigDecimal amount) throws InvalidAmountException, InsufficientFundsException {

        if (notValidAccount(sourceAccount)){
            throw new InvalidAccountException("Source not Found.");
        }
        if (notValidAccount(destinationAccount)) {
            throw new InvalidAccountException("destination not Found");
        }
        if(amount.compareTo(BigDecimal.ZERO) <=0){
            throw new InvalidAmountException();
        }

        return performTransfer(sourceAccount , destinationAccount , amount);

    }

    private static String performTransfer(Account sourceAccount , Account destinationAccount , BigDecimal amount) throws InvalidAmountException, InsufficientFundsException {
        Object lock1,lock2;
        if(sourceAccount.getAccountId().compareTo(destinationAccount.getAccountId()) < 0){
                lock1= sourceAccount;
                lock2= destinationAccount;
        }else {
            lock1= destinationAccount;
            lock2= sourceAccount;
        }

        synchronized (lock1){
            synchronized (lock2){
                try {
                    sourceAccount.withdraw(amount);
                    destinationAccount.deposit(amount);
                    return TransactionalStatus.SUCCEED.getName();
                } catch (Exception e) {
                    sourceAccount.deposit(amount);
                    return TransactionalStatus.FAILED.getName();
                }
            }
        }
    }

    private boolean notValidAccount(Account sourceAccount) {
        return Optional.ofNullable(sourceAccount)
                .map(Account::getAccountId)
                .filter(accountRepository::hasAccount)
                .isEmpty();
    }
}

