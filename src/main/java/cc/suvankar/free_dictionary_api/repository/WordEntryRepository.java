package cc.suvankar.free_dictionary_api.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import cc.suvankar.free_dictionary_api.entity.WordEntryEntity;

@Repository
public interface WordEntryRepository extends JpaRepository<WordEntryEntity, Long> {
    List<WordEntryEntity> findByWord(String word);

    List<WordEntryEntity> findByWordAndLangCode(String word, String langCode);

    @Query("SELECT w FROM WordEntryEntity w WHERE LOWER(w.word) = LOWER(:word)")
    List<WordEntryEntity> findByWordIgnoreCase(String word);

    @Query("SELECT w FROM WordEntryEntity w WHERE LOWER(w.word) = LOWER(:word) AND w.langCode = :langCode")
    List<WordEntryEntity> findByWordAndLangCodeIgnoreCase(String word, String langCode);

    @Query("SELECT DISTINCT w.word FROM WordEntryEntity w")
    Page<String> findAllWords(Pageable pageable);

    @Query("SELECT DISTINCT w.word FROM WordEntryEntity w WHERE w.word LIKE CONCAT(:prefix, '%')")
    List<String> findWordsByPrefix(String prefix, Pageable pageable);

    @Query("SELECT DISTINCT w.word FROM WordEntryEntity w WHERE LOWER(w.word) LIKE CONCAT(LOWER(:prefix), '%')")
    List<String> findWordsByPrefixIgnoreCase(String prefix, Pageable pageable);
}
