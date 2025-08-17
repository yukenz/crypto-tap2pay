package id.co.awan.tap2pay.repository;

import id.co.awan.tap2pay.model.entity.OnrampTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OnrampTransactionRepository extends JpaRepository<OnrampTransaction, String> {

    Optional<OnrampTransaction> findByWalletAddressAndTransactionStatus(String walletAddress, String transactionStatus);

}
