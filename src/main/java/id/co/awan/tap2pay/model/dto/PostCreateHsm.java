package id.co.awan.tap2pay.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor

public class PostCreateHsm {
    private String pin;
    private String uuid;
}
