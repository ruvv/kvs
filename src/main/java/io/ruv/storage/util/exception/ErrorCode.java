package io.ruv.storage.util.exception;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;


@Slf4j
@RequiredArgsConstructor
public enum ErrorCode {

    MISSING_KEY("errors.access.missing-key"),
    DUPLICATE_KEY("errors.access.duplicate-key"),

    PERSISTENCE_WRITE("errors.persistence.write"),

    PERSISTENCE_READ("errors.persistence.read"),

    PERSISTENCE_READ_STORAGE("errors.persistence.read-storage"),

    PERSISTENCE_CLEAN_STORAGE("errors.persistence.clean-storage"),

    SERVICE_UNAVAILABLE("errors.access.service-unavailable");

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
