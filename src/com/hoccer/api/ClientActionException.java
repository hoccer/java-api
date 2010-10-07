package com.hoccer.api;

public class ClientActionException extends Exception {

    private static final long serialVersionUID = -8513254386842627480L;

    public ClientActionException(String details) {
        super(details);
    }
}
