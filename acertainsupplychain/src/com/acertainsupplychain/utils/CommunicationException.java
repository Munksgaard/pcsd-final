package com.acertainsupplychain.utils;

/**
 * This exception flags that an invalid item ID was given to an item supplier.
 */
@SuppressWarnings("serial")
public class CommunicationException extends OrderProcessingException {

    /**
     * Constructor based on Exception constructors.
     */
    public CommunicationException() {
        super();
    }

    /**
     * Constructor based on Exception constructors.
     */
    public CommunicationException(String message) {
        super(message);
    }

}
