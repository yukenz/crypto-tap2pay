package id.co.awan.tap2pay.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PostCardAccess {

    private String signerAddress;
    private String ethSignMessage;
    private String hashCard;
    private String hashPin;

}
