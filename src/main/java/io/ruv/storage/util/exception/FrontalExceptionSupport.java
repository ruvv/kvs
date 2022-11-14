package io.ruv.storage.util.exception;

import io.ruv.storage.web.dto.ErrorWrapperDto;
import lombok.val;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;

public abstract class FrontalExceptionSupport extends RuntimeException implements ErrorCodeContainer {

    public FrontalExceptionSupport(String message) {

        super(message);
    }

    public FrontalExceptionSupport(String message, Exception cause) {

        super(message, cause);
    }

    protected abstract Object[] getArgs();

    public abstract HttpStatus getHttpStatus();

    public ErrorWrapperDto makeErrorWrapper(MessageSource messageSource) {

        val errorCode = this.getErrorCode();
        val args = getArgs();
        val message = errorCode.localizedMessage(messageSource, args);

        return new ErrorWrapperDto(errorCode.name(), message);
    }
}
