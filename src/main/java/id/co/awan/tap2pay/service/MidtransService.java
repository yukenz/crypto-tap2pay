package id.co.awan.tap2pay.service;

import com.fasterxml.jackson.databind.JsonNode;
import id.co.awan.tap2pay.service.abstracts.MidtransServiceAbstract;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class MidtransService extends MidtransServiceAbstract {

    private final RestTemplate restTemplate;

    public JsonNode createTransaction(
            String orderId,
            Integer grossAmount,
            Boolean secure,
            String firstName,
            String lastName,
            String email,
            String phone
    ) {

        final LinkedMultiValueMap<String, String> HEADERS = midtransCommonHeaders();
        final JsonNode REQUEST = super.generateCreateTransactionRequest(
                orderId,
                grossAmount,
                secure,
                firstName,
                lastName,
                email,
                phone
        );

        ResponseEntity<JsonNode> responseEntity = restTemplate.exchange(
                super.transactionUrl,
                HttpMethod.POST,
                new HttpEntity<>(REQUEST, HEADERS),
                JsonNode.class
        );


        return responseEntity.getBody();
    }

    public JsonNode cancelTransaction(
            String orderId
    ) {

        final LinkedMultiValueMap<String, String> HEADERS = midtransCommonHeaders();

        ResponseEntity<JsonNode> responseEntity = restTemplate.exchange(
                String.format("%s/v2/%s/cancel", super.midtransHost, orderId),
                HttpMethod.POST,
                new HttpEntity<>(null, HEADERS),
                JsonNode.class
        );

        return responseEntity.getBody();
    }

    public JsonNode refundTransaction(
            String orderId
    ) {

        final LinkedMultiValueMap<String, String> HEADERS = midtransCommonHeaders();

        ResponseEntity<JsonNode> responseEntity = restTemplate.exchange(
                String.format("%s/v2/%s/refund", super.midtransHost, orderId),
                HttpMethod.POST,
                new HttpEntity<>(null, HEADERS),
                JsonNode.class
        );

        return responseEntity.getBody();
    }

    public JsonNode expireTransaction(
            String orderId
    ) {

        final LinkedMultiValueMap<String, String> HEADERS = midtransCommonHeaders();

        ResponseEntity<JsonNode> responseEntity = restTemplate.exchange(
                String.format("%s/v2/%s/expire", super.midtransHost, orderId),
                HttpMethod.POST,
                new HttpEntity<>(null, HEADERS),
                JsonNode.class
        );

        return responseEntity.getBody();
    }


    @Override
    public String generateOrderId(String ownerAddress) {
        return "";
    }

    @Override
    public String saveToken(String ownerAddress, String midtransTransactionToken) {
        return "";
    }
}
