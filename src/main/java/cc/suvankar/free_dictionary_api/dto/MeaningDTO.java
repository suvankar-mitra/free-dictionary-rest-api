package cc.suvankar.free_dictionary_api.dto;

import java.io.Serializable;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class MeaningDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String partOfSpeech;
    private Set<SenseDTO> senses;
    private Set<String> synonyms;
    private Set<String> antonyms;
    private String etymologyText;
}
