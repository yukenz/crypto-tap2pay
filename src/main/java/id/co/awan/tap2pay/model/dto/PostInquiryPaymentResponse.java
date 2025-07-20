package id.co.awan.tap2pay.model.dto;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostInquiryPaymentResponse {

    private String merchantName;
    private String merchantAddress;

}
