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
public class VaNumber {

    @JsonProperty("va_number")
    private String vaNumber;

    @JsonProperty("bank")
    private String bank;

}
