package cc.suvankar.free_dictionary_api.controller;

import java.text.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cc.suvankar.free_dictionary_api.dto.ErrorResponseDTO;
import cc.suvankar.free_dictionary_api.dto.TranslationDTO;
import cc.suvankar.free_dictionary_api.dto.WordEntryCompactDTO;
import cc.suvankar.free_dictionary_api.dto.WordEntryDTO;
import cc.suvankar.free_dictionary_api.mapper.WordEntryCompactMapper;
import cc.suvankar.free_dictionary_api.services.DatabaseService;
import cc.suvankar.free_dictionary_api.services.OffensiveTermsProvider;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/dictionaryapi/v1")
public class WordEntryRestController {
    private static final Logger LOG = Logger.getLogger(WordEntryRestController.class.getName());

    private final DatabaseService databaseService;
    private final OffensiveTermsProvider offensiveTermsProvider;
    private final WordEntryCompactMapper compactMapper;

    private static final int MAX_WORD_LENGTH = 100;

    public WordEntryRestController(DatabaseService databaseService, 
                OffensiveTermsProvider offensiveTermsProvider,
                WordEntryCompactMapper compactMapper) {
        this.databaseService = databaseService;
        this.offensiveTermsProvider = offensiveTermsProvider;
        this.compactMapper = compactMapper;
    }

    @GetMapping("/definitions/en/{word}")
    public ResponseEntity<?> getDefinitions(
        @PathVariable String word,
        @RequestParam(value = "compact", defaultValue = "false") boolean compact,
        HttpServletRequest request) {

        String normalizedWord = normalizeWord(word);

        if (normalizedWord == null || normalizedWord.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        // Reject excessively long input before any database lookup.
        int wordLength = normalizedWord.codePointCount(
            0, normalizedWord.length());

        if (wordLength > MAX_WORD_LENGTH) {
            ErrorResponseDTO err = new ErrorResponseDTO(
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                "Word must not exceed " + MAX_WORD_LENGTH + " characters",
                request.getRequestURI(),
                word,
                null);

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(err);
        }

        // Log each request with IP
        String clientIp = request.getHeader("X-Forwarded-For");
        String userAgent = request.getHeader("User-Agent");

        if (clientIp == null) {
            clientIp = request.getRemoteAddr();
        }

        LOG.info(String.format(
            "Request for word '%s' (normalized: '%s') from IP: %s, userAgent: %s",
            word, normalizedWord, clientIp, userAgent));

        // Check for offensive terms after normalization
        if (offensiveTermsProvider.isOffensive(normalizedWord)) {
            ErrorResponseDTO err = new ErrorResponseDTO(
                HttpStatus.NOT_FOUND.value(),
                "Not Found",
                "No definition found for word: " + word,
                request.getRequestURI(),
                word,
                null);

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(err);
        }

        WordEntryDTO definition =
            databaseService.getWordEntryByWord(normalizedWord, "en");

        if (definition == null) {
            LOG.info("No definition found for word: " + normalizedWord);

            ErrorResponseDTO err = new ErrorResponseDTO(
                HttpStatus.NOT_FOUND.value(),
                "Not Found",
                "No definition found for word: " + word,
                request.getRequestURI(),
                word,
                null);

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(err);
        }

        if (compact) {
            WordEntryCompactDTO compactDef =
                compactMapper.makeWordDefinitionCompact(definition);

            if (compactDef == null) {
                ErrorResponseDTO err = new ErrorResponseDTO(
                    HttpStatus.NOT_FOUND.value(),
                    "Not Found",
                    "No definition found for word: " + word,
                    request.getRequestURI(),
                    word,
                    null);

                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(err);
            }

            return ResponseEntity.ok(compactDef);
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
        LOG.log(Level.INFO,"Request for filter '{0}' from IP: {1}, userAgent:{2}", new Object[]{filter, clientIp, userAgent});

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
        LOG.log(Level.INFO,"Request for translation for word '{0}' | {1} from IP: {2}, userAgent: {3}", new Object[]{word, pos, clientIp, userAgent});
        List<TranslationDTO> translationDTOs = databaseService.getWordTransaltions(word, pos);
        LOG.log(Level.INFO, "Found {0} translations for {1}, {2}", new Object[]{translationDTOs.size(), word, pos});

        if (translationDTOs.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(translationDTOs);
    }

    public static String normalizeWord(String word) {
        if (word == null) {
            return null;
        }

        return Normalizer.normalize(word, Normalizer.Form.NFKC)
            .toLowerCase(Locale.ROOT)
            .strip();
    }
}
