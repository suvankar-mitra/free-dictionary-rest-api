package cc.suvankar.free_dictionary_api.mapper;

import java.util.HashSet;
import java.util.List;
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

                Set<MeaningDTO> meanings = new HashSet<>(entities.size());

                for (WordEntryEntity entity : entities) {
                        MeaningDTO meaning = new MeaningDTO();

                        meaning.setPartOfSpeech(entity.getPos());
                        meaning.setEtymologyText(entity.getEtymologyText());
                        meaning.setSynonyms(new HashSet<>(synonymMapper.entityToStringList(entity.getSynonyms())));
                        meaning.setAntonyms(new HashSet<>(antonymMapper.entityToStringList(entity.getAntonyms())));

                        if (entity.getSenses() != null) {
                                Set<SenseDTO> senses = new HashSet<>(entity.getSenses().size());
                                for (SenseEntity senseEntity : entity.getSenses()) {
                                        SenseDTO senseDTO = new SenseDTO();

                                        senseDTO.setGlosses(new HashSet<>(senseEntity.getGlosses()));
                                        senseDTO.setTags(new HashSet<>(senseEntity.getTags()));
                                        senseDTO
                                                        .setRelated(new HashSet<>(RelatedMapper.entityToRelatedList(
                                                                        senseEntity.getRelated())));
                                        senseDTO.setSynonyms(
                                                        new HashSet<>(synonymMapper.senseEntityToStringList(
                                                                        senseEntity.getSenseSynonyms())));
                                        senseDTO.setAntonyms(
                                                        new HashSet<>(antonymMapper.senseEntityToStringList(
                                                                        senseEntity.getSenseAntonyms())));
                                        senseDTO
                                                        .setExamples(new HashSet<>(ExampleMapper.entityToStringList(
                                                                        senseEntity.getExamples())));

                                        senses.add(senseDTO);
                                }
                                meaning.setSenses(senses);
                        }
                        meanings.add(meaning);
                }

                dto.setMeanings(meanings);

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
