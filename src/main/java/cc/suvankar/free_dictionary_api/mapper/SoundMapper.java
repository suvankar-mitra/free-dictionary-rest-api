package cc.suvankar.free_dictionary_api.mapper;

import cc.suvankar.free_dictionary_api.dto.*;
import cc.suvankar.free_dictionary_api.entity.*;
import java.util.*;
import java.util.stream.*;

public class SoundMapper {

  public static List<SoundDTO> entityToDtoList(List<SoundEntity> soundEntities) {

    if (soundEntities == null || soundEntities.isEmpty()) {
      return List.of();
    }

    return soundEntities.stream()
        .filter(Objects::nonNull)
        .filter(SoundMapper::hasContent)
        .map(SoundMapper::toDto)
        .collect(
            Collectors.toMap(
                SoundMapper::deduplicationKey,
                dto -> dto,
                (existing, replacement) -> existing,
                LinkedHashMap::new))
        .values()
        .stream()
        .toList();
  }

  private static SoundDTO toDto(SoundEntity entity) {
    SoundDTO dto = new SoundDTO();

    dto.setIpa(entity.getIpa());
    dto.setOggUrl(entity.getOggUrl());
    dto.setMp3Url(entity.getMp3Url());

    if (entity.getTags() != null) {
      dto.setTags(
          entity.getTags().stream()
              .filter(Objects::nonNull)
              .collect(Collectors.toCollection(LinkedHashSet::new)));
    }

    return dto;
  }

  private static boolean hasContent(SoundEntity entity) {
    return hasIpa(entity) || hasAudio(entity);
  }

  private static boolean hasIpa(SoundEntity entity) {
    return entity.getIpa() != null && !entity.getIpa().isBlank();
  }

  private static boolean hasAudio(SoundEntity entity) {
    return (entity.getMp3Url() != null && !entity.getMp3Url().isBlank())
        || (entity.getOggUrl() != null && !entity.getOggUrl().isBlank());
  }

  private static String deduplicationKey(SoundDTO dto) {
    String tags =
        dto.getTags() == null
            ? ""
            : dto.getTags().stream().sorted().collect(Collectors.joining(","));

    return String.join(
        "|",
        Objects.toString(dto.getIpa(), ""),
        Objects.toString(dto.getMp3Url(), ""),
        Objects.toString(dto.getOggUrl(), ""),
        tags);
  }
}
