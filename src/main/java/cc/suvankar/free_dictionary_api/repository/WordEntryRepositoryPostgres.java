package cc.suvankar.free_dictionary_api.repository;

import java.util.*;
import org.springframework.context.annotation.*;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.*;
import org.springframework.stereotype.*;

@Repository
@Primary
@Profile("docker")
public interface WordEntryRepositoryPostgres extends WordEntryRepository {

  //  Optimizes case-insensitive prefix searches for dictionary autocomplete.
  //  Supports queries using:
  //  WHERE lower(word) LIKE 'prefix%'
  //  text_pattern_ops enables efficient prefix matching with B-tree.
  //  CONCURRENTLY avoids blocking regular table writes during index creation.
  //
  //  CREATE INDEX CONCURRENTLY idx_word_entry_lower_word_prefix
  //  ON word_entry (lower(word) text_pattern_ops);
  @Query(
      value =
          """
              SELECT DISTINCT w.word
              FROM word_entry w
              WHERE lower(w.word) LIKE :pattern
              ORDER BY w.word
              """,
      nativeQuery = true)
  @Override
  List<String> findWordsByPrefixIgnoreCase(@Param("pattern") String pattern, Pageable pageable);
}
