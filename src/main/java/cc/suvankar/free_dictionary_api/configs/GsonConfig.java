package cc.suvankar.free_dictionary_api.configs;

import com.google.gson.Gson;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GsonConfig {
  @Bean
  public Gson gson() {
    return new Gson();
  }
}
