package id.co.awan.tap2pay.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PostOpenPaymentResponse {

    // ERC-20 Token Support
    private List<String> tokens;

}
