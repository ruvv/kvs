package io.ruv.storage.persistence;

import io.ruv.storage.util.exception.ErrorCode;
import io.ruv.storage.util.exception.InternalServerErrorException;
import lombok.Getter;

/**
 * Exception indicating persistence problem
 */
@Getter
public class PersistenceException extends InternalServerErrorException {

    private final String key;
    private final ErrorCode errorCode;

    public static PersistenceException writing(String key, Exception cause) {

        return new PersistenceException(
                key,
                ErrorCode.PERSISTENCE_WRITE,
                String.format("Failed to persist value associated with key '%s'.", key),
                cause);
    }

    public static PersistenceException reading(String key, Exception cause) {

        return new PersistenceException(
                key,
                ErrorCode.PERSISTENCE_READ,
                String.format("Failed to load value associated with key '%s'.", key),
                cause);
    }

    public static PersistenceException readingStorage(Exception cause) {

        return new PersistenceException(
                "",
                ErrorCode.PERSISTENCE_READ_STORAGE,
                "Failed to read persistent storage.",
                cause);
    }

    public static PersistenceException cleaningStorage(Exception cause) {

        return new PersistenceException(
                "",
                ErrorCode.PERSISTENCE_CLEAN_STORAGE,
                "Failed to clean persistent storage.",
                cause);
    }

    private PersistenceException(String key, ErrorCode errorCode, String message, Exception cause) {

        super(message, cause);
        this.key = key;
        this.errorCode = errorCode;
    }

    @Override
    protected Object[] getArgs() {

        return new Object[]{key};
    }
}
