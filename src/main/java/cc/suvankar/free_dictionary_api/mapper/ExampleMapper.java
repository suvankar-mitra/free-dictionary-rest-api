package cc.suvankar.free_dictionary_api.mapper;

import java.util.List;

import cc.suvankar.free_dictionary_api.entity.ExampleEntity;

public class ExampleMapper {

    public static List<String> entityToStringList(List<ExampleEntity> entity) {
        if (entity == null || entity.isEmpty()) {
            return List.of();
        }

        return entity.stream()
                .map(ExampleEntity::getText)
                .toList();
    }
}
