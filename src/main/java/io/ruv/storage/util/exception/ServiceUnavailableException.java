package io.ruv.storage.util.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception indicating that requested service api is not available right now
 */
public class ServiceUnavailableException extends FrontalExceptionSupport {

    public ServiceUnavailableException(String message) {

        super(message);
    }

    @Override
    public ErrorCode getErrorCode() {

        return ErrorCode.SERVICE_UNAVAILABLE;
    }

    @Override
    protected Object[] getArgs() {

        return new Object[0];
    }

    @Override
    public HttpStatus getHttpStatus() {

        return HttpStatus.SERVICE_UNAVAILABLE;
    }
}
