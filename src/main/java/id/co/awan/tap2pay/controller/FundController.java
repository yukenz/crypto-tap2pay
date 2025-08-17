package id.co.awan.tap2pay.controller;


import com.fasterxml.jackson.databind.JsonNode;
import id.co.awan.tap2pay.service.MidtransService;
import id.co.awan.tap2pay.service.RampTransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;

@RestController
@RequestMapping("/api/fund")
@RequiredArgsConstructor
@Slf4j
public class FundController {


    private final RampTransactionService rampTransactionService;
    private final MidtransService midtransService;

    @GetMapping(
            path = "/inquiry-on-ramp",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<JsonNode> inquiyOnRamp(
            @RequestParam String address
    ) {

        // TODO: IMPLEMENT LOGIC
        // Cek Pending ORRDER-ID based on address

        return ResponseEntity.ok(null);
    }

    @PostMapping(
            path = "/create-on-ramp",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<JsonNode> createOnRamp(
            @RequestBody
            JsonNode request
    ) {

        String firstName = request.at("/first_name").asText();
        String lastName = request.at("/last_name").asText();
        String email = request.at("/email").asText();
        String phone = request.at("/phone").asText();
        String walletAddress = request.at("/wallet_address").asText();
        int amount = request.at("/amount").asInt();

        // Create OnRamp
        String orderId = rampTransactionService.createTransactionOnRampFirstPhase(
                walletAddress,
                BigInteger.valueOf(amount)
        );

        // Create VA
        JsonNode vaDetail = midtransService.createTransaction(
                orderId,
                amount,
                true,
                firstName,
                lastName,
                email,
                phone
        );

        String redirectUrl = vaDetail.at("/redirect_url").asText();
        String token = vaDetail.at("/token").asText();
        rampTransactionService.createTransactionOnRampSecondPhase(orderId, redirectUrl, token);

        return ResponseEntity.ok(vaDetail);
    }
}
