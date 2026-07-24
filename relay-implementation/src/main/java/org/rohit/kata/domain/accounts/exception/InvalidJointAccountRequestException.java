// Implements: AC-2, AC-3
package org.rohit.kata.domain.accounts.exception;

public class InvalidJointAccountRequestException extends IllegalArgumentException {

    public InvalidJointAccountRequestException(String message) {
        super(message);
    }
}
