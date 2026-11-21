package com.n23.foa.bankingaiassistant.controller;

import com.n23.foa.bankingaiassistant.ai.BankingAssistant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/assistant")
public class BankingAssistantController {

    private static final Logger log = LoggerFactory.getLogger(BankingAssistantController.class);
    private static final Pattern JSON_EXTRACT_PATTERN = Pattern.compile("\"(?:question|message)\"\\s*:\\s*\"([^\"]+)\"");

    private final BankingAssistant bankingAssistant;

    public BankingAssistantController(BankingAssistant bankingAssistant) {
        this.bankingAssistant = bankingAssistant;
    }

    @PostMapping(value = "/ask", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> ask(
            @RequestBody(required = false) String rawBody,
            @RequestHeader(value = "X-Session-Id", required = false) String sessionId
    ) {
        String effectiveSessionId = (sessionId != null && !sessionId.isBlank()) ? sessionId : "default-session";
        String question = extractQuestion(rawBody);

        if (question == null || question.isBlank()) {
            return ResponseEntity.badRequest().body("Question cannot be empty.");
        }

        log.info("Processing question for session '{}': {}", effectiveSessionId, question);
        try {
            String answer = bankingAssistant.chat(effectiveSessionId, question);
            return ResponseEntity.ok(answer);
        } catch (Exception ex) {
            log.error("Error occurred while processing banking chat request: {}", ex.getMessage(), ex);
            return ResponseEntity.ok("I apologize, but I am currently experiencing difficulty processing your request. Please try again shortly.");
        }
    }

    private String extractQuestion(String raw) {
        if (raw == null) return "";
        String trimmed = raw.trim();

        // 1. Check if it's a JSON object like {"question": "..."} or {"message": "..."}
        Matcher matcher = JSON_EXTRACT_PATTERN.matcher(trimmed);
        if (matcher.find()) {
            return matcher.group(1).trim();
        }

        // 2. Check if wrapped in quotes e.g. "hi" or "\"hi\""
        if (trimmed.startsWith("\"") && trimmed.endsWith("\"") && trimmed.length() >= 2) {
            trimmed = trimmed.substring(1, trimmed.length() - 1);
        }

        // Unescape escaped quotes if any
        return trimmed.replace("\\\"", "\"").trim();
    }
}
