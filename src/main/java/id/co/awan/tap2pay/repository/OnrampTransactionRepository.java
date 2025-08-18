package id.co.awan.tap2pay.repository;

import id.co.awan.tap2pay.model.entity.OnrampTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OnrampTransactionRepository extends JpaRepository<OnrampTransaction, String> {

    Optional<OnrampTransaction> findByWalletAddressAndTransactionStatus(String walletAddress, String transactionStatus);

    List<OnrampTransaction> findAllByWalletAddress(String walletAddress);

    @Query("SELECT e.onchainReceipt FROM OnrampTransaction e WHERE e.orderId = :orderId")
    Optional<String> findTransactionReceiptById(@Param("orderId") String orderId);

}
