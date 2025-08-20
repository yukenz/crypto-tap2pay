package id.co.awan.tap2pay.service.abstracts;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.util.Assert;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;

@RequiredArgsConstructor
public abstract class MidtransServiceAbstract {

    private final RestTemplate restTemplate;

    @Value("${midtrans.host}")
    private String midtransHost;

    @Value("${midtrans.server-key}")
    private String serverKey;

    public JsonNode generateCreateTransactionRequest(
            String orderId,
            Integer grossAmount,
            Boolean secure,
            String firstName,
            String lastName,
            String email,
            String phone
    ) {

        /*
          {
            "transaction_details": {
              "order_id": "YOUR-ORDERID-123456",
              "gross_amount": 10000
            },
            "credit_card": {
              "secure": true
            },
            "customer_details": {
              "first_name": "budi",
              "last_name": "pratama",
              "email": "budi.pra@example.com",
              "phone": "08111222333"
            }
          }
          */

        ObjectNode transactionDetails = JsonNodeFactory.instance.objectNode();
        transactionDetails.put("order_id", orderId);
        transactionDetails.put("gross_amount", grossAmount);

        ObjectNode creditCard = JsonNodeFactory.instance.objectNode();
        creditCard.put("secure", secure);

        ObjectNode customerDetails = JsonNodeFactory.instance.objectNode();
        customerDetails.put("first_name", firstName);
        customerDetails.put("last_name", lastName);
        customerDetails.put("email", email);
        customerDetails.put("phone", phone);


        ObjectNode requestObject = JsonNodeFactory.instance.objectNode();
        requestObject.set("transaction_details", transactionDetails);
        requestObject.set("credit_card", creditCard);
        requestObject.set("customer_details", customerDetails);

        return requestObject;
    }

    /*
     * =================================================================================================================
     * ORCHESTRATION
     * =================================================================================================================
     */

    private String midtransBasicAuthorization() {
        return HttpHeaders.encodeBasicAuth(serverKey, "", StandardCharsets.UTF_8);
//        return HttpHeaders.encodeBasicAuth("Administrator", "manage", StandardCharsets.UTF_8);
    }

    @NotNull
    protected ResponseEntity<JsonNode> executePostRest(final String midtransPath, final JsonNode request) {

        final LinkedMultiValueMap<String, String> HEADERS = new LinkedMultiValueMap<>();
        HEADERS.add(HttpHeaders.CONTENT_TYPE, "application/json");
        HEADERS.add(HttpHeaders.ACCEPT, "application/json");
        HEADERS.add(HttpHeaders.AUTHORIZATION, "Basic " + midtransBasicAuthorization());

        return restTemplate.exchange(
                midtransHost + midtransPath,
                HttpMethod.POST,
                new HttpEntity<>(request, HEADERS),
                JsonNode.class
        );
    }

    @NotNull
    protected JsonNode parseResponseJsonNode(ResponseEntity<JsonNode> responseEntity) {

        JsonNode responseJson = responseEntity.getBody();
        Assert.notNull(responseJson, () -> {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "01" + "|" + "ResponseJson should not be null");
        });

        HttpStatusCode httpStatusCode = responseEntity.getStatusCode();
        if (!httpStatusCode.equals(HttpStatus.OK)) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "01" + "|" + httpStatusCode);
        }

        return responseJson;
    }


}
