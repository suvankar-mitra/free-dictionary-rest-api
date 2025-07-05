package cc.suvankar.free_dictionary_api.services;

import java.util.List;
import java.util.logging.Logger;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import cc.suvankar.free_dictionary_api.dto.TranslationDTO;
import cc.suvankar.free_dictionary_api.dto.WordEntryDTO;
import cc.suvankar.free_dictionary_api.entity.TranslationEntity;
import cc.suvankar.free_dictionary_api.entity.WordEntryEntity;
import cc.suvankar.free_dictionary_api.mapper.TranslationMapper;
import cc.suvankar.free_dictionary_api.mapper.WordEntryMapper;
import cc.suvankar.free_dictionary_api.repository.WordEntryRepository;
import jakarta.transaction.Transactional;

@Service
public class DatabaseService {
    private static final Logger LOG = Logger.getLogger(DatabaseService.class.getName());

    private final WordEntryRepository wordEntryRepository;
    private final WordEntryMapper wordEntryMapper;
    private final TranslationMapper translationMapper;

    public DatabaseService(WordEntryRepository wordEntryRepository, WordEntryMapper wordEntryMapper,
            TranslationMapper translationMapper) {
        this.wordEntryRepository = wordEntryRepository;
        this.wordEntryMapper = wordEntryMapper;
        this.translationMapper = translationMapper;
    }

    @Transactional
    @Cacheable(value = "wordEntries", key = "#word + '_' + #langCode")
    public WordEntryDTO getWordEntryByWord(final String word, final String langCode) {
        if (word == null || word.isEmpty()) {
            LOG.warning("Attempted to retrieve WordEntryDTO with a null or empty word");
            return null;
        }

        final String normalizedWord = word.trim();

        List<WordEntryEntity> entities = wordEntryRepository.findByWordAndLangCode(normalizedWord, langCode);
        if (entities.isEmpty()) {
            LOG.info("No WordEntries found for word: " + normalizedWord);
            return null;
        }

        LOG.info("Found " + entities.size() + " WordEntries for word: " + normalizedWord);
        return wordEntryMapper.entityToDto(normalizedWord, langCode, entities);
    }

    public Page<String> getWordsPaginated(Pageable pageable) {
        return wordEntryRepository.findAllWords(pageable);
    }

    public Long getWordEntryCount() {
        return wordEntryRepository.count();
    }

    @Cacheable(value = "wordsStartingWith", key = "#filter + '_' + #limit.pageNumber + '_' + #limit.pageSize")
    public List<String> getWordsStartingWith(String filter, Pageable limit) {
        if (filter == null) {
            filter = "";
        }
        return wordEntryRepository.findWordsByPrefixIgnoreCase(filter, limit);
    }

    public List<TranslationDTO> getWordTransaltions(String word, String pos) {
        List<TranslationEntity> translationEntities = wordEntryRepository.getTranslationsByWordAndPos(word, pos);
        if (translationEntities.isEmpty()) {
            return List.of();
        }

        return translationEntities.stream().map(translationMapper::entityToDto).toList();
    }
}
