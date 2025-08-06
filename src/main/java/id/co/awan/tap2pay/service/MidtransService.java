package id.co.awan.tap2pay.service;

import com.fasterxml.jackson.databind.JsonNode;
import id.co.awan.tap2pay.service.abstracts.MidtransServiceAbstract;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class MidtransService extends MidtransServiceAbstract {

    private final RestTemplate restTemplate;

    private final String MIDTRANS_TRANSACTION_URL = "https://app.sandbox.midtrans.com/snap/v1/transactions";


    public String createTransaction(
            String orderId,
            Integer grossAmount,
            Boolean secure,
            String firstName,
            String lastName,
            String email,
            String phone
    ) {

        final LinkedMultiValueMap<String, String> HEADERS = new LinkedMultiValueMap<>();
        HEADERS.add(HttpHeaders.CONTENT_TYPE, "application/json");
        HEADERS.add(HttpHeaders.ACCEPT, "application/json");
        HEADERS.add(HttpHeaders.AUTHORIZATION, midtransBasicAuthorization("SERVER_KEY"));

        final JsonNode REQUEST = super.generateTransactionRequest(
                orderId,
                grossAmount,
                secure,
                firstName,
                lastName,
                email,
                phone
        );

        ResponseEntity<JsonNode> responseEntity = restTemplate.exchange(
                MIDTRANS_TRANSACTION_URL,
                HttpMethod.POST,
                new HttpEntity<>(REQUEST, HEADERS),
                JsonNode.class
        );

        JsonNode response = responseEntity.getBody();

        String token = response.at("/token").asText();
        String redirectUrl = response.at("/redirect_url").asText();

        return redirectUrl;
    }

    @Override
    public String midtransBasicAuthorization(String serverKey) {
        return "";
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
