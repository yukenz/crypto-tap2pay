package id.co.awan.tap2pay.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "hsm", schema = "tap2pay")
public class Hsm {

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "owner_address", nullable = true) // Boleh null karna akan diset di pertama kali register card
    private String ownerAddress;

    @Column(name = "secret_key", nullable = true) // Boleh null karna akan diset di pertama kali register card
    private String secretKey;

    @Column(name = "pin", nullable = true) // Boleh null karna akan diset di pertama kali register card
    private String pin;

}
