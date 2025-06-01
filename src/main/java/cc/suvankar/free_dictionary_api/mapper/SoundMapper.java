package cc.suvankar.free_dictionary_api.mapper;

import java.util.List;

import cc.suvankar.free_dictionary_api.dto.SoundDTO;
import cc.suvankar.free_dictionary_api.entity.SoundEntity;

public class SoundMapper {

    public static List<SoundDTO> entityToDtoList(List<SoundEntity> soundEntities) {
        if (soundEntities == null || soundEntities.isEmpty()) {
            return List.of();
        }

        return soundEntities.stream()
                .filter(entity -> (entity.getIpa() != null && !entity.getIpa().isEmpty())
                        || (entity.getMp3Url() != null && !entity.getMp3Url().isEmpty())
                        || (entity.getOggUrl() != null && !entity.getOggUrl().isEmpty()))
                .map(entity -> {
                    SoundDTO dto = new SoundDTO();
                    dto.setIpa(entity.getIpa());
                    dto.setOggUrl(entity.getOggUrl());
                    dto.setMp3Url(entity.getMp3Url());
                    dto.setTags(entity.getTags().stream().collect(java.util.stream.Collectors.toSet()));
                    return dto;
                })
                .toList();
    }
}
