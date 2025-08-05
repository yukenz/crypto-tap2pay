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
public class PaymentAmount {

    @JsonProperty("paid_at")
    private String paidAt;

    @JsonProperty("amount")
    private String amount;

}
