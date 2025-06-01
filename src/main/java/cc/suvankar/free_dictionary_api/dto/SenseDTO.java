package cc.suvankar.free_dictionary_api.dto;

import java.io.Serializable;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class SenseDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Set<String> glosses;
    private Set<String> synonyms;
    private Set<String> antonyms;
    private Set<String> examples;
    private Set<String> tags;
    private Set<String> related;
}
