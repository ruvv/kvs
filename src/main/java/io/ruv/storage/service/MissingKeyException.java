package io.ruv.storage.service;

import io.ruv.storage.util.exception.BadRequestException;
import io.ruv.storage.util.exception.ErrorCode;
import lombok.Getter;

/**
 * Exception indicating missing key on retrieve and delete operations
 */
@Getter
public class MissingKeyException extends BadRequestException {

    private final String key;
    private final ErrorCode errorCode = ErrorCode.MISSING_KEY;

    public static MissingKeyException of(String key) {

        return new MissingKeyException(key, String.format("Key '%s' is not associated with a value.", key));
    }

    private MissingKeyException(String key, String message) {

        super(message);
        this.key = key;
    }

    @Override
    protected Object[] getArgs() {

        return new Object[]{key};
    }
}
