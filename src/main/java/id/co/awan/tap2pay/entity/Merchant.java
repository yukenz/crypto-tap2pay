package id.co.awan.tap2pay.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "merchant", schema = "tap2pay")
public class Merchant {
    @Id
    @Column(name = "id", nullable = false, length = Integer.MAX_VALUE)
    private String id;

    @Column(name = "name", length = Integer.MAX_VALUE)
    private String name;

    @Column(name = "key", length = Integer.MAX_VALUE)
    private String key;

    @OneToMany(mappedBy = "merchant")
    private Set<Terminal> terminals = new LinkedHashSet<>();

    @NotNull
    @Column(name = "address", nullable = false, length = Integer.MAX_VALUE)
    private String address;

}