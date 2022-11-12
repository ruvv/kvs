package io.ruv.util;

import io.ruv.web.dto.ErrorWrapperDto;
import lombok.val;
import org.springframework.context.MessageSource;

public abstract class FrontalExceptionSupport extends RuntimeException implements ErrorCodeContainer {

    public FrontalExceptionSupport(String message) {

        super(message);
    }

    protected abstract Object[] getArgs();

    public ErrorWrapperDto makeErrorWrapper(MessageSource messageSource) {

        val errorCode = this.getErrorCode();
        val args = getArgs();
        val message = errorCode.localizedMessage(messageSource, args);

        return new ErrorWrapperDto(errorCode.name(), message);
    }
}
