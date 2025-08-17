package id.co.awan.tap2pay;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import id.co.awan.tap2pay.config.RestConfig;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.math.BigInteger;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

@Slf4j
public class Web3MiddlewareTest {

    private RestTemplate restTemplate;

    @BeforeEach
    void setUp() throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        RestConfig restConfig = new RestConfig();
        restTemplate = restConfig.apachePoolingRestTemplate(new RestTemplateBuilder());
    }

    @Test
    void totalSuply() {

        final LinkedMultiValueMap<String, String> HEADERS = new LinkedMultiValueMap<>();
        HEADERS.add(HttpHeaders.CONTENT_TYPE, "application/json");
        HEADERS.add(HttpHeaders.ACCEPT, "application/json");
        HEADERS.add(HttpHeaders.AUTHORIZATION, "Basic QWRtaW5pc3RyYXRvcjptYW5hZ2Ux=");

        final ObjectNode REQUEST = JsonNodeFactory.instance.objectNode();
        REQUEST.put("chain", "anvil");
        REQUEST.put("erc20Address", "0x5FbDB2315678afecb367f032d93F642f64180aa3");

        ResponseEntity<JsonNode> responseEntity = restTemplate.exchange(
                "http://web3.yukenz.id:3000/api/web3/erc20/read/totalSupply",
                HttpMethod.POST,
                new HttpEntity<>(REQUEST, HEADERS),
                JsonNode.class
        );

        if (!responseEntity.getStatusCode().equals(HttpStatus.OK)) {
            String errorCode = responseEntity.getBody().at("/error").asText();
            String errorDetail = responseEntity.getBody().at("/errorDetail").asText();
            String errorFormat = String.format("%s | %s", errorCode, errorDetail);
            log.info("Error Occured : {}", responseEntity.getStatusCode());
            log.info("Error Occured : {}", errorFormat);

        }

//        String data = responseEntity.getBody().at("/data").asText();
//        log.info("Total Supply: {}", new BigInteger(data));
    }
}
