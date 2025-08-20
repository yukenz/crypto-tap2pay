package id.co.awan.tap2pay.service.abstracts;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.util.List;

@RequiredArgsConstructor
public class Web3MiddlewareServiceAbstract {

    @Value("${web3-mdw.host}")
    private String web3MiddlewareHost;

    @Value("${web3-mdw.username}")
    private String web3MiddlewareUsername;

    @Value("${web3-mdw.password}")
    private String web3MiddlewarePassword;

    private final RestTemplate restTemplate;

    @NotNull
    protected ResponseEntity<JsonNode> executePostRest(final String erc20MiddlewarePath, final JsonNode request) {

        HttpHeaders HEADERS = new HttpHeaders();
        HEADERS.setContentType(MediaType.APPLICATION_JSON);
        HEADERS.setAccept(List.of(MediaType.APPLICATION_JSON));
        HEADERS.setBasicAuth(web3MiddlewareUsername, web3MiddlewarePassword, StandardCharsets.UTF_8);

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
        HttpStatusCode httpStatusCode = responseEntity.getStatusCode();

        Assert.notNull(responseJson, () -> {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "01" + "|" + "ResponseJson should not be null");
        });

        if (!httpStatusCode.equals(HttpStatus.OK)) {
            String error = responseJson.at("/error").asText("General Error");
            String errorDetail = responseJson.at("/errorDetail").asText(null);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, error + "|" + errorDetail);
        }

        return responseJson;
    }

}
