package cc.suvankar.free_dictionary_api.mapper;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import org.springframework.stereotype.Component;

import cc.suvankar.free_dictionary_api.dto.MeaningDTO;
import cc.suvankar.free_dictionary_api.dto.SenseDTO;
import cc.suvankar.free_dictionary_api.dto.WordEntryDTO;
import cc.suvankar.free_dictionary_api.dto.WordOfTheDayDTO;

@Component 
public class WordOfTheDayMapper {

    public WordOfTheDayDTO wordEntryToWordOfTheDayDTO(WordEntryDTO entryDTO) {
        WordOfTheDayDTO dto = new WordOfTheDayDTO();

        dto.setWord(entryDTO.getWord());
        dto.setIpa(entryDTO.getIpa());
        dto.setAudioUrl(entryDTO.getAudioUrl());
        dto.setDate(getCurrentDate());

        MeaningDTO firstMeaning = entryDTO
                                        .getMeanings().stream().findFirst()
                                        .orElse(null);
        
        if (firstMeaning != null) {
            dto.setPos(firstMeaning.getPartOfSpeech());

            SenseDTO firstSense = firstMeaning
                                        .getSenses().stream().findFirst()
                                        .orElse(null);

            if (firstSense != null) {
                dto.setMeaning(firstSense.getGlosses().stream().findFirst().orElse(null));
            }
        }

        return dto;
    }

    private String getCurrentDate() {
        LocalDate now = LocalDate.now();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.ENGLISH);

        return now.format(formatter);
    }
}
