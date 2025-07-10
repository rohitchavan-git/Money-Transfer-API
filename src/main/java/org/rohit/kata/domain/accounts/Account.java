package org.rohit.kata.domain.accounts;

import org.rohit.kata.domain.accounts.exception.InsufficientFundsException;
import org.rohit.kata.domain.transfer.exception.InvalidAmountException;

import java.math.BigDecimal;
import java.util.Objects;

public class Account {
    private final String accountId;
    private BigDecimal balance;

    public Account(String accountId , BigDecimal balance) {
        this.accountId = accountId;
        this.balance = balance;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public String getAccountId() {
        return accountId;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return Objects.equals(getAccountId() , account.getAccountId()) && Objects.equals(getBalance() , account.getBalance());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getAccountId() , getBalance());
    }

    public void withdraw(BigDecimal amount) throws InvalidAmountException,
            InsufficientFundsException {


        if (amount.compareTo(BigDecimal.ZERO)<=0) {
            throw new InvalidAmountException();
        }

        if(balance.compareTo(amount) < 0){
            throw new InsufficientFundsException("Insufficient balance in account " + accountId);
        }
        balance=balance.subtract(amount);

    }

    public void deposit(BigDecimal amount) throws InvalidAmountException, InsufficientFundsException {

        if(amount.compareTo(BigDecimal.ZERO) < 0){
            throw new InvalidAmountException();
        }
        balance=balance.add(amount);
    }
}
