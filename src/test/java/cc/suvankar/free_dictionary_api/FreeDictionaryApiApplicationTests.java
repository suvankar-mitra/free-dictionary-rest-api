package cc.suvankar.free_dictionary_api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import cc.suvankar.free_dictionary_api.dto.WordEntryDTO;


@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class FreeDictionaryApiApplicationTests {

	@Autowired
    private TestRestTemplate restTemplate;

	@Test
	void testGettingWord() {
		// mock test controller
		String word = "dictionary";
        String url = "/dictionaryapi/v1/definitions/en/" + word;

        ResponseEntity<WordEntryDTO> response = restTemplate.getForEntity(url, WordEntryDTO.class);

		assertEquals(200, response.getStatusCode().value(), "Should return HTTP 200");
		WordEntryDTO body = response.getBody();
		assertNotNull(body, "Response body should not be null");
		assertEquals(word, body.getWord(), "Word should match requested word");
	}
}
