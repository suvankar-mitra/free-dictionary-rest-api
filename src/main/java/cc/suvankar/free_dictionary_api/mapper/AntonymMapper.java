package cc.suvankar.free_dictionary_api.mapper;

import java.util.List;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cc.suvankar.free_dictionary_api.entity.AntonymEntity;
import cc.suvankar.free_dictionary_api.entity.SenseAntonymEntity;
import cc.suvankar.free_dictionary_api.services.OffensiveTermsProvider;

@Component
public class AntonymMapper {

    // Pattern to detect notes, metadata, or malformed entries
    private static final Pattern META_PATTERN = Pattern.compile(".*\\*.*|.*note.*|.*section.*|.*containing.*",
            Pattern.CASE_INSENSITIVE);

    @Autowired
    private OffensiveTermsProvider offensiveTermsProvider;

    private boolean isValidAntonym(String word) {
        String cleaned = word.replaceAll(".*\\*\\s*", "").trim();
        return !cleaned.isEmpty()
                && !META_PATTERN.matcher(cleaned).matches()
                && !offensiveTermsProvider.isOffensive(cleaned.toLowerCase())
                && cleaned.length() <= 40;
    }

    private String cleanAntonym(String word) {
        return word.replaceAll(".*\\*\\s*", "").trim();
    }

    public List<String> entityToStringList(List<AntonymEntity> entity) {
        if (entity == null || entity.isEmpty()) {
            return List.of();
        }

        return entity.stream()
                .map(AntonymEntity::getWord)
                .map(this::cleanAntonym)
                .filter(this::isValidAntonym)
                .toList();
    }

    public List<String> senseEntityToStringList(List<SenseAntonymEntity> entity) {
        if (entity == null || entity.isEmpty()) {
            return List.of();
        }

        return entity.stream()
                .map(SenseAntonymEntity::getWord)
                .map(this::cleanAntonym)
                .filter(this::isValidAntonym)
                .toList();
    }
}
