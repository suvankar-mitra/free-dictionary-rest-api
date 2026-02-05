package cc.suvankar.free_dictionary_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableCaching
@EnableJpaRepositories(basePackages = "cc.suvankar.free_dictionary_api.repository")
public class FreeDictionaryApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(FreeDictionaryApiApplication.class, args);
	}

}
