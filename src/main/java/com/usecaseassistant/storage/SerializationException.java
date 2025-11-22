package com.usecaseassistant.storage;

/**
 * Exception thrown when serialization or deserialization operations fail.
 */
public class SerializationException extends RuntimeException {
    
    public SerializationException(String message) {
        super(message);
    }
    
    public SerializationException(String message, Throwable cause) {
        super(message, cause);
    }
}
