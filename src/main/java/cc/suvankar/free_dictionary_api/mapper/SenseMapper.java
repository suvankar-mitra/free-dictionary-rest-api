package cc.suvankar.free_dictionary_api.mapper;

import java.util.List;

import cc.suvankar.free_dictionary_api.entity.SenseEntity;

public class SenseMapper {

    public static List<String> entityToDefinitionList(List<SenseEntity> senseEntities) {
        if (senseEntities == null || senseEntities.isEmpty()) {
            return List.of();
        }

        return senseEntities.stream()
                .flatMap(entity -> entity.getGlosses().stream())
                .toList();
    }

    public static List<String> entityToExampleList(List<SenseEntity> senseEntities) {
        if (senseEntities == null || senseEntities.isEmpty()) {
            return List.of();
        }

        return senseEntities.stream()
                .flatMap(entity -> entity.getExamples().stream()
                        .map(example -> example.getText()))
                .toList();
    }
}
