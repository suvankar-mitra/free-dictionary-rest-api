package cc.suvankar.free_dictionary_api.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.logging.Logger;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cc.suvankar.free_dictionary_api.dto.WordEntryDTO;
import cc.suvankar.free_dictionary_api.services.DatabaseService;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/dictionaryapi/v1")
public class WordOfTheDayController {

    private static final Logger LOG = Logger.getLogger(WordOfTheDayController.class.getName());

    private final DatabaseService databaseService;

    public WordOfTheDayController(DatabaseService databaseService) {
        this.databaseService = databaseService;
    }

    private final List<String> wordsOfTheDay = List.of(
            "balance", "kindness", "gratitude", "hope", "passion", "wellness", "adventure", "consistency", "focus",
            "laughter", "dream", "mindful", "resilience", "unite", "forgive", "community", "generosity", "organize",
            "trust", "prosper", "courage", "seek", "alignment", "change", "fearless", "love", "simplify", "elevate",
            "compassion", "beauty", "faith", "cultivate", "family", "sparkle", "awareness", "fulfilled", "play",
            "friendship", "happiness", "purpose", "abundance", "dedication", "journey", "overcome", "serenity",
            "forward", "centered", "dance", "give", "intentional", "positivity", "rise", "let go", "joy", "savor",
            "less", "healing", "free", "boundaries", "creativity", "heart", "light", "perspective", "connect",
            "spirit", "harmony", "action", "exploration", "contentment", "glow", "healing", "thoughtful", "soar",
            "calm", "transformation", "peace", "movement", "inspire", "breathe", "flourish", "create", "believe",
            "grace", "possibility", "self love", "delight", "connection", "acceptance", "integrity", "reflect",
            "thrive", "strength", "persevere", "listen", "embrace", "commit", "bloom", "grounded", "pause",
            "stillness", "meaningful", "brave", "appreciate", "renew", "clarity", "grow", "present", "vulnerability",
            "build", "shine", "streamline");

    @GetMapping("/wordoftheday")
    public ResponseEntity<WordEntryDTO> getWordOfTheDay(HttpServletRequest request) {

        // Log each request with IP
        String clientIp = request.getHeader("X-Forwarded-For");
        String userAgent = request.getHeader("User-Agent");
        if (clientIp == null) {
            clientIp = request.getRemoteAddr();
        }
        LOG.info("Request for word of the day from IP: " + clientIp + ", userAgent:" + userAgent);

        String word = getWordOfTheDayFromList();

        WordEntryDTO definition = databaseService
                .getWordEntryByWord(word, "en");
        if (definition == null) {
            LOG.info("No definition found for word of the day: " + word);
            return ResponseEntity.notFound().build();
        }

        // Return the word of the day definition
        return ResponseEntity.ok(definition);

    }

    private String getWordOfTheDayFromList() {
        LocalDate date = LocalDate.now(); // Get the current date

        // select a word of the day, based on date
        int dayOfYear = date.getDayOfYear(); // 1 to 365 or 366
        int index = (dayOfYear - 1) % wordsOfTheDay.size();
        return wordsOfTheDay.get(index);
    }

}
