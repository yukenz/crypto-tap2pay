package id.co.awan.tap2pay.service;

import id.co.awan.tap2pay.model.entity.OnrampTransaction;
import id.co.awan.tap2pay.repository.OnrampTransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigInteger;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RampTransactionService {

    private final OnrampTransactionRepository onrampTransactionRepository;

    @Transactional
    public String createTransactionOnRampFirstPhase(
            String walletAddress,
            BigInteger amount
    ) {

        // Pastikan tidak ada pending trx
        if (inquiryOnRamp(walletAddress).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Please complete previous transaction");
        }

        // Buatkan TRX
        OnrampTransaction onrampTransaction = new OnrampTransaction();
        onrampTransaction.setWalletAddress(walletAddress);
        onrampTransaction.setGrossAmount(amount);
        onrampTransactionRepository.save(onrampTransaction);

        return onrampTransaction.getOrderId();
    }

    @Transactional
    public void createTransactionOnRampSecondPhase(
            String orderId,
            String redirectUrl,
            String token
    ) {

        OnrampTransaction onrampTransaction = onrampTransactionRepository.findById(orderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));

        // Sempurnakan TRX
        onrampTransaction.setRedirectUrl(redirectUrl);
        onrampTransaction.setToken(token);
        onrampTransaction.setTransactionStatus("pending");
        onrampTransactionRepository.save(onrampTransaction);
    }

    // Inquiry Status Deposit
    public Optional<OnrampTransaction> inquiryOnRamp(String walletAddress) {
        return onrampTransactionRepository
                .findByWalletAddressAndTransactionStatus(walletAddress, "pending");
    }

    // Inquiry History Deposit
    public void historyOnRamp(String walletAddress) {

    }
}
