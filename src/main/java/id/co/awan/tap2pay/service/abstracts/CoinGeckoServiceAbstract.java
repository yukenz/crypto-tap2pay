package id.co.awan.tap2pay.service.abstracts;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@RequiredArgsConstructor
public class CoinGeckoServiceAbstract {

    private final RestTemplate restTemplate;

    @Value("${coingecko.host}")
    private String coinGeckoHost;

    @Value("${coingecko.api-key}")
    private String apiKey;

    @NotNull
    protected ResponseEntity<JsonNode> executePostRest(final String coinGeckoPath, final JsonNode request) {

        final LinkedMultiValueMap<String, String> HEADERS = new LinkedMultiValueMap<>();
        HEADERS.add(HttpHeaders.CONTENT_TYPE, "application/json");
        HEADERS.add(HttpHeaders.ACCEPT, "application/json");
        HEADERS.add("x-cg-demo-api-key", apiKey);

        return restTemplate.exchange(
                coinGeckoHost + coinGeckoPath,
                HttpMethod.POST,
                new HttpEntity<>(request, HEADERS),
                JsonNode.class
        );
    }

    @NotNull
    protected ResponseEntity<JsonNode> executeGetRest(final String coinGeckoPath, LinkedMultiValueMap<String, String> queryParams) {

        URI uri = UriComponentsBuilder.fromUriString(coinGeckoHost + coinGeckoPath)
                .queryParams(queryParams)
                .build()
                .toUri();

        final LinkedMultiValueMap<String, String> HEADERS = new LinkedMultiValueMap<>();
        HEADERS.add(HttpHeaders.ACCEPT, "application/json");
        HEADERS.add("x-cg-demo-api-key", apiKey);

        return restTemplate.exchange(
                uri,
                HttpMethod.POST,
                new HttpEntity<>(null, HEADERS),
                JsonNode.class
        );
    }

    @NotNull
    protected ResponseEntity<JsonNode> executeGetRest(final String coinGeckoPath) {
        return executeGetRest(coinGeckoPath, null);
    }


}
