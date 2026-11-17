package cc.suvankar.free_dictionary_api.controller;

import cc.suvankar.free_dictionary_api.dto.*;
import lombok.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/dictionaryapi/v1/attribution")
@RequiredArgsConstructor
public class ApiAttributionController {

  @GetMapping
  public ResponseEntity<ApiAttributionDTO> getAttribution() {
    return ResponseEntity.ok(ApiAttributionDTO.getInstance());
  }
}
