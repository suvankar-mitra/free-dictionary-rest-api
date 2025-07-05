package cc.suvankar.free_dictionary_api.controller;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cc.suvankar.free_dictionary_api.dto.TranslationDTO;
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

    @GetMapping("/words/en")
    public ResponseEntity<List<String>> getWordsStartingWith(
            @RequestParam("filter") String filter,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {

        if (filter == null || filter.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        Pageable pageable = PageRequest.of(page, size);

        List<String> words = databaseService.getWordsStartingWith(filter, pageable);

        return ResponseEntity.ok(words);
    }

    @GetMapping("/translations/en/")
    public ResponseEntity<List<TranslationDTO>> getTranslations(@RequestParam("word") String word,
            @RequestParam("pos") String pos) {
        List<TranslationDTO> translationDTOs = databaseService.getWordTransaltions(word, pos);
        if (translationDTOs.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(translationDTOs);
    }
}
