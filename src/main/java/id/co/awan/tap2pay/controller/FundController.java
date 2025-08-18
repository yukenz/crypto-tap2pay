package id.co.awan.tap2pay.controller;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import id.co.awan.tap2pay.service.MidtransService;
import id.co.awan.tap2pay.service.RampTransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigInteger;
import java.util.List;

@RestController
@RequestMapping("/api/fund")
@RequiredArgsConstructor
@Slf4j
public class FundController {


    private final RampTransactionService rampTransactionService;
    private final MidtransService midtransService;
    private final ObjectMapper objectMapper;

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

        String firstName = request.at("/first_name").asText(null);
        String lastName = request.at("/last_name").asText(null);
        String email = request.at("/email").asText(null);
        String phone = request.at("/phone").asText(null);
        String walletAddress = request.at("/wallet_address").asText(null);
        String chain = request.at("/chain").asText(null);
        String erc20Address = request.at("/erc20_address").asText(null);
        int amount = request.at("/amount").asInt();

        // Create OnRamp
        String orderId = rampTransactionService.createTransactionOnRampFirstPhase(
                walletAddress,
                chain,
                erc20Address,
                BigInteger.valueOf(amount)
        );

        // Create VA
        try {
            ResponseEntity<JsonNode> transaction = midtransService.createTransaction(
                    orderId,
                    amount,
                    true,
                    firstName,
                    lastName,
                    email,
                    phone
            );

            JsonNode vaDetail = transaction.getBody();
            Assert.notNull(vaDetail, "vaDetail should not be null");

            if (transaction.getStatusCode().equals(HttpStatus.CREATED)) {
                String redirectUrl = vaDetail.at("/redirect_url").asText(null);
                String token = vaDetail.at("/token").asText(null);
                rampTransactionService.createTransactionOnRampSecondPhase(orderId, redirectUrl, token);
                return ResponseEntity.ok(vaDetail);
            } else {
                String errorMessages = String.join(",", objectMapper.convertValue(vaDetail.at("/error_messages"), new TypeReference<List<String>>() {
                }));
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, errorMessages);
            }

        } catch (Exception ex) {
            rampTransactionService.errorTransactionOnRampSecondPhase(orderId, ex.getMessage());
            throw ex;
        }
    }
}
