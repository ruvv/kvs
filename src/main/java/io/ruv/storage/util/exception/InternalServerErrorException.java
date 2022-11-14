package io.ruv.storage.util.exception;

import org.springframework.http.HttpStatus;

/**
 * Base for exception resulting in http 500 for the client
 */
public abstract class InternalServerErrorException extends FrontalExceptionSupport {


    public InternalServerErrorException(String message, Exception cause) {
        super(message, cause);
    }

    @Override
    public HttpStatus getHttpStatus() {

        return HttpStatus.INTERNAL_SERVER_ERROR;
    }
}
