package com.laris.dataprocessingpipeline.exception;

public class NodeProcessingException extends RuntimeException {
    
    public NodeProcessingException(String message) {
        super(message);
    }
    
    public NodeProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}
