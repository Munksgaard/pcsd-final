package com.acertainsupplychain.utils;

/**
 * This exception is used by the Logger class
 */
@SuppressWarnings("serial")
public class LogException extends OrderProcessingException {

    private final Exception e;

    public LogException(Exception e) {
        super(e);
        this.e = e;
    }

    public LogException() {
        super();
        e = null;
    }

    public Exception getException() {
        return e;
    }

}
