package id.co.awan.tap2pay.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import id.co.awan.tap2pay.model.dto.midtrans.notification.*;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.util.encoders.Hex;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

@Service

@RequiredArgsConstructor
@Slf4j
public class MidtransNotificationService {

    private final ObjectMapper objectMapper;

    @Value("${midtrans.server-key}")
    @Setter
    private String serverKey;

    public void validateSignature(JsonNode request) throws NoSuchAlgorithmException, NoSuchProviderException {

        String orderId = request.at("/order_id").asText();
        String statusCode = request.at("/status_code").asText();
        String grossAmount = request.at("/gross_amount").asText();

        MessageDigest digest = MessageDigest.getInstance("SHA-512", "BC");
        byte[] hashBytes = digest.digest((orderId + statusCode + grossAmount + serverKey)
                .getBytes(StandardCharsets.UTF_8));

        String signatureKey = request.at("/signature_key").asText();
        String signatrue = Hex.toHexString(hashBytes);

        if (!signatureKey.equalsIgnoreCase(signatrue)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Signature Key isn't valid");
        }
    }

    public void creditCardProcessPayment(JsonNode request) throws JsonProcessingException {

        MNPaymentCreditCard mnPaymentCreditCard = objectMapper.treeToValue(request, new TypeReference<MNPaymentCreditCard>() {
        });

    }

    public void gopayProcessPayment(JsonNode request) throws JsonProcessingException {

        MNPaymentGopay mnPaymentGopay = objectMapper.treeToValue(request, new TypeReference<MNPaymentGopay>() {
        });

    }

    public void qrisProcessPayment(JsonNode request) throws JsonProcessingException {

        MNPaymentQris mnPaymentQris = objectMapper.treeToValue(request, new TypeReference<MNPaymentQris>() {
        });

    }

    public void shopeepayProcessPayment(JsonNode request) throws JsonProcessingException {
        MNPaymentShopeePay mnPaymentShopeePay = objectMapper.treeToValue(request, new TypeReference<MNPaymentShopeePay>() {
        });

    }

    public void echannelProcessPayment(JsonNode request) throws JsonProcessingException {
        MNPaymentMandiriBill mnPaymentMandiriBill = objectMapper.treeToValue(request, new TypeReference<MNPaymentMandiriBill>() {
        });

    }

    public void cstoreProcessPayment(JsonNode request) {

    }

    public void akulakuProcessPayment(JsonNode request) throws JsonProcessingException {
        MNAkulaku mnAkulaku = objectMapper.treeToValue(request, new TypeReference<MNAkulaku>() {
        });
    }


    /* ========================================================================================
     * Midtrans Notification Payment Bank Transfer
     * ======================================================================================== */
    public void bankTransferProcessPayment(JsonNode request) throws JsonProcessingException {

        switch (request.at("/va_numbers/0/bank").asText()) {
            case "bca" -> bcaBankTransferProcessPayment(request);
            case "bni" -> bniBankTransferProcessPayment(request);
            case "bri" -> briBankTransferProcessPayment(request);
            default -> defaultBankTransferProcessPayment(request);
        }
    }

    public void bcaBankTransferProcessPayment(JsonNode request) throws JsonProcessingException {
        MNPaymentBCAVA mnPaymentCreditCard = objectMapper.treeToValue(request, new TypeReference<MNPaymentBCAVA>() {
        });
    }

    public void bniBankTransferProcessPayment(JsonNode request) throws JsonProcessingException {
        MNPaymenBNIVA mnPaymenBNIVA = objectMapper.treeToValue(request, new TypeReference<MNPaymenBNIVA>() {
        });

    }

    public void briBankTransferProcessPayment(JsonNode request) throws JsonProcessingException {
        MNPaymenBRIVA mnPaymenBRIVA = objectMapper.treeToValue(request, new TypeReference<MNPaymenBRIVA>() {
        });

    }

    public void defaultBankTransferProcessPayment(JsonNode request) throws JsonProcessingException {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unimplement Bank Transfer Process");
    }

    public void defaultProcessPayment(JsonNode request) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Undefined Payment Type Process");

    }
}
