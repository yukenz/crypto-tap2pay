package id.co.awan.tap2pay.model.dto.midtrans.notification;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MNPaymentCreditCard {

    @JsonProperty("transaction_time")
    private String transactionTime;

    @JsonProperty("transaction_status")
    private String transactionStatus;

    @JsonProperty("transaction_id")
    private String transactionId;

    @JsonProperty("status_message")
    private String statusMessage;

    @JsonProperty("status_code")
    private String statusCode;

    @JsonProperty("signature_key")
    private String signatureKey;

    @JsonProperty("payment_type")
    private String paymentType;

    @JsonProperty("order_id")
    private String orderId;

    @JsonProperty("merchant_id")
    private String merchantId;

    @JsonProperty("masked_card")
    private String maskedCard;

    @JsonProperty("gross_amount")
    private String grossAmount;

    @JsonProperty("fraud_status")
    private String fraudStatus;

    @JsonProperty("eci")
    private String eci;

    @JsonProperty("currency")
    private String currency;

    @JsonProperty("channel_response_message")
    private String channelResponseMessage;

    @JsonProperty("channel_response_code")
    private String channelResponseCode;

    @JsonProperty("card_type")
    private String cardType;

    @JsonProperty("bank")
    private String bank;

    @JsonProperty("approval_code")
    private String approvalCode;

}
