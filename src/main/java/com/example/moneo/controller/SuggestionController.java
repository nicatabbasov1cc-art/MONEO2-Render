package com.example.moneo.controller;

import com.example.moneo.service.SuggestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/suggestions")
@RequiredArgsConstructor
public class SuggestionController {

    private final SuggestionService suggestionService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getSuggestion(@RequestParam(required = false) String note) {
        SuggestionService.SuggestionResult result = suggestionService.getSuggestion(note);

        Map<String, Object> response = new HashMap<>();
        response.put("suggestedCategory", result.suggestedCategory());
        response.put("suggestedAmount", result.suggestedAmount());

        return ResponseEntity.ok(response);
    }
}