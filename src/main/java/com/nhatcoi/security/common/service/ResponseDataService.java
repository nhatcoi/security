package com.nhatcoi.security.common.service;

import com.nhatcoi.security.common.dto.ResponseData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ResponseDataService {
    
    private final MessageService messageService;
    
    // Success responses with message keys
    public <T> ResponseData<T> success(String messageKey, T data) {
        return ResponseData.success(messageService.getMessage(messageKey), data);
    }
    
    public <T> ResponseData<T> success(String messageKey) {
        return ResponseData.success(messageService.getMessage(messageKey));
    }
    
    public <T> ResponseData<T> success(String messageKey, T data, Object... args) {
        return ResponseData.success(messageService.getMessage(messageKey, args), data);
    }
    
    public <T> ResponseData<T> success(String messageKey, Object... args) {
        return ResponseData.success(messageService.getMessage(messageKey, args));
    }
    
    // Error responses with message keys
    public <T> ResponseData<T> error(String messageKey) {
        return ResponseData.error(messageService.getMessage(messageKey));
    }
    
    public <T> ResponseData<T> error(String messageKey, String errorCode) {
        return ResponseData.error(messageService.getMessage(messageKey), errorCode);
    }
    
    public <T> ResponseData<T> error(String messageKey, String errorCode, Integer code) {
        return ResponseData.error(messageService.getMessage(messageKey), errorCode, code);
    }
    
    public <T> ResponseData<T> error(String messageKey, Object details) {
        return ResponseData.error(messageService.getMessage(messageKey), details);
    }
    
    public <T> ResponseData<T> error(String messageKey, String errorCode, Object details) {
        return ResponseData.error(messageService.getMessage(messageKey), errorCode, details);
    }
    
    public <T> ResponseData<T> error(String messageKey, Object details, Object... args) {
        return ResponseData.error(messageService.getMessage(messageKey, args), details);
    }
    
    // Multi-language support
    public <T> ResponseData<T> successByLocale(String messageKey, String locale, T data) {
        return ResponseData.success(messageService.getMessageByLocale(messageKey, locale), data);
    }
    
    public <T> ResponseData<T> successByLocale(String messageKey, String locale) {
        return ResponseData.success(messageService.getMessageByLocale(messageKey, locale));
    }
    
    public <T> ResponseData<T> errorByLocale(String messageKey, String locale) {
        return ResponseData.error(messageService.getMessageByLocale(messageKey, locale));
    }
    
    public <T> ResponseData<T> errorByLocale(String messageKey, String locale, String errorCode) {
        return ResponseData.error(messageService.getMessageByLocale(messageKey, locale), errorCode);
    }
}
