package id.co.awan.tap2pay.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PostPaymentRequest {

    private String merchantId;
    private String merchantKey;
    private String terminalId;
    private String terminalKey;
    private String hashCard;
    private String hashPin;
    private BigInteger paymentAmount;

}
