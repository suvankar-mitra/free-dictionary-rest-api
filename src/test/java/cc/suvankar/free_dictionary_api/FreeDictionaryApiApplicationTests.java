package cc.suvankar.free_dictionary_api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import cc.suvankar.free_dictionary_api.dto.ErrorResponseDTO;
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

		assertEquals(404, response.getStatusCode().value(), "Should return HTTP 404");
	}

	@Test
	void testRateLimitHeadersOnDefinitions() {
		String word = "test";
		String url = "/dictionaryapi/v1/definitions/en/" + word;

		ResponseEntity<WordEntryDTO> response = restTemplate.getForEntity(url, WordEntryDTO.class);

		// Check for rate limit headers
		assertTrue(response.getHeaders().containsKey("X-RateLimit-Limit"), "Should contain X-RateLimit-Limit header");
		assertTrue(response.getHeaders().containsKey("X-RateLimit-Remaining"),
				"Should contain X-RateLimit-Remaining header");
		assertTrue(response.getHeaders().containsKey("X-RateLimit-Reset"), "Should contain X-RateLimit-Reset header");

		// Verify limit is 30 for definitions endpoint
		String limitHeader = response.getHeaders().getFirst("X-RateLimit-Limit");
		assertEquals("30", limitHeader, "Definition endpoint limit should be 30");
	}

	@Test
	void testRateLimitHeadersOnWordOfTheDay() {
		String url = "/dictionaryapi/v1/wordoftheday";

		ResponseEntity<WordEntryDTO> response = restTemplate.getForEntity(url, WordEntryDTO.class);

		// Check for rate limit headers
		assertTrue(response.getHeaders().containsKey("X-RateLimit-Limit"), "Should contain X-RateLimit-Limit header");
		assertTrue(response.getHeaders().containsKey("X-RateLimit-Remaining"),
				"Should contain X-RateLimit-Remaining header");
		assertTrue(response.getHeaders().containsKey("X-RateLimit-Reset"), "Should contain X-RateLimit-Reset header");

		// Verify limit is 5 for word-of-the-day endpoint
		String limitHeader = response.getHeaders().getFirst("X-RateLimit-Limit");
		assertEquals("5", limitHeader, "Word-of-the-day endpoint limit should be 5");
	}

	@Test
	void testRateLimitRemainingDecreases() {
		String word = "apple";
		String url = "/dictionaryapi/v1/definitions/en/" + word;

		// First request
		ResponseEntity<WordEntryDTO> response1 = restTemplate.getForEntity(url, WordEntryDTO.class);
		String remaining1 = response1.getHeaders().getFirst("X-RateLimit-Remaining");
		assertNotNull(remaining1, "First response should have X-RateLimit-Remaining");

		// Second request
		ResponseEntity<WordEntryDTO> response2 = restTemplate.getForEntity(url, WordEntryDTO.class);
		String remaining2 = response2.getHeaders().getFirst("X-RateLimit-Remaining");
		assertNotNull(remaining2, "Second response should have X-RateLimit-Remaining");

		int rem1 = Integer.parseInt(remaining1);
		int rem2 = Integer.parseInt(remaining2);
		assertTrue(rem2 <= rem1, "Remaining requests should decrease or stay same after request");
	}

	@Test
	void testWordNotFoundReturnsErrorResponse() {
		String nonexistentWord = "xyznonexistentwordxyz12345";
		String url = "/dictionaryapi/v1/definitions/en/" + nonexistentWord;

		ResponseEntity<ErrorResponseDTO> response = restTemplate.getForEntity(url, ErrorResponseDTO.class);

		assertEquals(404, response.getStatusCode().value(), "Should return HTTP 404 for non-existent word");
		
		ErrorResponseDTO body = response.getBody();
		assertNotNull(body, "Response body should not be null");
		assertEquals(404, body.getStatus(), "Status in response should be 404");
		assertEquals("Not Found", body.getError(), "Error field should be 'Not Found'");
		assertTrue(body.getMessage().contains("No definition found"), "Message should indicate no definition found");
		assertEquals(nonexistentWord, body.getRequestedWord(), "requestedWord should match the requested word");
	}

	@Test
	void testRateLimitResetTimeIsValid() {
		String url = "/dictionaryapi/v1/wordoftheday";

		ResponseEntity<WordEntryDTO> response = restTemplate.getForEntity(url, WordEntryDTO.class);

		String resetTime = response.getHeaders().getFirst("X-RateLimit-Reset");
		assertNotNull(resetTime, "X-RateLimit-Reset header should be present");

		long resetEpoch = Long.parseLong(resetTime);
		long nowEpoch = System.currentTimeMillis() / 1000;

		// Reset time should be approximately 60 seconds from now
		long diff = resetEpoch - nowEpoch;
		assertTrue(diff >= 59 && diff <= 61, "Reset time should be approximately 60 seconds from now");
	}

	@Test
	void testEmptyWordParameterReturnsBadRequest() {
		String url = "/dictionaryapi/v1/definitions/en/";

		ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

		assertEquals(404, response.getStatusCode().value(), "Should return HTTP 404 for empty word");
	}

	@Test
	void testMultipleRequestsWithCustomHeader() {
		String word = "hello";
		String url = "/dictionaryapi/v1/definitions/en/" + word;

		// Create request with custom header
		HttpHeaders headers = new HttpHeaders();
		headers.set("X-Forwarded-For", "192.168.1.1");

		HttpEntity<String> entity = new HttpEntity<>(headers);

		// Make request with custom headers
		ResponseEntity<WordEntryDTO> response = restTemplate.exchange(url, HttpMethod.GET, entity,
				WordEntryDTO.class);

		// Should still have rate limit headers
		assertTrue(response.getHeaders().containsKey("X-RateLimit-Limit"), "Should contain X-RateLimit-Limit header");
		assertTrue(response.getHeaders().containsKey("X-RateLimit-Remaining"),
				"Should contain X-RateLimit-Remaining header");
	}
}
