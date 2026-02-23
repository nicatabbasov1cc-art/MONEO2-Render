package com.example.moneo.service;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;

@Service
public class SuggestionService {

    // Keyword → Category mapping
    private static final Map<String, String> KEYWORD_CATEGORY_MAP = Map.ofEntries(
            Map.entry("bravo", "Qida"),
            Map.entry("market", "Qida"),
            Map.entry("ərzaq", "Qida"),
            Map.entry("yemek", "Qida"),
            Map.entry("restoran", "Qida"),
            Map.entry("kafe", "Qida"),
            Map.entry("mcdonalds", "Qida"),
            Map.entry("pizza", "Qida"),
            Map.entry("avtomobil", "Nəqliyyat"),
            Map.entry("taksi", "Nəqliyyat"),
            Map.entry("uber", "Nəqliyyat"),
            Map.entry("bolt", "Nəqliyyat"),
            Map.entry("avtobus", "Nəqliyyat"),
            Map.entry("metro", "Nəqliyyat"),
            Map.entry("benzin", "Nəqliyyat"),
            Map.entry("kino", "Əyləncə"),
            Map.entry("oyun", "Əyləncə"),
            Map.entry("konsert", "Əyləncə"),
            Map.entry("netflix", "Əyləncə"),
            Map.entry("spotify", "Əyləncə"),
            Map.entry("aptek", "Sağlamlıq"),
            Map.entry("həkim", "Sağlamlıq"),
            Map.entry("klinika", "Sağlamlıq"),
            Map.entry("dərman", "Sağlamlıq"),
            Map.entry("işıq", "Kommunal"),
            Map.entry("qaz", "Kommunal"),
            Map.entry("su", "Kommunal"),
            Map.entry("internet", "Kommunal"),
            Map.entry("telefon", "Kommunal"),
            Map.entry("maaş", "Maaş"),
            Map.entry("bonus", "Maaş"),
            Map.entry("kurs", "Təhsil"),
            Map.entry("universitet", "Təhsil"),
            Map.entry("kitab", "Təhsil")
    );

    // Keyword → Amount mapping
    private static final Map<String, BigDecimal> KEYWORD_AMOUNT_MAP = Map.ofEntries(
            Map.entry("bravo", new BigDecimal("30")),
            Map.entry("market", new BigDecimal("25")),
            Map.entry("restoran", new BigDecimal("20")),
            Map.entry("kafe", new BigDecimal("10")),
            Map.entry("taksi", new BigDecimal("5")),
            Map.entry("uber", new BigDecimal("6")),
            Map.entry("bolt", new BigDecimal("5")),
            Map.entry("metro", new BigDecimal("0.50")),
            Map.entry("benzin", new BigDecimal("50")),
            Map.entry("netflix", new BigDecimal("15")),
            Map.entry("spotify", new BigDecimal("5")),
            Map.entry("aptek", new BigDecimal("15")),
            Map.entry("işıq", new BigDecimal("40")),
            Map.entry("internet", new BigDecimal("25"))
    );

    public SuggestionResult getSuggestion(String note) {
        if (note == null || note.isBlank()) {
            return SuggestionResult.empty();
        }

        String lowerNote = note.toLowerCase().trim();

        String suggestedCategory = null;
        BigDecimal suggestedAmount = null;

        for (Map.Entry<String, String> entry : KEYWORD_CATEGORY_MAP.entrySet()) {
            if (lowerNote.contains(entry.getKey())) {
                suggestedCategory = entry.getValue();
                suggestedAmount = KEYWORD_AMOUNT_MAP.get(entry.getKey());
                break;
            }
        }

        return new SuggestionResult(suggestedCategory, suggestedAmount);
    }

    public record SuggestionResult(String suggestedCategory, BigDecimal suggestedAmount) {
        public static SuggestionResult empty() {
            return new SuggestionResult(null, null);
        }
    }
}