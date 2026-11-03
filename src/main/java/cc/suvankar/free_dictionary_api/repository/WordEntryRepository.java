
package cc.suvankar.free_dictionary_api.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import cc.suvankar.free_dictionary_api.entity.TranslationEntity;
import cc.suvankar.free_dictionary_api.entity.WordEntryEntity;

@Repository
public interface WordEntryRepository extends JpaRepository<WordEntryEntity, Long> {

    List<WordEntryEntity> findByWordOrderBySourceFileAscSourceLineNumberAsc(
            String word);

    List<WordEntryEntity> findByWordAndLangCodeOrderBySourceFileAscSourceLineNumberAsc(
            String word, String langCode);

    @Query("""
            SELECT w FROM WordEntryEntity w
            WHERE LOWER(w.word) = LOWER(:word)
            ORDER BY w.sourceFile ASC, w.sourceLineNumber ASC
            """)
    List<WordEntryEntity> findByWordIgnoreCase(
            @Param("word") String word);

    @Query("""
            SELECT w FROM WordEntryEntity w
            WHERE LOWER(w.word) = LOWER(:word)
              AND w.langCode = :langCode
            ORDER BY w.sourceFile ASC, w.sourceLineNumber ASC
            """)
    List<WordEntryEntity> findByWordAndLangCodeIgnoreCase(
            @Param("word") String word,
            @Param("langCode") String langCode);

    @Query("SELECT DISTINCT w.word FROM WordEntryEntity w")
    Page<String> findAllWords(Pageable pageable);

    @Query("""
            SELECT DISTINCT w.word
            FROM WordEntryEntity w
            WHERE w.word LIKE CONCAT(:prefix, '%')
            """)
    List<String> findWordsByPrefix(
            @Param("prefix") String prefix,
            Pageable pageable);

    // This method will not be called from here in prod, check
    // WordEntryRepositoryPostgres class
    @Query("""
            SELECT DISTINCT w.word
            FROM WordEntryEntity w
            WHERE LOWER(w.word) LIKE CONCAT(LOWER(:prefix), '%')
              AND LOWER(w.word) NOT LIKE '% %'
            ORDER BY w.word
            """)
    List<String> findWordsByPrefixIgnoreCase(
            @Param("prefix") String prefix,
            Pageable pageable);

    @Query("""
            SELECT t FROM WordEntryEntity we
            JOIN we.senses s
            JOIN s.translations t
            WHERE we.word = :word
              AND we.pos = :pos
            """)
    List<TranslationEntity> getTranslationsByWordAndPos(
            @Param("word") String word,
            @Param("pos") String pos);
}
