package id.co.awan.tap2pay.service;

import com.fasterxml.jackson.databind.JsonNode;
import id.co.awan.tap2pay.service.abstracts.MidtransServiceAbstract;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class MidtransService extends MidtransServiceAbstract {

    @Value("${midtrans.path.transaction}")
    private String transactionPath;

    public MidtransService(RestTemplate restTemplate) {
        super(restTemplate);
    }

    public ResponseEntity<JsonNode>
    createTransaction(
            String orderId,
            Integer grossAmount,
            Boolean secure,
            String firstName,
            String lastName,
            String email,
            String phone
    ) {

        final JsonNode REQUEST = super.generateCreateTransactionRequest(
                orderId,
                grossAmount,
                secure,
                firstName,
                lastName,
                email,
                phone
        );

        log.info("[HTTP-REQUEST - {}:{}] : {}", this.getClass().getSimpleName(), "createTransaction()", REQUEST.toPrettyString());

        ResponseEntity<JsonNode> responseEntity = executePostRest(transactionPath, REQUEST);

        final JsonNode RESPONSE = responseEntity.getBody();
        Assert.notNull(RESPONSE, "Rest Template Response shouldn't be null");
        log.info("[HTTP-RESPONSE - {}:{}] : {}", this.getClass().getSimpleName(), "createTransaction()", RESPONSE.toPrettyString());

        /*
        {
            "token": "{{snap_token}}",
            "redirect_url": "https://app.sandbox.midtrans.com/snap/v3/redirection/{{snap_token}}"
        }
        */
        return responseEntity;
    }

    public JsonNode
    cancelTransaction(
            String orderId
    ) {
        String cancelPath = String.format("/v2/%s/cancel", orderId);
        ResponseEntity<JsonNode> responseEntity = executePostRest(cancelPath, null);
        return responseEntity.getBody();
    }

    public JsonNode
    refundTransaction(
            String orderId
    ) {
        String refundPath = String.format("/v2/%s/cancel", orderId);
        ResponseEntity<JsonNode> responseEntity = executePostRest(refundPath, null);
        return responseEntity.getBody();
    }

    public JsonNode
    expireTransaction(
            String orderId
    ) {
        String refundPath = String.format("/v2/%s/expire", orderId);
        ResponseEntity<JsonNode> responseEntity = executePostRest(refundPath, null);
        return responseEntity.getBody();
    }

    public JsonNode
    statusTransaction(
            String id
    ) {
        String refundPath = String.format("/v2/%s/status", id);
        ResponseEntity<JsonNode> responseEntity = executePostRest(refundPath, null);
        return responseEntity.getBody();
    }
}
