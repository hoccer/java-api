package com.hoccer.api;

public class BadModeException extends Exception {

    private static final long serialVersionUID = 4395207037071978730L;

    public BadModeException(String details) {
        super(details);
    }
}
