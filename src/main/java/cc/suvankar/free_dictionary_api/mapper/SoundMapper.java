
package cc.suvankar.free_dictionary_api.mapper;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

import cc.suvankar.free_dictionary_api.dto.SoundDTO;
import cc.suvankar.free_dictionary_api.entity.SoundEntity;

public class SoundMapper {

    public static List<SoundDTO> entityToDtoList(
            List<SoundEntity> soundEntities) {

        if (soundEntities == null || soundEntities.isEmpty()) {
            return List.of();
        }

        SoundEntity selectedIpa = soundEntities.stream()
                .filter(SoundMapper::hasIpa)
                .min(Comparator.comparingInt(SoundMapper::ipaPriority))
                .orElse(null);

        SoundEntity selectedAudio = soundEntities.stream()
                .filter(SoundMapper::hasAudio)
                .min(Comparator.comparingInt(SoundMapper::audioPriority))
                .orElse(null);

        if (selectedIpa == null && selectedAudio == null) {
            return List.of();
        }

        SoundDTO dto = new SoundDTO();

        if (selectedIpa != null) {
            dto.setIpa(selectedIpa.getIpa());
        }

        if (selectedAudio != null) {
            dto.setOggUrl(selectedAudio.getOggUrl());
            dto.setMp3Url(selectedAudio.getMp3Url());
        }

        Set<String> tags = soundEntities.stream()
            .filter(entity -> entity == selectedIpa
                    || entity == selectedAudio)
            .filter(entity -> entity.getTags() != null)
            .flatMap(entity -> entity.getTags().stream())
            .filter(java.util.Objects::nonNull)
            .collect(Collectors.toSet());

        if (!tags.isEmpty()) {
            dto.setTags(tags);
        }

        return List.of(dto);
    }

    private static boolean hasIpa(SoundEntity entity) {
        return entity != null
                && entity.getIpa() != null
                && !entity.getIpa().isBlank();
    }

    private static boolean hasAudio(SoundEntity entity) {
        return entity != null
                && ((entity.getMp3Url() != null
                        && !entity.getMp3Url().isBlank())
                    || (entity.getOggUrl() != null
                        && !entity.getOggUrl().isBlank()));
    }

    private static boolean hasTag(SoundEntity entity, String expected) {
        return entity.getTags() != null
                && entity.getTags().stream()
                        .filter(tag -> tag != null)
                        .anyMatch(tag -> tag.equalsIgnoreCase(expected));
    }

    private static int ipaPriority(SoundEntity entity) {
        if (entity.getTags() == null || entity.getTags().isEmpty()) {
            return 0;
        }
        if (hasTag(entity, "US")) {
            return 1;
        }
        if (hasTag(entity, "General-American")) {
            return 2;
        }
        if (hasTag(entity, "Canada")) {
            return 3;
        }
        if (hasTag(entity, "Received-Pronunciation")
                || hasTag(entity, "UK")) {
            return 4;
        }
        return 5;
    }

    private static int audioPriority(SoundEntity entity) {
        if (hasTag(entity, "US")) {
            return 0;
        }
        if (hasTag(entity, "General-American")) {
            return 1;
        }
        if (hasTag(entity, "Canada")) {
            return 2;
        }
        if (entity.getTags() == null || entity.getTags().isEmpty()) {
            return 3;
        }
        return 4;
    }
}
