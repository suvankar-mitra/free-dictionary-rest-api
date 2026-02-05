package cc.suvankar.free_dictionary_api.dto;

import java.io.Serializable;
import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ErrorResponseDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private int status;
    private String error;
    private String message;
    private String path;
    private String requestedWord;
    private Instant timestamp = Instant.now();
}
