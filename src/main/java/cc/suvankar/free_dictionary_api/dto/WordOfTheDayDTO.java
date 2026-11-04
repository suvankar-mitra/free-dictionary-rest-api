package cc.suvankar.free_dictionary_api.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data 
@NoArgsConstructor 
@AllArgsConstructor 
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class WordOfTheDayDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    private String word;
    private String ipa;
    private String audioUrl;
    private String pos;
    private String meaning;
    private String date;
}
