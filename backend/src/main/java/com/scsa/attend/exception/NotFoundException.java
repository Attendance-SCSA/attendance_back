package com.scsa.attend.exception;

public class NotFoundException extends RuntimeException {
    public NotFoundException () {

    }
    public NotFoundException (String message) {
        super(message);
    }
}
