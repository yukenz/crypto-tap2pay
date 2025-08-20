package id.co.awan.tap2pay.service.abstracts;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.util.Assert;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RequiredArgsConstructor
public class CoinGeckoServiceAbstract {

    private final RestTemplate restTemplate;

    @Value("${coingecko.host}")
    private String coinGeckoHost;

    @Value("${coingecko.api-key}")
    private String apiKey;

    @NotNull
    protected ResponseEntity<JsonNode> executePostRest(final String coinGeckoPath, final JsonNode request) {

        HttpHeaders HEADERS = new HttpHeaders();
        HEADERS.setContentType(MediaType.APPLICATION_JSON);
        HEADERS.setAccept(List.of(MediaType.APPLICATION_JSON));
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

        HttpHeaders HEADERS = new HttpHeaders();
        HEADERS.setAccept(List.of(MediaType.APPLICATION_JSON));
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

    @NotNull
    protected JsonNode parseResponseJsonNode(ResponseEntity<JsonNode> responseEntity) {

        JsonNode responseJson = responseEntity.getBody();
        Assert.notNull(responseJson, () -> {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "01" + "|" + "ResponseJson should not be null");
        });

        HttpStatusCode httpStatusCode = responseEntity.getStatusCode();

        if (!httpStatusCode.equals(HttpStatus.OK)) {

            String errorMessage1 = responseJson.at("/error").asText("General Error");
            if (errorMessage1 != null) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "01" + "|" + errorMessage1);
            }

            Integer errorCode = responseJson.at("/status/error_code").asInt();
            String errorMessage2 = responseJson.at("/status/error_message").asText("General Error");
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, errorCode + "|" + errorMessage2);
        }

        return responseJson;
    }


}
