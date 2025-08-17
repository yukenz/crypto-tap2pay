package id.co.awan.tap2pay.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Generated;
import org.hibernate.generator.EventType;
import org.web3j.abi.datatypes.Int;

import java.math.BigInteger;
import java.time.Instant;
import java.time.LocalDate;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "onramp", schema = "tap2pay")
public class OnrampTransaction {


    /* =================================================================================================================
     * CREATIONAL
     * =================================================================================================================
     */

    @Id
    @Column(name = "id", updatable = false, nullable = false, insertable = false)
    @Generated(event = {EventType.INSERT})
    private String orderId;

    @Column(name = "gross_amount", nullable = false)
    private BigInteger grossAmount;

    @Column(name = "wallet_address", nullable = false)
    private String walletAddress;

    @Column(name = "redirect_url", nullable = true)
    private String redirectUrl;

    @Column(name = "token", nullable = true)
    private String token;

    /* =================================================================================================================
     * NOTIFICATION
     * =================================================================================================================
     */

    @Column(name = "transaction_status", nullable = true)
    private String transactionStatus;

    @Column(name = "transaction_id", nullable = true)
    private String transactionId;

//    @Temporal(TemporalType.DATE)
    @Column(name = "settlement_time", nullable = true)
    private LocalDate settlementTime;

    @Column(name = "payment_type", nullable = true)
    private String paymentType;

    @Column(name = "fraud_status", nullable = true)
    private String fraudStatus;

    @Column(name = "currency", nullable = true)
    private String currency;

}
