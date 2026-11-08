package cc.suvankar.free_dictionary_api;

import cc.suvankar.free_dictionary_api.dto.*;
import org.springframework.aot.hint.annotation.*;
import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.*;
import org.springframework.cache.annotation.*;
import org.springframework.data.jpa.repository.config.*;

@SpringBootApplication
@EnableCaching
@EnableJpaRepositories(basePackages = "cc.suvankar.free_dictionary_api.repository")
// Register the DTO explicitly for Spring binding - needed for native image
@RegisterReflectionForBinding({
    WordEntryDTO.class,
    MeaningDTO.class,
    SenseDTO.class,
    SoundDTO.class,
    ErrorResponseDTO.class,
    TranslationDTO.class,
    TranslationSenseDTO.class,
    WordEntryCompactDTO.class,
    WordOfTheDayDTO.class,
    SourceAttributionDTO.class,
    ApiAttributionDTO.class
})
public class FreeDictionaryApiApplication {

  public static void main(String[] args) {
    SpringApplication.run(FreeDictionaryApiApplication.class, args);
  }
}
