package cc.suvankar.free_dictionary_api.repository;

import java.util.List;

import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
@Primary
@Profile("h2")
public interface WordEntryRepositoryH2 extends WordEntryRepository {
    @Query("SELECT DISTINCT w.word FROM WordEntryEntity w WHERE LOWER(w.word) LIKE CONCAT(LOWER(:prefix), '%') ORDER BY w.word")
    List<String> findWordsByPrefixIgnoreCase(String prefix, Pageable pageable);
}
