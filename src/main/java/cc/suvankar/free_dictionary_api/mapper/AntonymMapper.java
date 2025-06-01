package cc.suvankar.free_dictionary_api.mapper;

import java.util.List;

import cc.suvankar.free_dictionary_api.entity.AntonymEntity;
import cc.suvankar.free_dictionary_api.entity.SenseAntonymEntity;

public class AntonymMapper {

    public static List<String> entityToStringList(List<AntonymEntity> entity) {
        if (entity == null || entity.isEmpty()) {
            return List.of();
        }

        return entity.stream()
                .map(AntonymEntity::getWord)
                .toList();
    }

    public static List<String> senseEntityToStringList(List<SenseAntonymEntity> entity) {
        if (entity == null || entity.isEmpty()) {
            return List.of();
        }

        return entity.stream()
                .map(SenseAntonymEntity::getWord)
                .toList();
    }
}
