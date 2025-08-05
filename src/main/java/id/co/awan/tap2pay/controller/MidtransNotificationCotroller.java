package id.co.awan.tap2pay.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import id.co.awan.tap2pay.service.MidtransNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/midtrans/notification")
@RequiredArgsConstructor
@Slf4j
public class MidtransNotificationCotroller {

    private final ObjectMapper objectMapper;
    private final MidtransNotificationService midtransNotificationService;

    @PostMapping(
            path = "/payment",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<String>
    paymentNotification(
            @RequestBody
            JsonNode request
    ) {

        try {
            switch (request.at("/payment_type").asText()) {
                case "credit_card" -> midtransNotificationService.creditCardProcessPayment(request);
                case "gopay" -> midtransNotificationService.gopayProcessPayment(request);
                case "qris" -> midtransNotificationService.qrisProcessPayment(request);
                case "shopeepay" -> midtransNotificationService.shopeepayProcessPayment(request);
                case "bank_transfer" -> midtransNotificationService.bankTransferProcessPayment(request);
                case "echannel" -> midtransNotificationService.echannelProcessPayment(request);
                case "cstore" -> midtransNotificationService.cstoreProcessPayment(request);
                case "akulaku" -> midtransNotificationService.akulakuProcessPayment(request);
                default -> midtransNotificationService.defaultProcessPayment(request);
            }
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }

        return ResponseEntity.ok(null);
    }

    @PostMapping(
            path = "/recurring",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<String>
    recurringNotification(
    ) {
        return ResponseEntity.ok(null);
    }

    @PostMapping(
            path = "/pay-account",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<String>
    payAccount(
    ) {
        return ResponseEntity.ok(null);
    }


}
