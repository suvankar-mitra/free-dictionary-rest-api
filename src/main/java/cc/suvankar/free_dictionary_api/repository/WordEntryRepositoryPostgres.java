package cc.suvankar.free_dictionary_api.repository;

import java.util.List;

import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
@Primary
@Profile("docker")
public interface WordEntryRepositoryPostgres extends WordEntryRepository {
    @Query(value = "SELECT DISTINCT w.word FROM word_entry w WHERE w.word ~* :pattern ORDER BY w.word", nativeQuery = true)
    @Override
    List<String> findWordsByPrefixIgnoreCase(@Param("pattern") String pattern, Pageable pageable);
}
