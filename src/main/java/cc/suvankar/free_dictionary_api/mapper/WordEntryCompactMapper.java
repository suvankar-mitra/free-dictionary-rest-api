package cc.suvankar.free_dictionary_api.mapper;

import cc.suvankar.free_dictionary_api.dto.WordEntryCompactDTO;
import cc.suvankar.free_dictionary_api.dto.WordEntryDTO;
import org.springframework.stereotype.Component;

@Component
public class WordEntryCompactMapper {
  public WordEntryCompactDTO makeWordDefinitionCompact(final WordEntryDTO definition) {
    if (definition == null) {
      return null;
    }

    WordEntryCompactDTO compactDTO = new WordEntryCompactDTO();
    compactDTO.setWord(definition.getWord());
    compactDTO.setMeanings(definition.getMeanings());
    compactDTO.setAttribute(definition.getAttribute());

    return compactDTO;
  }
}
