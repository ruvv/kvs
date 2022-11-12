package io.ruv.web.advice;

import io.ruv.util.FrontalExceptionSupport;
import io.ruv.web.dto.ErrorWrapperDto;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@RequiredArgsConstructor
public class FrontalExceptionAdvice {

    private final MessageSource messageSource;

    @ExceptionHandler(FrontalExceptionSupport.class)
    public ResponseEntity<ErrorWrapperDto> frontalError(FrontalExceptionSupport ex) {

        val errorWrapper = ex.makeErrorWrapper(messageSource);

        return ResponseEntity.badRequest()
                .contentType(MediaType.APPLICATION_JSON)
                .body(errorWrapper);
    }
}
