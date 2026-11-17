package cc.suvankar.free_dictionary_api.dto;

import com.fasterxml.jackson.annotation.*;
import java.io.*;
import java.nio.charset.*;
import lombok.*;
import org.springframework.web.util.*;

@Getter
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class SourceAttributionDTO implements Serializable {

  private static final long serialVersionUID = 1L;

  private static final String WIKTIONARY_URL = "https://en.wiktionary.org/wiki/";

  private static final String LICENSE = "CC BY-SA 4.0 / GFDL";

  private static final String LICENSE_URL = "https://en.wiktionary.org/wiki/Wiktionary:Copyrights";

  private final String source;
  private final String sourceUrl;
  private final String extractedVia;
  private final String license;
  private final String licenseUrl;
  private final String note;

  private SourceAttributionDTO(
      String source,
      String sourceUrl,
      String extractedVia,
      String license,
      String licenseUrl,
      String note) {
    this.source = source;
    this.sourceUrl = sourceUrl;
    this.extractedVia = extractedVia;
    this.license = license;
    this.licenseUrl = licenseUrl;
    this.note = note;
  }

  public static SourceAttributionDTO forWiktionary(String word) {
    String encodedWord = UriUtils.encodePathSegment(word, StandardCharsets.UTF_8);

    return new SourceAttributionDTO(
        "Wiktionary",
        WIKTIONARY_URL + encodedWord,
        "https://kaikki.org",
        LICENSE,
        LICENSE_URL,
        "Extracted and processed from Wiktionary via Kaikki.org.");
  }
}
