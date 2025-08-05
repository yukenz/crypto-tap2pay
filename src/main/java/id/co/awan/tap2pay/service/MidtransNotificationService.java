package id.co.awan.tap2pay.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import id.co.awan.tap2pay.model.dto.midtrans.notification.MNPaymentBCAVA;
import id.co.awan.tap2pay.model.dto.midtrans.notification.MNPaymentCreditCard;
import id.co.awan.tap2pay.model.dto.midtrans.notification.MNPaymentGopay;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service

@RequiredArgsConstructor
@Slf4j
public class MidtransNotificationService {

    private final ObjectMapper objectMapper;

    public void creditCardProcessPayment(JsonNode request) throws JsonProcessingException {

        MNPaymentCreditCard mnPaymentCreditCard = objectMapper.treeToValue(request, new TypeReference<MNPaymentCreditCard>() {
        });

    }

    public void gopayProcessPayment(JsonNode request) throws JsonProcessingException {

        MNPaymentGopay mnPaymentCreditCard = objectMapper.treeToValue(request, new TypeReference<MNPaymentGopay>() {
        });

    }

    public void qrisProcessPayment(JsonNode request) {
    }

    public void shopeepayProcessPayment(JsonNode request) {
    }

    public void echannelProcessPayment(JsonNode request) {
    }

    public void cstoreProcessPayment(JsonNode request) {
    }

    public void akulakuProcessPayment(JsonNode request) {
    }

    public void defaultProcessPayment(JsonNode request) {
    }

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
    }

    public void briBankTransferProcessPayment(JsonNode request) throws JsonProcessingException {
    }

    public void defaultBankTransferProcessPayment(JsonNode request) throws JsonProcessingException {
    }


}
