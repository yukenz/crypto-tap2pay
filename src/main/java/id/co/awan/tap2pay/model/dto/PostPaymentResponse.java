package id.co.awan.tap2pay.model.dto;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostPaymentResponse {

    private String fromAddress;
    private String secretKey;

}
