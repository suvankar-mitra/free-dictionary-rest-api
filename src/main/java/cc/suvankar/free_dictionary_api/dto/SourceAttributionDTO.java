package cc.suvankar.free_dictionary_api.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;

@Getter
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class SourceAttributionDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private static final SourceAttributionDTO INSTANCE = new SourceAttributionDTO();

    private final String source;
    private final String sourceUrl;
    private final String sourceLicense;
    private final String note;
    private final ApiAttributionDTO api;

    private SourceAttributionDTO() {
        this.source = "Wiktionary";
        this.sourceUrl = "https://en.wiktionary.org";
        this.sourceLicense = "CC BY-SA 4.0";
        this.note = "Includes processed content from Wiktionary, used under CC BY-SA 4.0.";
        this.api = ApiAttributionDTO.getInstance();
    }

    public static SourceAttributionDTO getInstance() {
        return INSTANCE;
    }
}