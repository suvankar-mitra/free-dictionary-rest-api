package cc.suvankar.free_dictionary_api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cc.suvankar.free_dictionary_api.dto.WordEntryDTO;
import cc.suvankar.free_dictionary_api.services.DatabaseService;
import cc.suvankar.free_dictionary_api.services.OffensiveTermsProvider;

@RestController
@RequestMapping("/dictionaryapi/v1")
public class WordEntryRestController {
    private final DatabaseService databaseService;
    private final OffensiveTermsProvider offensiveTermsProvider;

    public WordEntryRestController(DatabaseService databaseService, OffensiveTermsProvider offensiveTermsProvider) {
        this.databaseService = databaseService;
        this.offensiveTermsProvider = offensiveTermsProvider;
    }

    @GetMapping("/definitions/en/{word}")
    public ResponseEntity<WordEntryDTO> getDefinitions(
            @PathVariable String word) {
        if (word == null || word.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        // Check for offensive terms
        if (offensiveTermsProvider.isOffensive(word)) {
            return ResponseEntity.notFound().build();
        }

        WordEntryDTO definition = databaseService
                .getWordEntryByWord(word, "en");
        if (definition == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(definition);
    }
}
