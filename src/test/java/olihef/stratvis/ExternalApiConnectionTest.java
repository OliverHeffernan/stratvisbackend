package olihef.stratvis;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ExternalApiConnectionTest {

    @Test
    @EnabledIfSystemProperty(named = "runExternalConnectionTest", matches = "true")
    void apiDomainIsReachableOverHttps() throws Exception {
        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api-stratvis.olihef.com"))
                .timeout(Duration.ofSeconds(15))
                .method("HEAD", HttpRequest.BodyPublishers.noBody())
                .build();

        HttpResponse<Void> response = client.send(request, HttpResponse.BodyHandlers.discarding());
        int status = response.statusCode();

        // Any non-5xx response confirms the host is reachable and serving traffic.
        assertTrue(status >= 100 && status < 500,
                "Expected reachable HTTPS endpoint at api-stratvis.olihef.com, got status " + status);
    }
}
