package cc.suvankar.free_dictionary_api.mapper;

import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cc.suvankar.free_dictionary_api.entity.SenseSynonymEntity;
import cc.suvankar.free_dictionary_api.entity.SynonymEntity;
import cc.suvankar.free_dictionary_api.services.OffensiveTermsProvider;

@Component
public class SynonymMapper {

    // Pattern to detect notes, metadata, or malformed entries
    private static final Pattern META_PATTERN = Pattern.compile(".*\\*.*|.*note.*|.*section.*|.*containing.*",
            Pattern.CASE_INSENSITIVE);

    @Autowired
    private OffensiveTermsProvider offensiveTermsProvider;

    private boolean isValidSynonym(String word) {
        String cleaned = word.replaceAll(".*\\*\\s*", "").trim();
        return !cleaned.isEmpty()
                && !META_PATTERN.matcher(cleaned).matches()
                && !offensiveTermsProvider.isOffensive(cleaned.toLowerCase())
                && cleaned.length() <= 40;
    }

    private String cleanSynonym(String word) {
        return word.replaceAll(".*\\*\\s*", "").trim();
    }

    public List<String> entityToStringList(List<SynonymEntity> entity) {
        if (entity == null || entity.isEmpty()) {
            return List.of();
        }

        return entity.stream()
                .map(SynonymEntity::getWord)
                .map(this::cleanSynonym)
                .filter(this::isValidSynonym)
                .distinct()
                .collect(Collectors.toList());
    }

    public List<String> senseEntityToStringList(List<SenseSynonymEntity> entity) {
        if (entity == null || entity.isEmpty()) {
            return List.of();
        }

        return entity.stream()
                .map(SenseSynonymEntity::getWord)
                .map(this::cleanSynonym)
                .filter(this::isValidSynonym)
                .distinct()
                .collect(Collectors.toList());
    }
}
