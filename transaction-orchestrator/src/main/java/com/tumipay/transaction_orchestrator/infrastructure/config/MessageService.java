package com.tumipay.transaction_orchestrator.infrastructure.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageSource messageSource;

    /**
     * Resolve message for current locale.
     * @param key Message key
     * @param args Message arguments
     * @return Resolved message
     */
    public String getMessage(String key, Object... args) {
        Object[] messageArgs = (args == null) ? new Object[0] : args;
        String message = messageSource.getMessage(key, messageArgs, LocaleContextHolder.getLocale());
        log.debug("Resolved message for key {}: {}", key, message);
        return message;
    }

    /**
     * Resolve message for current locale with a default value.
     * @param key Message key
     * @param defaultMessage Default message if key not found
     * @param args Message arguments
     * @return Resolved message
     */
    public String getMessage(String key, String defaultMessage, Object... args) {
        return messageSource.getMessage(key, args, defaultMessage, LocaleContextHolder.getLocale());
    }
}
