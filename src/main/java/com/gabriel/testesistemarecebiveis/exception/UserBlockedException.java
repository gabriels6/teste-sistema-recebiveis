package com.gabriel.testesistemarecebiveis.exception;

public class UserBlockedException extends RuntimeException {

    private final int remainingAttempts;

    public UserBlockedException(String message, int remainingAttempts) {
        super(message);
        this.remainingAttempts = remainingAttempts;
    }

    public int getRemainingAttempts() {
        return remainingAttempts;
    }
}
