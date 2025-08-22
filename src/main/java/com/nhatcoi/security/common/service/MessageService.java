package com.nhatcoi.security.common.service;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.Locale;

@Service
public class MessageService {
    
    private final MessageSource messageSource;
    
    public MessageService(MessageSource messageSource) {
        this.messageSource = messageSource;
    }
    
    /**
     * Lấy message với locale hiện tại
     */
    public String getMessage(String code) {
        return getMessage(code, null);
    }
    
    /**
     * Lấy message với parameters và locale hiện tại
     */
    public String getMessage(String code, Object... args) {
        return getMessage(code, args, LocaleContextHolder.getLocale());
    }
    
    /**
     * Lấy message với locale string
     */
    public String getMessageByLocale(String code, String locale) {
        return getMessage(code, null, new Locale(locale));
    }
    
    /**
     * Lấy message với parameters và locale cụ thể
     */
    public String getMessage(String code, Object[] args, Locale locale) {
        try {
            return messageSource.getMessage(code, args, locale);
        } catch (Exception e) {
            // Fallback to code if message not found
            return code;
        }
    }
    
    /**
     * Format message với pattern
     */
    public String formatMessage(String pattern, Object... args) {
        try {
            return MessageFormat.format(pattern, args);
        } catch (Exception e) {
            return pattern;
        }
    }
    
    /**
     * Lấy message với locale string và parameters
     */
    public String getMessageByLocale(String code, String locale, Object... args) {
        return getMessage(code, args, new Locale(locale));
    }
    
    /**
     * Kiểm tra message có tồn tại không
     */
    public boolean hasMessage(String code) {
        try {
            String message = messageSource.getMessage(code, null, LocaleContextHolder.getLocale());
            return !code.equals(message);
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Lấy message với fallback
     */
    public String getMessageWithFallback(String code, String fallback) {
        try {
            String message = messageSource.getMessage(code, null, LocaleContextHolder.getLocale());
            return code.equals(message) ? fallback : message;
        } catch (Exception e) {
            return fallback;
        }
    }
}
