package cc.suvankar.free_dictionary_api.mapper;

import java.util.List;

import cc.suvankar.free_dictionary_api.entity.SenseSynonymEntity;
import cc.suvankar.free_dictionary_api.entity.SynonymEntity;

public class SynonymMapper {

    public static List<String> entityToStringList(List<SynonymEntity> entity) {
        if (entity == null || entity.isEmpty()) {
            return List.of();
        }

        return entity.stream()
                .map(SynonymEntity::getWord)
                .toList();
    }

    public static List<String> senseEntityToStringList(List<SenseSynonymEntity> entity) {
        if (entity == null || entity.isEmpty()) {
            return List.of();
        }

        return entity.stream()
                .map(SenseSynonymEntity::getWord)
                .toList();
    }
}
