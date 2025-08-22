package com.nhatcoi.security;

import com.nhatcoi.security.common.dto.ResponseData;
import com.nhatcoi.security.common.service.ResponseDataService;
import com.nhatcoi.security.common.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/test/response")
@RequiredArgsConstructor
@Slf4j
public class ResponseDataTest {

    private final ResponseDataService responseDataService;
    private final MessageService messageService;

    @GetMapping("/success-simple")
    public ResponseEntity<ResponseData<Object>> testSuccessSimple() {
        return ResponseEntity.ok(responseDataService.success("success.login"));
    }

    @GetMapping("/success-with-data")
    public ResponseEntity<ResponseData<Object>> testSuccessWithData() {
        Map<String, Object> data = new HashMap<>();
        data.put("userId", 1);
        data.put("email", "test@example.com");
        data.put("name", "Test User");
        
        return ResponseEntity.ok(responseDataService.success("success.login", data));
    }

    @GetMapping("/success-with-parameters")
    public ResponseEntity<ResponseData<Object>> testSuccessWithParameters() {
        Map<String, Object> data = new HashMap<>();
        data.put("sessionCount", 5);
        data.put("maxSessions", 10);
        
        // Sử dụng MessageService trực tiếp
        String message = messageService.getMessage("session.limit.exceeded", 5);
        return ResponseEntity.ok(ResponseData.success(message, data));
    }

    @GetMapping("/error-simple")
    public ResponseEntity<ResponseData<Object>> testErrorSimple() {
        return ResponseEntity.badRequest().body(responseDataService.error("auth.invalid.credentials"));
    }

    @GetMapping("/error-with-code")
    public ResponseEntity<ResponseData<Object>> testErrorWithCode() {
        return ResponseEntity.badRequest().body(
            responseDataService.error("auth.invalid.credentials", "AUTH_001", 1000)
        );
    }

    @GetMapping("/error-with-details")
    public ResponseEntity<ResponseData<Object>> testErrorWithDetails() {
        Map<String, String> details = new HashMap<>();
        details.put("field", "email");
        details.put("value", "invalid-email");
        details.put("reason", "Invalid format");
        
        return ResponseEntity.badRequest().body(
            responseDataService.error("validation.invalid.email", details)
        );
    }

    @GetMapping("/multi-language/{locale}")
    public ResponseEntity<ResponseData<Object>> testMultiLanguage(@PathVariable String locale) {
        Map<String, Object> data = new HashMap<>();
        data.put("locale", locale);
        data.put("message", "Test message");
        
        return ResponseEntity.ok(responseDataService.successByLocale("success.login", locale, data));
    }

    @GetMapping("/compare-formats")
    public ResponseEntity<ResponseData<Object>> testCompareFormats() {
        Map<String, Object> comparison = new HashMap<>();
        
        // Old format (Map)
        Map<String, Object> oldFormat = new HashMap<>();
        oldFormat.put("success", true);
        oldFormat.put("message", "Login successful");
        oldFormat.put("data", Map.of("userId", 1));
        
        // New format (ResponseData)
        ResponseData<Object> newFormat = responseDataService.success("success.login", Map.of("userId", 1));
        
        comparison.put("oldFormat", oldFormat);
        comparison.put("newFormat", newFormat);
        comparison.put("advantages", new String[]{
            "Type safety",
            "Consistent structure",
            "Built-in timestamp",
            "Easy to extend",
            "Message integration"
        });
        
        return ResponseEntity.ok(ResponseData.success(comparison));
    }

    @GetMapping("/all-types")
    public ResponseEntity<ResponseData<Object>> testAllResponseTypes() {
        Map<String, Object> examples = new HashMap<>();
        
        // Success examples
        examples.put("successSimple", ResponseData.success("Simple success"));
        examples.put("successWithData", ResponseData.success("Success with data", Map.of("key", "value")));
        examples.put("successWithMessage", responseDataService.success("success.login"));
        examples.put("successWithMessageAndData", responseDataService.success("success.login", Map.of("token", "abc123")));
        
        // Error examples
        examples.put("errorSimple", ResponseData.error("Simple error"));
        examples.put("errorWithCode", ResponseData.error("Error with code", "ERR_001", 1000));
        examples.put("errorWithDetails", ResponseData.error("Error with details", Map.of("field", "email")));
        examples.put("errorWithMessage", responseDataService.error("auth.invalid.credentials"));
        examples.put("errorWithMessageAndCode", responseDataService.error("auth.invalid.credentials", "AUTH_001", 1000));
        
        return ResponseEntity.ok(ResponseData.success(examples));
    }
}
