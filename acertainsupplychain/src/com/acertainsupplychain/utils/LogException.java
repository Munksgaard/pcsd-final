package com.acertainsupplychain.utils;

/**
 * This exception is used by the Logger class
 */
@SuppressWarnings("serial")
public class LogException extends Exception {

    private final Exception e;

    public LogException(Exception e) {
        super(e);
        this.e = e;
    }

    public Exception getException() {
        return e;
    }

}
