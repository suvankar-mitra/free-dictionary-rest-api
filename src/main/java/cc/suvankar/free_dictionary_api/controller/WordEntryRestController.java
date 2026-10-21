package cc.suvankar.free_dictionary_api.controller;

import cc.suvankar.free_dictionary_api.dto.*;
import cc.suvankar.free_dictionary_api.mapper.*;
import cc.suvankar.free_dictionary_api.services.*;
import jakarta.servlet.http.*;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.text.*;
import java.util.*;
import java.util.logging.*;

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
      @RequestParam(value = "compact", defaultValue = "false") String compactParam,
      HttpServletRequest request) {

    boolean compact = Boolean.parseBoolean(compactParam);

    String normalizedWord = normalizeWord(word);

    if (normalizedWord == null || normalizedWord.isEmpty()) {
      ErrorResponseDTO err = new ErrorResponseDTO(
          HttpStatus.BAD_REQUEST.value(),
          "Bad Request",
          "Word must not be blank",
          request.getRequestURI(),
          word,
          null);

      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(err);
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
        "Request for word '%s' (normalized: '%s', compact: %s) from IP: %s, userAgent: %s",
        word, normalizedWord, compact, clientIp, userAgent));

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
  public ResponseEntity<?> getWordsStartingWith(
      @RequestParam("filter") String filter,
      @RequestParam(value = "page", defaultValue = "0") String pageParam,
      @RequestParam(value = "size", defaultValue = "10") String sizeParam,
      HttpServletRequest request) {

    if (filter == null || filter.isBlank()) {
      ErrorResponseDTO err = new ErrorResponseDTO(
          HttpStatus.BAD_REQUEST.value(),
          "Bad Request",
          "Filter must not be blank",
          request.getRequestURI(),
          filter,
          null);

      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(err);
    }

    filter = normalizeWord(filter);
    if (filter.length() > 50) { // Prevent overly long input strings
      ErrorResponseDTO err = new ErrorResponseDTO(
          HttpStatus.BAD_REQUEST.value(),
          "Bad Request",
          "Filter must not exceed 50 characters",
          request.getRequestURI(),
          filter,
          null);

      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(err);
    }

    int page = 0;
    try {
      page = Integer.parseInt(pageParam.trim());
      if (page < 0) page = 0;
    } catch (NumberFormatException ignored) {
    }

    int size = 10;
    try {
      size = Integer.parseInt(sizeParam.trim());
      if (size <= 0) size = 10;
      if (size > 100) size = 100; // Hard cap at 100 items per page
    } catch (NumberFormatException ignored) {
    }

    // Log request with IP
    String clientIp = request.getHeader("X-Forwarded-For");
    String userAgent = request.getHeader("User-Agent");
    if (clientIp == null) {
      clientIp = request.getRemoteAddr();
    }
    LOG.log(Level.INFO, "Request for filter '{0}' from IP: {1}, userAgent: {2}",
        new Object[]{filter, clientIp, userAgent});

    Pageable pageable = PageRequest.of(page, size);
    List<String> words = databaseService.getWordsStartingWith(filter, pageable);

    return ResponseEntity.ok(words);
  }

  @GetMapping("/translations/en/")
  public ResponseEntity<?> getTranslations(
      @RequestParam("word") String word,
      @RequestParam("pos") String pos,
      HttpServletRequest request) {

    if (word == null || word.isBlank() || pos == null || pos.isBlank()) {
      String invalidInput = (word == null || word.isBlank()) ? word : pos;
      String message = (word == null || word.isBlank())
          ? "Word must not be blank"
          : "Part of speech (pos) must not be blank";

      ErrorResponseDTO err = new ErrorResponseDTO(
          HttpStatus.BAD_REQUEST.value(),
          "Bad Request",
          message,
          request.getRequestURI(),
          invalidInput,
          null);

      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(err);
    }

    word = normalizeWord(word);
    pos = pos.trim().toLowerCase(Locale.ROOT);

    if (word.length() > 50 || pos.length() > 20) {
      String invalidInput = word.length() > 50 ? word : pos;
      String message = word.length() > 50
          ? "Word must not exceed 50 characters"
          : "Part of speech (pos) must not exceed 20 characters";

      ErrorResponseDTO err = new ErrorResponseDTO(
          HttpStatus.BAD_REQUEST.value(),
          "Bad Request",
          message,
          request.getRequestURI(),
          invalidInput,
          null);

      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(err);
    }

    // Log each request with IP
    String clientIp = request.getHeader("X-Forwarded-For");
    String userAgent = request.getHeader("User-Agent");
    if (clientIp == null) {
      clientIp = request.getRemoteAddr();
    }

    LOG.log(Level.INFO, "Request for translation for word '{0}' | {1} from IP: {2}, userAgent: {3}",
        new Object[]{word, pos, clientIp, userAgent});

    List<TranslationDTO> translationDTOs = databaseService.getWordTranslations(word, pos);

    LOG.log(Level.INFO, "Found {0} translations for {1}, {2}",
        new Object[]{translationDTOs.size(), word, pos});

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
