package io.ruv.storage.web.advice;

import io.ruv.storage.util.exception.FrontalExceptionSupport;
import io.ruv.storage.web.dto.ErrorWrapperDto;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@RequiredArgsConstructor
public class FrontalExceptionAdvice {

    private final MessageSource messageSource;

    @ExceptionHandler(FrontalExceptionSupport.class)
    public ResponseEntity<ErrorWrapperDto> frontalError(FrontalExceptionSupport ex) {

        return ResponseEntity
                .status(ex.getHttpStatus())
                .body(ex.makeErrorWrapper(messageSource));
    }
}
