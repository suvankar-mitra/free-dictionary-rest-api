package cc.suvankar.free_dictionary_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class FreeDictionaryApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(FreeDictionaryApiApplication.class, args);
	}

}
