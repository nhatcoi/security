package com.nhatcoi.security;

import com.nhatcoi.security.common.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/test/success")
@RequiredArgsConstructor
@Slf4j
public class SuccessMessage {

    private final MessageService messageService;

    @GetMapping("/all")
    public ResponseEntity<Map<String, Object>> getAllSuccessMessages() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("messages", Map.of(
            "register", messageService.getMessage("success.register"),
            "login", messageService.getMessage("success.login"),
            "logout", messageService.getMessage("success.logout"),
            "logout_all", messageService.getMessage("success.logout.all"),
            "refresh", messageService.getMessage("success.refresh"),
            "session_revoke", messageService.getMessage("success.session.revoke"),
            "session_revoke_all", messageService.getMessage("success.session.revoke.all")
        ));
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/auth")
    public ResponseEntity<Map<String, Object>> getAuthSuccessMessages() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("messages", Map.of(
            "register", messageService.getMessage("success.register"),
            "login", messageService.getMessage("success.login"),
            "logout", messageService.getMessage("success.logout"),
            "logout_all", messageService.getMessage("success.logout.all"),
            "refresh", messageService.getMessage("success.refresh")
        ));
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/session")
    public ResponseEntity<Map<String, Object>> getSessionSuccessMessages() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("messages", Map.of(
            "session_revoke", messageService.getMessage("success.session.revoke"),
            "session_revoke_all", messageService.getMessage("success.session.revoke.all")
        ));
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/locale/{lang}")
    public ResponseEntity<Map<String, Object>> getSuccessMessagesByLocale(@PathVariable String lang) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("locale", lang);
        response.put("messages", Map.of(
            "register", messageService.getMessageByLocale("success.register", lang),
            "login", messageService.getMessageByLocale("success.login", lang),
            "logout", messageService.getMessageByLocale("success.logout", lang),
            "logout_all", messageService.getMessageByLocale("success.logout.all", lang),
            "refresh", messageService.getMessageByLocale("success.refresh", lang)
        ));
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/compare")
    public ResponseEntity<Map<String, Object>> compareLocales() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("comparison", Map.of(
            "vietnamese", Map.of(
                "register", messageService.getMessageByLocale("success.register", "vi"),
                "login", messageService.getMessageByLocale("success.login", "vi"),
                "logout", messageService.getMessageByLocale("success.logout", "vi")
            ),
            "english", Map.of(
                "register", messageService.getMessageByLocale("success.register", "en"),
                "login", messageService.getMessageByLocale("success.login", "en"),
                "logout", messageService.getMessageByLocale("success.logout", "en")
            )
        ));
        
        return ResponseEntity.ok(response);
    }
}
