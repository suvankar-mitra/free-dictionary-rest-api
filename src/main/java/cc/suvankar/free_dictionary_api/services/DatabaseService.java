package cc.suvankar.free_dictionary_api.services;

import cc.suvankar.free_dictionary_api.dto.*;
import cc.suvankar.free_dictionary_api.entity.*;
import cc.suvankar.free_dictionary_api.mapper.*;
import cc.suvankar.free_dictionary_api.repository.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.cache.annotation.*;
import org.springframework.data.domain.*;
import org.springframework.stereotype.*;
import org.springframework.transaction.annotation.*;

import java.util.*;
import java.util.logging.*;

@Service
public class DatabaseService {
  private static final Logger LOG = Logger.getLogger(DatabaseService.class.getName());

  private final WordEntryRepository wordEntryRepository;
  private final WordEntryMapper wordEntryMapper;
  private final TranslationMapper translationMapper;

  @Value("${spring.profiles.active:}")
  private String activeProfile;

  public DatabaseService(WordEntryRepository wordEntryRepository, WordEntryMapper wordEntryMapper,
                         TranslationMapper translationMapper) {
    this.wordEntryRepository = wordEntryRepository;
    this.wordEntryMapper = wordEntryMapper;
    this.translationMapper = translationMapper;
  }

  @Transactional(readOnly = true)
  @Cacheable(
      value = "wordEntries",
      key = "#word + '_' + #langCode",
      unless = "#result == null"
  )
  public WordEntryDTO getWordEntryByWord(final String word, final String langCode) {
    if (word == null || word.isEmpty()) {
      LOG.warning("Attempted to retrieve WordEntryDTO with a null or empty word");
      return null;
    }

    final String normalizedWord = word.trim();

//    List<WordEntryEntity> entities = wordEntryRepository.findByWordAndLangCode(normalizedWord, langCode);
//    if (entities.isEmpty()) {
//      //LOG.log(Level.INFO, "No WordEntries found for word: {0}", normalizedWord);
//      return null;
//    }
//
//    LOG.log(Level.INFO, "Found {0} WordEntries for word: {1}", new Object[]{entities.size(), normalizedWord});
//    return wordEntryMapper.entityToDto(normalizedWord, langCode, entities);

    long queryStart = System.nanoTime();

    List<WordEntryEntity> entities =
        wordEntryRepository.findByWordAndLangCode(normalizedWord, langCode);

    long queryEnd = System.nanoTime();

    LOG.info(String.format(
        "PERF word=%s repositoryQuery=%.3fms",
        normalizedWord,
        (queryEnd - queryStart) / 1_000_000.0
    ));

    if (entities.isEmpty()) {
      return null;
    }

    long mappingStart = System.nanoTime();

    WordEntryDTO dto =
        wordEntryMapper.entityToDto(normalizedWord, langCode, entities);

    long mappingEnd = System.nanoTime();

    LOG.info(String.format(
        "PERF word=%s entityToDto=%.3fms",
        normalizedWord,
        (mappingEnd - mappingStart) / 1_000_000.0
    ));

    return dto;
  }

  public Page<String> getWordsPaginated(Pageable pageable) {
    return wordEntryRepository.findAllWords(pageable);
  }

  public Long getWordEntryCount() {
    return wordEntryRepository.count();
  }

  @Cacheable(
      value = "wordsStartingWith",
      key = "#filter + '_' + #limit.pageNumber + '_' + #limit.pageSize",
      unless = "#result == null"
  )
  public List<String> getWordsStartingWith(String filter, Pageable limit) {
    if (filter == null) {
      filter = "";
    }
    String pattern;
    if ("docker".equals(activeProfile)) {
      pattern = "^" + filter + "[a-z]*$";
      if (filter.contains(" ")) {
        pattern = "^" + filter + "[a-z ]*$";
      }
    } else {
      pattern = filter;
    }
    List<String> words = wordEntryRepository.findWordsByPrefixIgnoreCase(pattern, limit);
    return words;
  }

  @Cacheable(
      value = "TranslationsByWordAndPos",
      key = "#word + '_' + #pos",
      unless = "#result == null"
  )
  public List<TranslationDTO> getWordTranslations(String word, String pos) {
    List<TranslationEntity> translationEntities = wordEntryRepository.getTranslationsByWordAndPos(word, pos);
    if (translationEntities.isEmpty()) {
      return List.of();
    }

    return translationMapper.groupEntitiesToDto(translationEntities);
  }
}
