package com.scsa.attend.exception;

public class PermissionDeniedException extends Exception {
    public PermissionDeniedException () {

    }
    public PermissionDeniedException (String message) {
        super(message);
    }
}
