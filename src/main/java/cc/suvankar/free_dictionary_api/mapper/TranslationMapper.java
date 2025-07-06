package cc.suvankar.free_dictionary_api.mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import cc.suvankar.free_dictionary_api.dto.TranslationDTO;
import cc.suvankar.free_dictionary_api.dto.TranslationSenseDTO;
import cc.suvankar.free_dictionary_api.entity.TranslationEntity;

@Component
public class TranslationMapper {
    public List<TranslationDTO> groupEntitiesToDto(List<TranslationEntity> entities) {
        if (entities == null || entities.isEmpty())
            return List.of();

        // Group by lang and code
        Map<String, Map<String, List<TranslationEntity>>> grouped = entities.stream()
                .collect(Collectors.groupingBy(
                        TranslationEntity::getLang,
                        Collectors.groupingBy(TranslationEntity::getCode)));

        List<TranslationDTO> result = new ArrayList<>();
        for (var langEntry : grouped.entrySet()) {
            String lang = langEntry.getKey();
            for (var codeEntry : langEntry.getValue().entrySet()) {
                String code = codeEntry.getKey();
                List<TranslationSenseDTO> senses = codeEntry.getValue().stream()
                        .map(e -> new TranslationSenseDTO(e.getSense(), e.getRoman(), e.getWord()))
                        .collect(Collectors.toList());
                result.add(new TranslationDTO(lang, code, senses));
            }
        }
        return result;
    }
}
