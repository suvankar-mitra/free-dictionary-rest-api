package cc.suvankar.free_dictionary_api.controller;

import java.util.List;
import java.util.logging.Logger;

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
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/dictionaryapi/v1")
public class WordEntryRestController {
    private static final Logger LOG = Logger.getLogger(WordEntryRestController.class.getName());

    private final DatabaseService databaseService;
    private final OffensiveTermsProvider offensiveTermsProvider;

    public WordEntryRestController(DatabaseService databaseService, OffensiveTermsProvider offensiveTermsProvider) {
        this.databaseService = databaseService;
        this.offensiveTermsProvider = offensiveTermsProvider;
    }

    @GetMapping("/definitions/en/{word}")
    public ResponseEntity<WordEntryDTO> getDefinitions(
            @PathVariable String word,
            HttpServletRequest request) {
        if (word == null || word.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        // Log each request with IP
        String clientIp = request.getHeader("X-Forwarded-For");
        String userAgent = request.getHeader("User-Agent");
        if (clientIp == null) {
            clientIp = request.getRemoteAddr();
        }
        LOG.info("Request for word '" + word + "' from IP: " + clientIp + ", userAgent:" + userAgent);

        // Check for offensive terms
        if (offensiveTermsProvider.isOffensive(word)) {
            return ResponseEntity.notFound().build();
        }

        WordEntryDTO definition = databaseService
                .getWordEntryByWord(word, "en");
        if (definition == null) {
            // Give another try with a normalized word
            String normalizedWord = word.trim().toLowerCase();
            definition = databaseService.getWordEntryByWord(normalizedWord, "en");

            if (definition == null) {
                LOG.info("No definition found for word: " + word);
                return ResponseEntity.notFound().build();
            }
        }
        return ResponseEntity.ok(definition);
    }

    @GetMapping("/words/en/")
    public ResponseEntity<List<String>> getWordsStartingWith(
            @RequestParam("filter") String filter,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            HttpServletRequest request) {

        if (filter == null || filter.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        // Log each request with IP
        String clientIp = request.getHeader("X-Forwarded-For");
        String userAgent = request.getHeader("User-Agent");
        if (clientIp == null) {
            clientIp = request.getRemoteAddr();
        }
        LOG.info("Request for filter '" + filter + "' from IP: " + clientIp + ", userAgent:" + userAgent);

        Pageable pageable = PageRequest.of(page, size);

        List<String> words = databaseService.getWordsStartingWith(filter, pageable);

        return ResponseEntity.ok(words);
    }

    @GetMapping("/translations/en/")
    public ResponseEntity<List<TranslationDTO>> getTranslations(
            @RequestParam("word") String word,
            @RequestParam("pos") String pos,
            HttpServletRequest request) {

        // Log each request with IP
        String clientIp = request.getHeader("X-Forwarded-For");
        String userAgent = request.getHeader("User-Agent");
        if (clientIp == null) {
            clientIp = request.getRemoteAddr();
        }
        LOG.info("Request for translation for word '" + word + "|" + pos + "' from IP: " + clientIp + ", userAgent:"
                + userAgent);
        List<TranslationDTO> translationDTOs = databaseService.getWordTransaltions(word, pos);
        LOG.info("Found " + translationDTOs.size() + " translations for " + word + ", " + pos);

        if (translationDTOs.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(translationDTOs);
    }
}
