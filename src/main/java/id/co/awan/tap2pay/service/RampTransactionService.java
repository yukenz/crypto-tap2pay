package id.co.awan.tap2pay.service;

import id.co.awan.tap2pay.model.entity.OnrampTransaction;
import id.co.awan.tap2pay.repository.OnrampTransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RampTransactionService {

    private final OnrampTransactionRepository onrampTransactionRepository;


    // ========================================================
    // CREATE TRANSACTION SECTION
    // ========================================================

    /**
     * Buat transaksi OnRamp Phase 1
     */
    @Transactional
    public String createTransactionOnRampFirstPhase(
            String walletAddress,
            String chain,
            String erc20Address,
            BigInteger amount
    ) {

        // Pastikan tidak ada pending trx
        if (inquiryByWalletAddress(walletAddress).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Please complete previous transaction");
        }

        OnrampTransaction onrampTransaction = OnrampTransaction.builder()
                .walletAddress(walletAddress)
                .chain(chain)
                .erc20Address(erc20Address)
                .grossAmount(amount)
                .build();
        onrampTransactionRepository.save(onrampTransaction);

        return onrampTransaction.getOrderId();
    }

    /**
     * Buat transaksi OnRamp Phase 2 Sukses
     */
    @Transactional
    public void createTransactionOnRampSecondPhase(
            String orderId,
            String redirectUrl,
            String token
    ) {

        OnrampTransaction onrampTransaction = inquiryByOrderId(orderId);

        onrampTransaction.setRedirectUrl(redirectUrl);
        onrampTransaction.setToken(token);
        onrampTransaction.setTransactionStatus("pending");
        onrampTransactionRepository.save(onrampTransaction);
    }

    /**
     * Buat transaksi OnRamp Phase 2 Gagal
     */
    @Transactional
    public void errorTransactionOnRampSecondPhase(
            String orderId,
            String error
    ) {

        OnrampTransaction onrampTransaction = inquiryByOrderId(orderId);

        // Sempurnakan TRX
        onrampTransaction.setTransactionStatus("error");
        onrampTransaction.setErrorCause(error);
        onrampTransactionRepository.save(onrampTransaction);
    }

    // ========================================================
    // UPDATE TRANSACTION SECTION
    // ========================================================

    /**
     * Handle Payment Notif OnRamp
     */
    @Transactional
    public OnrampTransaction updateTransactionOnRamp(
            OnrampTransaction entity
    ) {

        OnrampTransaction onrampTransaction = inquiryByOrderId(entity.getOrderId());

        // Prohibited Double Update
        if (onrampTransaction.getTransactionStatus().equalsIgnoreCase(entity.getTransactionStatus())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Transaction status already set");
        }

        // Update NOTIFICATION
        onrampTransaction.setTransactionStatus(entity.getTransactionStatus());
        onrampTransaction.setTransactionId(entity.getTransactionId());
        onrampTransaction.setSettlementTime(entity.getSettlementTime());
        onrampTransaction.setPaymentType(entity.getPaymentType());
        onrampTransaction.setFraudStatus(entity.getFraudStatus());
        onrampTransaction.setCurrency(entity.getCurrency());
        onrampTransactionRepository.save(onrampTransaction);

        return onrampTransaction;
    }


    // ========================================================
    // NON TRANSACTIONAL
    // ========================================================

    /**
     * Get TransactionReceiot
     */
    public Optional<String> inquiryTransactionReceipt(String orderId) {
        return onrampTransactionRepository
                .findTransactionReceiptById(orderId);
    }

    /**
     * Inquiry OnRamp Transaction Metadata by OrderId
     */
    public OnrampTransaction inquiryByOrderId(String orderId) {
        return onrampTransactionRepository
                .findById(orderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));
    }

    /**
     * Inquiry OnRamp Transaction Metadata by WalletAddress
     */
    public Optional<OnrampTransaction> inquiryByWalletAddress(String walletAddress) {
        return onrampTransactionRepository
                .findByWalletAddressAndTransactionStatus(walletAddress, "pending");
    }

    /**
     * Inquiry OnRamp Transaction History
     */
    public List<OnrampTransaction> historyOnRamp(String walletAddress) {
        return onrampTransactionRepository.findAllByWalletAddress(walletAddress);
    }
}
