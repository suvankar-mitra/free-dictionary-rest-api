package cc.suvankar.free_dictionary_api.dto;

import com.fasterxml.jackson.annotation.*;
import java.io.*;
import lombok.*;

@Getter
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ApiAttributionDTO implements Serializable {

  private static final long serialVersionUID = 1L;

  private static final ApiAttributionDTO INSTANCE = new ApiAttributionDTO();

  private final String api;
  private final String apiUrl;

  private ApiAttributionDTO() {
    this.api = "Free Dictionary REST API";
    this.apiUrl = "https://api.suvankar.cc/docs/dictionary";
  }

  public static ApiAttributionDTO getInstance() {
    return INSTANCE;
  }
}
