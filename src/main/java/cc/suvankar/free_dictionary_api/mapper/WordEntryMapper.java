package cc.suvankar.free_dictionary_api.mapper;

import cc.suvankar.free_dictionary_api.dto.MeaningDTO;
import cc.suvankar.free_dictionary_api.dto.SenseDTO;
import cc.suvankar.free_dictionary_api.dto.SoundDTO;
import cc.suvankar.free_dictionary_api.dto.SourceAttributionDTO;
import cc.suvankar.free_dictionary_api.dto.WordEntryDTO;
import cc.suvankar.free_dictionary_api.entity.SenseEntity;
import cc.suvankar.free_dictionary_api.entity.SoundEntity;
import cc.suvankar.free_dictionary_api.entity.WordEntryEntity;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.stereotype.Component;

@Component
public class WordEntryMapper {

  private final SynonymMapper synonymMapper;
  private final AntonymMapper antonymMapper;

  public WordEntryMapper(SynonymMapper synonymMapper, AntonymMapper antonymMapper) {
    this.synonymMapper = synonymMapper;
    this.antonymMapper = antonymMapper;
  }

  public WordEntryDTO entityToDto(
      final String word, final String langCode, final List<WordEntryEntity> entities) {
    if (entities == null || entities.isEmpty()) {
      return null;
    }

    WordEntryDTO dto = new WordEntryDTO();

    dto.setWord(word);
    dto.setLang(langCode);
    dto.setAttribute(SourceAttributionDTO.getInstance());

    // Using a map to ensure unique partOfSpeech per meaning
    Map<String, MeaningDTO> meaningMap = new LinkedHashMap<>();

    for (WordEntryEntity entity : entities) {
      String pos = entity.getPos().trim().toLowerCase();
      MeaningDTO meaning = meaningMap.get(pos);

      if (meaning == null) {
        meaning = new MeaningDTO();
        meaning.setPartOfSpeech(pos);
        meaning.setEtymologyText(entity.getEtymologyText());
        meaning.setSynonyms(new LinkedHashSet<>());
        meaning.setAntonyms(new LinkedHashSet<>());
        meaning.setSenses(new LinkedHashSet<>());
        meaningMap.put(pos, meaning);
      } else {
        if (meaning.getEtymologyText() == null && entity.getEtymologyText() != null) {
          meaning.setEtymologyText(entity.getEtymologyText());
        }
      }

      // Merge synonyms and antonyms
      meaning.getSynonyms().addAll(synonymMapper.entityToStringList(entity.getSynonyms()));
      meaning.getAntonyms().addAll(antonymMapper.entityToStringList(entity.getAntonyms()));

      // Using a map to ensure unique gloss + tags
      Map<String, SenseDTO> senseMap = new LinkedHashMap<>();

      if (entity.getSenses() != null) {
        for (SenseEntity senseEntity : entity.getSenses()) {
          if (senseEntity.getGlosses() != null) {
            for (String gloss : senseEntity.getGlosses()) {
              // Key: gloss text + tags (sorted for consistency)
              String tagsKey =
                  senseEntity.getTags() == null
                      ? ""
                      : String.join(",", senseEntity.getTags().stream().sorted().toList());
              String key = gloss.trim().toLowerCase() + "|" + tagsKey;

              SenseDTO sense = senseMap.get(key);
              if (sense == null) {
                sense = new SenseDTO();
                sense.setGlosses(new LinkedHashSet<>());
                sense.setTags(
                    senseEntity.getTags() == null
                        ? Set.of()
                        : new LinkedHashSet<>(senseEntity.getTags()));
                sense.setRelated(new LinkedHashSet<>());
                sense.setSynonyms(new LinkedHashSet<>());
                sense.setAntonyms(new LinkedHashSet<>());
                sense.setExamples(new LinkedHashSet<>());
                senseMap.put(key, sense);
              }
              sense.getGlosses().add(gloss);
              if (senseEntity.getRelated() != null)
                sense
                    .getRelated()
                    .addAll(RelatedMapper.entityToRelatedList(senseEntity.getRelated()));
              if (senseEntity.getSenseSynonyms() != null)
                sense
                    .getSynonyms()
                    .addAll(synonymMapper.senseEntityToStringList(senseEntity.getSenseSynonyms()));
              if (senseEntity.getSenseAntonyms() != null)
                sense
                    .getAntonyms()
                    .addAll(antonymMapper.senseEntityToStringList(senseEntity.getSenseAntonyms()));
              if (senseEntity.getExamples() != null)
                sense
                    .getExamples()
                    .addAll(ExampleMapper.entityToStringList(senseEntity.getExamples()));
            }
          }
        }
      }

      // After processing all senses for this meaning, set the merged senses
      meaning.getSenses().addAll(senseMap.values());
    }

    dto.setMeanings(new LinkedHashSet<>(meaningMap.values()));

    // Collect all sounds across all word entries.
    List<SoundEntity> allSounds =
        entities.stream()
            .filter(entity -> entity.getSounds() != null)
            .flatMap(entity -> entity.getSounds().stream())
            .filter(java.util.Objects::nonNull)
            .toList();

    // Select one primary pronunciation, combining IPA and audio if necessary.
    List<SoundDTO> selectedSounds = SoundMapper.entityToDtoList(allSounds);

    if (!selectedSounds.isEmpty()) {
      SoundDTO primarySound = selectedSounds.get(0);

      dto.setIpa(primarySound.getIpa());

      String audioUrl =
          primarySound.getMp3Url() != null && !primarySound.getMp3Url().isBlank()
              ? primarySound.getMp3Url()
              : primarySound.getOggUrl();

      dto.setAudioUrl(audioUrl);
    }

    // Expose only the selected sound instead of remapping each entry separately.
    dto.setSounds(new LinkedHashSet<>(selectedSounds));

    return dto;
  }
}
