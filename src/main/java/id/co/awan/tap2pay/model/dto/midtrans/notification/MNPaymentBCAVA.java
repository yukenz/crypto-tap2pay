package id.co.awan.tap2pay.model.dto.midtrans.notification;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MNPaymentBCAVA {

    @JsonProperty("transaction_time")
    private String transactionTime;

    @JsonProperty("transaction_time")
    private String transactionStatus;

    @JsonProperty("transaction_time")
    private String transactionId;

    @JsonProperty("transaction_time")
    private String statusMessage;

    @JsonProperty("transaction_time")
    private String statusCode;

    @JsonProperty("transaction_time")
    private String signatureKey;

    @JsonProperty("transaction_time")
    private String settlementTime;

    @JsonProperty("transaction_time")
    private String paymentType;

    @JsonProperty("transaction_time")
    private String paymentAmounts;

    @JsonProperty("transaction_time")
    private String orderId;

    @JsonProperty("transaction_time")
    private String merchantId;

    @JsonProperty("transaction_time")
    private String grossAmount;

    @JsonProperty("transaction_time")
    private String fraudStatus;

    @JsonProperty("va_numbers")
    private List<VaNumber> vaNumbers;


}
