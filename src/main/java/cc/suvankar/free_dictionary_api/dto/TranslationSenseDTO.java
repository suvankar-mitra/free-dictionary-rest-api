package cc.suvankar.free_dictionary_api.dto;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TranslationSenseDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String sense;
    private String roman;
    private String word;
}