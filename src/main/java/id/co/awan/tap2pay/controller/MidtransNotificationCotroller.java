package id.co.awan.tap2pay.controller;

import com.fasterxml.jackson.databind.JsonNode;
import id.co.awan.tap2pay.model.dto.midtrans.notification.TransactionStatusEnum;
import id.co.awan.tap2pay.model.entity.OnrampTransaction;
import id.co.awan.tap2pay.service.ERC20MiddlewareService;
import id.co.awan.tap2pay.service.MidtransNotificationService;
import id.co.awan.tap2pay.service.RampTransactionService;
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

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@RestController
@RequestMapping("/api/midtrans/notification")
@RequiredArgsConstructor
@Slf4j
public class MidtransNotificationCotroller {

    private final MidtransNotificationService midtransNotificationService;
    private final RampTransactionService rampTransactionService;
    private final ERC20MiddlewareService erc20MiddlewareService;

    @PostMapping(
            path = "/payment",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<String>
    paymentNotification(
            @RequestBody
            JsonNode request
    ) throws NoSuchAlgorithmException, NoSuchProviderException {

        log.info("[HTTP-REQUEST - {}:{}] : {}", this.getClass().getSimpleName(), "paymentNotification() : {}", request.toPrettyString());
        midtransNotificationService.validateSignature(request);

        String orderId = request.at("/order_id").asText(null);
        String transactionStatus = request.at("/transaction_status").asText(null);
        String transactionId = request.at("/transaction_id").asText(null);
        String settlementTime = request.at("/settlement_time").asText(null);
        String paymentType = request.at("/payment_type").asText(null);
        String fraudStatus = request.at("/fraud_status").asText(null);
        String currency = request.at("/currency").asText(null);


        TransactionStatusEnum transactionStatusEnum = TransactionStatusEnum.valueOf(transactionStatus.toUpperCase());
        try {
            switch (transactionStatusEnum) {
                case TransactionStatusEnum.PENDING -> log.info("{}", TransactionStatusEnum.PENDING);
                // Success Case
                case TransactionStatusEnum.CAPTURE -> log.info("{}", TransactionStatusEnum.CAPTURE);
                case TransactionStatusEnum.SETTLEMENT -> log.info("{}", TransactionStatusEnum.SETTLEMENT);
                // Reversal Case
                case TransactionStatusEnum.REFUND -> log.info("{}", TransactionStatusEnum.REFUND);
                case TransactionStatusEnum.CANCEL -> log.info("{}", TransactionStatusEnum.CANCEL);
                // Unsuccess Case
                case TransactionStatusEnum.DENY -> log.info("{}", TransactionStatusEnum.DENY);
                case TransactionStatusEnum.EXPIRE -> log.info("{}", TransactionStatusEnum.EXPIRE);
                case TransactionStatusEnum.FAILURE -> log.info("{}", TransactionStatusEnum.FAILURE);
                case TransactionStatusEnum.CHARGEBACK -> log.info("{}", TransactionStatusEnum.CHARGEBACK);
                case TransactionStatusEnum.PARTIAL_REFUND -> log.info("{}", TransactionStatusEnum.PARTIAL_REFUND);
                case TransactionStatusEnum.PARTIAL_CHARGEBACK ->
                        log.info("{}", TransactionStatusEnum.PARTIAL_CHARGEBACK);
                case TransactionStatusEnum.AUTHORIZE -> log.info("{}", TransactionStatusEnum.AUTHORIZE);
                default -> log.info("payment status undefined {}", transactionStatus);
            }
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }


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


        // ========================================================
        // SEND TOKEN
        // ========================================================
        String onchainReceipt;
        Optional<String> transactionReceipt = rampTransactionService.inquiryTransactionReceipt(orderId);

        if (transactionReceipt.isEmpty()) {
            onchainReceipt = null;

            if (transactionStatusEnum.equals(TransactionStatusEnum.CAPTURE)) {
                OnrampTransaction onrampTransaction = rampTransactionService.inquiryByOrderId(orderId);
                onchainReceipt = erc20MiddlewareService.transfer(
                        onrampTransaction.getChain(),
                        onrampTransaction.getErc20Address(),
                        onrampTransaction.getWalletAddress(),
                        // TODO: Working on decimals
                        onrampTransaction.getGrossAmount().toString(),
                        ERC20MiddlewareService.ScOperation.WRITE
                );
            }
        } else {
            onchainReceipt = transactionReceipt.get();
        }

        // ========================================================
        // UPDATE TRANSACTION SECTION
        // ========================================================


        LocalDateTime settlementTimeDateTime = null;
        if (settlementTime != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            settlementTimeDateTime = LocalDateTime.parse(settlementTime, formatter);
        }

        OnrampTransaction onrampTransaction = rampTransactionService.updateTransactionOnRamp(
                OnrampTransaction.builder()
                        .orderId(orderId)
                        .transactionStatus(transactionStatus)
                        .transactionId(transactionId)
                        .settlementTime(settlementTimeDateTime)
                        .paymentType(paymentType)
                        .fraudStatus(fraudStatus)
                        .currency(currency)
                        .onchainReceipt(onchainReceipt)
                        .build()
        );

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
