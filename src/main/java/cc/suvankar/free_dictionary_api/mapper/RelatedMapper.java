package cc.suvankar.free_dictionary_api.mapper;

import java.util.List;

import cc.suvankar.free_dictionary_api.entity.RelatedEntity;

public class RelatedMapper {
    public static List<String> entityToRelatedList(List<RelatedEntity> entities) {
        if (entities == null || entities.isEmpty()) {
            return List.of();
        }

        return entities.stream()
                .map(entity -> entity.getWord()).toList();
    }
}
