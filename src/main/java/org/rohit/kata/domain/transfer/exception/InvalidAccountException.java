package org.rohit.kata.domain.transfer.exception;

public class InvalidAccountException extends RuntimeException{
    public InvalidAccountException(String msg){
        super(msg);
    }
}
