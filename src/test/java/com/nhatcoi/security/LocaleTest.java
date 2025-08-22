package com.nhatcoi.security;

import com.nhatcoi.security.common.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/test/locale")
@RequiredArgsConstructor
@Slf4j
public class LocaleTest {

    private final MessageService messageService;

    @GetMapping("/current")
    public ResponseEntity<Map<String, Object>> getCurrentLocale() {
        Locale currentLocale = LocaleContextHolder.getLocale();
        
        Map<String, Object> response = new HashMap<>();
        response.put("currentLocale", currentLocale.toString());
        response.put("language", currentLocale.getLanguage());
        response.put("country", currentLocale.getCountry());
        response.put("displayName", currentLocale.getDisplayName());
        response.put("displayLanguage", currentLocale.getDisplayLanguage());
        response.put("displayCountry", currentLocale.getDisplayCountry());
        
        // Test message with current locale
        response.put("testMessage", messageService.getMessage("success.login"));
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/test/{locale}")
    public ResponseEntity<Map<String, Object>> testLocale(@PathVariable String locale) {
        Locale testLocale = new Locale(locale);
        
        Map<String, Object> response = new HashMap<>();
        response.put("testLocale", testLocale.toString());
        response.put("language", testLocale.getLanguage());
        response.put("country", testLocale.getCountry());
        
        // Test messages with specific locale
        response.put("messages", Map.of(
            "login", messageService.getMessageByLocale("success.login", locale),
            "register", messageService.getMessageByLocale("success.register", locale),
            "logout", messageService.getMessageByLocale("success.logout", locale)
        ));
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/debug")
    public ResponseEntity<Map<String, Object>> debugLocale() {
        Locale currentLocale = LocaleContextHolder.getLocale();
        Locale systemLocale = Locale.getDefault();
        
        Map<String, Object> response = new HashMap<>();
        response.put("currentLocale", currentLocale.toString());
        response.put("systemLocale", systemLocale.toString());
        response.put("availableLocales", Locale.getAvailableLocales().length);
        
        // Test different message keys
        response.put("testMessages", Map.of(
            "success.login", messageService.getMessage("success.login"),
            "success.login_en", messageService.getMessageByLocale("success.login", "en"),
            "success.login_vi", messageService.getMessageByLocale("success.login", "vi"),
            "auth.invalid.credentials", messageService.getMessage("auth.invalid.credentials"),
            "auth.invalid.credentials_en", messageService.getMessageByLocale("auth.invalid.credentials", "en"),
            "auth.invalid.credentials_vi", messageService.getMessageByLocale("auth.invalid.credentials", "vi")
        ));
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/switch/{locale}")
    public ResponseEntity<Map<String, Object>> switchLocale(@PathVariable String locale) {
        // This will be handled by LocaleChangeInterceptor
        Locale newLocale = new Locale(locale);
        LocaleContextHolder.setLocale(newLocale);
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Locale switched to: " + locale);
        response.put("newLocale", newLocale.toString());
        response.put("testMessage", messageService.getMessage("success.login"));
        
        return ResponseEntity.ok(response);
    }
}
