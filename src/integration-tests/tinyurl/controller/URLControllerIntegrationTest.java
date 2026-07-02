package tinyurl.controller;

import com.canosaa.tinyurl.TinyurlApplication;
import com.canosaa.tinyurl.dto.ShortenUrlRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = TinyurlApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class URLControllerIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String baseUrl() {
        return "http://localhost:" + port + "/api/v1/urls";
    }

    private ResponseEntity<String> shortenUrl(String url) {
        var request = new ShortenUrlRequest();
        request.setUrl(url);
        return restTemplate.postForEntity(baseUrl(), request, String.class);
    }

    @Test
    void shortenUrlCreatesShortCodeForNewUrl() {
        var originalUrl = "https://example.com/articles/" + UUID.randomUUID();

        var response = shortenUrl(originalUrl);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotBlank();
    }

    @Test
    void shortenUrlReturnsSameShortCodeForRepeatedUrl() {
        var originalUrl = "https://example.com/articles/" + UUID.randomUUID();

        var firstResponse = shortenUrl(originalUrl);
        var secondResponse = shortenUrl(originalUrl);

        assertThat(secondResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(secondResponse.getBody()).isEqualTo(firstResponse.getBody());
    }

    @Test
    void getUrlReturnsOriginalUrlForShortenedCode() {
        var originalUrl = "https://example.com/articles/" + UUID.randomUUID();
        var shortCode = shortenUrl(originalUrl).getBody();

        var response = restTemplate.getForEntity(baseUrl() + "/" + shortCode, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(originalUrl);
    }

    @Test
    void getUrlReturnsServerErrorForUnknownShortCode() {
        var response = restTemplate.getForEntity(baseUrl() + "/doesNotExist12345", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
