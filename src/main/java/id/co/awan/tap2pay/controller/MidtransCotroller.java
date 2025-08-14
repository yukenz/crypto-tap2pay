package id.co.awan.tap2pay.controller;

import com.fasterxml.jackson.databind.JsonNode;
import id.co.awan.tap2pay.service.MidtransService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/midtrans")
@RequiredArgsConstructor
@Slf4j
@Deprecated
public class MidtransCotroller {

    private final MidtransService midtransService;

    @PostMapping(
            path = "/transaction",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<JsonNode> createTransaction(
            @RequestBody
            JsonNode request
    ) {

        String orderId = request.at("/order_id").asText();
        Integer grossAmount = request.at("/gross_amount").asInt();
        Boolean secure = request.at("/secure").asBoolean();
        String firstName = request.at("/first_name").asText();
        String lastName = request.at("/last_name").asText();
        String email = request.at("/email").asText();
        String phone = request.at("/phone").asText();

        JsonNode transaction = midtransService.createTransaction(
                orderId,
                grossAmount,
                secure,
                firstName,
                lastName,
                email,
                phone
        );

        return ResponseEntity.ok(transaction);
    }


}
