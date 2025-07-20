package id.co.awan.tap2pay.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "terminal", schema = "tap2pay")
public class Terminal {
    @Id
    @Column(name = "id", nullable = false)
    private String id;

    @Column(name = "key")
    private String key;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "merchant_id")
    private Merchant merchant;

}