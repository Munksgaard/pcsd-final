package com.acertainsupplychain.utils;

/**
 * This exception flags that an invalid item ID was given to an item supplier.
 */
@SuppressWarnings("serial")
public class InvalidSupplierException extends OrderProcessingException {

    /**
     * Constructor based on Exception constructors.
     */
    public InvalidSupplierException() {
        super();
    }

    /**
     * Constructor based on Exception constructors.
     */
    public InvalidSupplierException(String message) {
        super(message);
    }

    /**
     * Constructor based on Exception constructors.
     */
    public InvalidSupplierException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor based on Exception constructors.
     */
    public InvalidSupplierException(Throwable ex) {
        super(ex);
    }

}
