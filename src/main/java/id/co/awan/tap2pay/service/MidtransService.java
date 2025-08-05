package id.co.awan.tap2pay.service;

import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class MidtransService {

    private final RestTemplate restTemplate;

    private final String MIDTRANS_TRANSACTION_URL = "https://app.sandbox.midtrans.com/snap/v1/transactions";

    public String createTransaction(
            String order_id,
            Integer gross_amount,
            Boolean secure,
            String first_name,
            String last_name,
            String email,
            String phone
    ) {

        final LinkedMultiValueMap<String, String> HEADERS = new LinkedMultiValueMap<>();
        HEADERS.add(HttpHeaders.CONTENT_TYPE, "application/json");
        HEADERS.add(HttpHeaders.ACCEPT, "application/json");

        final String REQUEST = String.format("""
                        {
                            "transaction_details": {
                                "order_id": "%s",
                                "gross_amount": %s
                            },
                            "credit_card":{
                                "secure" : %s
                            },
                            "customer_details": {
                                "first_name": "%s",
                                "last_name": "%s",
                                "email": "%s",
                                "phone": "%s"
                            }
                        """,
                order_id,
                gross_amount,
                secure,
                first_name,
                last_name,
                email,
                phone
        );


//        ResponseEntity<?> responseEntity = restTemplate
//                .postForEntity(
//                        MIDTRANS_TRANSACTION_URL,
//                        new HttpEntity<>(REQUEST, HEADERS),
//                        Map.class
//                );

        ResponseEntity<Map<String, Object>> responseEntity = restTemplate.exchange(
                MIDTRANS_TRANSACTION_URL,
                HttpMethod.POST,
                new HttpEntity<>(REQUEST, HEADERS),
                new ParameterizedTypeReference<Map<String, Object>>() {
                }
        );

        Map<String, Object> response = responseEntity.getBody();

        String token = (String) response.get("token");
        String redirectUrl = (String) response.get("redirect_url");

        return redirectUrl;
    }

}
