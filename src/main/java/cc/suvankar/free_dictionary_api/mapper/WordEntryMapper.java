package cc.suvankar.free_dictionary_api.mapper;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Component;

import cc.suvankar.free_dictionary_api.dto.MeaningDTO;
import cc.suvankar.free_dictionary_api.dto.SenseDTO;
import cc.suvankar.free_dictionary_api.dto.SoundDTO;
import cc.suvankar.free_dictionary_api.dto.SourceAttributionDTO;
import cc.suvankar.free_dictionary_api.dto.WordEntryDTO;
import cc.suvankar.free_dictionary_api.entity.SenseEntity;
import cc.suvankar.free_dictionary_api.entity.WordEntryEntity;

@Component
public class WordEntryMapper {

        private final SynonymMapper synonymMapper;
        private final AntonymMapper antonymMapper;

        public WordEntryMapper(SynonymMapper synonymMapper, AntonymMapper antonymMapper) {
                this.synonymMapper = synonymMapper;
                this.antonymMapper = antonymMapper;
        }

        public WordEntryDTO entityToDto(final String word, final String langCode,
                        final List<WordEntryEntity> entities) {
                if (entities == null || entities.isEmpty()) {
                        return null;
                }

                WordEntryDTO dto = new WordEntryDTO();

                dto.setWord(word);
                dto.setLang(langCode);
                dto.setAttribute(SourceAttributionDTO.getInstance());

                // Using a map to ensure unique partOfSpeech per meaning
                Map<String, MeaningDTO> meaningMap = new HashMap<>();

                for (WordEntryEntity entity : entities) {
                        String pos = entity.getPos().trim().toLowerCase();
                        MeaningDTO meaning = meaningMap.get(pos);

                        if (meaning == null) {
                                meaning = new MeaningDTO();
                                meaning.setPartOfSpeech(pos);
                                meaning.setEtymologyText(entity.getEtymologyText());
                                meaning.setSynonyms(new HashSet<>());
                                meaning.setAntonyms(new HashSet<>());
                                meaning.setSenses(new HashSet<>());
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
                        Map<String, SenseDTO> senseMap = new HashMap<>();

                        if (entity.getSenses() != null) {
                                for (SenseEntity senseEntity : entity.getSenses()) {
                                        if (senseEntity.getGlosses() != null) {
                                                for (String gloss : senseEntity.getGlosses()) {
                                                        // Key: gloss text + tags (sorted for consistency)
                                                        String tagsKey = senseEntity.getTags() == null ? ""
                                                                        : String.join(",", senseEntity.getTags()
                                                                                        .stream().sorted().toList());
                                                        String key = gloss.trim().toLowerCase() + "|" + tagsKey;

                                                        SenseDTO sense = senseMap.get(key);
                                                        if (sense == null) {
                                                                sense = new SenseDTO();
                                                                sense.setGlosses(new HashSet<>());
                                                                sense.setTags(senseEntity.getTags() == null ? Set.of()
                                                                                : new HashSet<>(senseEntity.getTags()));
                                                                sense.setRelated(new HashSet<>());
                                                                sense.setSynonyms(new HashSet<>());
                                                                sense.setAntonyms(new HashSet<>());
                                                                sense.setExamples(new HashSet<>());
                                                                senseMap.put(key, sense);
                                                        }
                                                        sense.getGlosses().add(gloss);
                                                        if (senseEntity.getRelated() != null)
                                                                sense.getRelated().addAll(RelatedMapper
                                                                                .entityToRelatedList(senseEntity
                                                                                                .getRelated()));
                                                        if (senseEntity.getSenseSynonyms() != null)
                                                                sense.getSynonyms().addAll(synonymMapper
                                                                                .senseEntityToStringList(senseEntity
                                                                                                .getSenseSynonyms()));
                                                        if (senseEntity.getSenseAntonyms() != null)
                                                                sense.getAntonyms().addAll(antonymMapper
                                                                                .senseEntityToStringList(senseEntity
                                                                                                .getSenseAntonyms()));
                                                        if (senseEntity.getExamples() != null)
                                                                sense.getExamples().addAll(ExampleMapper
                                                                                .entityToStringList(senseEntity
                                                                                                .getExamples()));
                                                }
                                        }
                                }
                        }

                        // After processing all senses for this meaning, set the merged senses
                        meaning.getSenses().addAll(senseMap.values());
                }

                dto.setMeanings(new HashSet<>(meaningMap.values()));

                // get first IPA
                String ipa = entities.stream().filter(entity -> entity.getSounds() != null)
                                .flatMap(entity -> entity.getSounds().stream())
                                .filter(sound -> sound != null && sound.getIpa() != null && !sound.getIpa().isEmpty())
                                .map(sound -> sound.getIpa())
                                .findFirst()
                                .orElse(null);
                dto.setIpa(ipa);

                // get first Audio URL
                String audioUrl = entities.stream().filter(entity -> entity.getSounds() != null)
                                .flatMap(entity -> entity.getSounds().stream())
                                .filter(sound -> sound != null && sound.getMp3Url() != null
                                                && !sound.getMp3Url().isEmpty())
                                .map(sound -> sound.getMp3Url())
                                .findFirst()
                                .orElseGet(() -> entities.stream().filter(entity -> entity.getSounds() != null)
                                                .flatMap(entity -> entity.getSounds().stream())
                                                .filter(sound -> sound != null && sound.getOggUrl() != null
                                                                && !sound.getOggUrl().isEmpty())
                                                .map(sound -> sound.getOggUrl())
                                                .findFirst()
                                                .orElse(null));
                dto.setAudioUrl(audioUrl);

                // Collect all sounds from all entities and convert them to DTOs
                Set<SoundDTO> sounds = new HashSet<>();
                entities.stream()
                                .filter(entity -> entity.getSounds() != null)
                                .forEach(entity -> sounds.addAll(SoundMapper.entityToDtoList(entity.getSounds())));
                dto.setSounds(sounds);

                return dto;
        }

}
