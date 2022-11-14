package io.ruv.storage.util.exception;

/**
 * Exception indicating a configuration problem during initialization
 */
public class InitializationException extends RuntimeException {

    public InitializationException(String message) {

        super(message);
    }
}
