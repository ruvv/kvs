package io.ruv.storage.util.exception;

import org.springframework.http.HttpStatus;

/**
 * Base for exception resulting in http 400 for the client
 */
public abstract class BadRequestException extends FrontalExceptionSupport {

    public BadRequestException(String message) {
        super(message);
    }

    @Override
    public HttpStatus getHttpStatus() {

        return HttpStatus.BAD_REQUEST;
    }
}
