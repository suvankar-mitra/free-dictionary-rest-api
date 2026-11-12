package cc.suvankar.free_dictionary_api;

import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.*;
import org.springframework.cache.annotation.*;
import org.springframework.data.jpa.repository.config.*;

@SpringBootApplication
@EnableCaching
@EnableJpaRepositories(basePackages = "cc.suvankar.free_dictionary_api.repository")
public class FreeDictionaryApiApplication {

  public static void main(String[] args) {
    SpringApplication.run(FreeDictionaryApiApplication.class, args);
  }
}
