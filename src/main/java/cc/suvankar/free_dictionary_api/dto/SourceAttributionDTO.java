package cc.suvankar.free_dictionary_api.dto;

import com.fasterxml.jackson.annotation.*;
import java.io.*;
import lombok.*;

@Getter
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class SourceAttributionDTO implements Serializable {
  private static final long serialVersionUID = 1L;

  private final String source;
  private final String sourceUrl;
  private final String sourceLicense;
  private final String note;
  private final ApiAttributionDTO api;

  public SourceAttributionDTO(
      String source, String sourceUrl, String sourceLicense, String note, ApiAttributionDTO api) {
    this.source = source;
    this.sourceUrl = sourceUrl;
    this.sourceLicense = sourceLicense;
    this.note = note;
    this.api = api;
  }

  public static SourceAttributionDTO forWiktionary(String word) {
    return new SourceAttributionDTO(
        "Wiktionary",
        "https://en.wiktionary.org/wiki/" + word,
        "CC BY-SA 4.0",
        "Includes processed content from Wiktionary, used under CC BY-SA 4.0.",
        ApiAttributionDTO.getInstance());
  }
}
