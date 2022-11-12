package io.ruv.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;


@Slf4j
@RequiredArgsConstructor
public enum ErrorCode {

    MISSING_KEY("errors.missing-key"),
    DUPLICATE_KEY("errors.duplicate-key");

    private final String messageCode;

    public String localizedMessage(MessageSource messageSource, Object... args) {

        val locale = LocaleContextHolder.getLocale();

        try {

            return messageSource.getMessage(messageCode, args, locale);
        } catch (NoSuchMessageException ex) {

            log.warn("Failed to resolve messageCode '{}' with messageSource '{}'.", messageCode, messageSource);
            return messageCode;
        }
    }
}
