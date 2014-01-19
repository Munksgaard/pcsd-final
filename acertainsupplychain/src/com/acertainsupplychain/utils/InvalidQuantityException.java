package com.acertainsupplychain.utils;

/**
 * This exception flags that an invalid item ID was given to an item supplier.
 */
@SuppressWarnings("serial")
public class InvalidQuantityException extends OrderProcessingException {

    /**
     * Constructor based on Exception constructors.
     */
    public InvalidQuantityException() {
        super();
    }

    /**
     * Constructor based on Exception constructors.
     */
    public InvalidQuantityException(String message) {
        super(message);
    }

    /**
     * Constructor based on Exception constructors.
     */
    public InvalidQuantityException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor based on Exception constructors.
     */
    public InvalidQuantityException(Throwable ex) {
        super(ex);
    }

}
