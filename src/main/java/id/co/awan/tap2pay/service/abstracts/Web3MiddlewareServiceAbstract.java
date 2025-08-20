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

@RequiredArgsConstructor
public class Web3MiddlewareServiceAbstract {

    @Value("${web3-mdw.host}")
    private String web3MiddlewareHost;

    @Value("${web3-mdw.authorization}")
    private String web3MiddlewareAuthorization;

    private final RestTemplate restTemplate;

    @NotNull
    protected ResponseEntity<JsonNode> executePostRest(final String erc20MiddlewarePath, final JsonNode request) {

        final LinkedMultiValueMap<String, String> HEADERS = new LinkedMultiValueMap<>();
        HEADERS.add(HttpHeaders.CONTENT_TYPE, "application/json");
        HEADERS.add(HttpHeaders.ACCEPT, "application/json");
        HEADERS.add(HttpHeaders.AUTHORIZATION, web3MiddlewareAuthorization);

        return restTemplate.exchange(
                web3MiddlewareHost + erc20MiddlewarePath,
                HttpMethod.POST,
                new HttpEntity<>(request, HEADERS),
                JsonNode.class
        );
    }

    @NotNull
    protected JsonNode parseResponseJsonNode(ResponseEntity<JsonNode> responseEntity) {

        JsonNode responseJson = responseEntity.getBody();
        Assert.notNull(responseJson, "Rest Template Response shouldn't be null");
        HttpStatusCode httpStatusCode = responseEntity.getStatusCode();

        if (!httpStatusCode.equals(HttpStatus.OK)) {
            String error = responseJson.at("/error").asText("General Error");
            String errorDetail = responseJson.at("/errorDetail").asText(null);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, error + "|" + errorDetail);
        }

        return responseJson;
    }

}
