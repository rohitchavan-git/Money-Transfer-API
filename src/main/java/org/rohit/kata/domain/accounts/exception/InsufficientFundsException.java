package org.rohit.kata.domain.accounts.exception;

public class InsufficientFundsException extends Exception {
    public InsufficientFundsException(String msg) {
        super(msg);
    }
}
