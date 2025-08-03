package id.co.awan.tap2pay.model.dto;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostPaymentResponse {

    private String fromAddress; // Wallet Address
    private String toAddress; // Merchant Address
    private String secretKey; // HSM Secret Key

}
