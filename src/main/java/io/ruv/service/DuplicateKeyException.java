package io.ruv.service;

import io.ruv.util.ErrorCode;
import io.ruv.util.FrontalExceptionSupport;
import lombok.Getter;

/**
 * Exception indicating key collision on store operation
 */
@Getter
public
class DuplicateKeyException extends FrontalExceptionSupport {

    private final String key;
    private final ErrorCode errorCode = ErrorCode.DUPLICATE_KEY;

    public static DuplicateKeyException of(String key) {

        return new DuplicateKeyException(key, String.format("Key '%s' is already associated with a value.", key));
    }

    private DuplicateKeyException(String key, String message) {
        super(message);
        this.key = key;
    }

    @Override
    protected Object[] getArgs() {

        return new Object[]{key};
    }
}
