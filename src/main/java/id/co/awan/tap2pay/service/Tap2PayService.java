package id.co.awan.tap2pay.service;

import id.co.awan.tap2pay.model.entity.Hsm;
import id.co.awan.tap2pay.model.entity.Merchant;
import id.co.awan.tap2pay.model.entity.Terminal;
import id.co.awan.tap2pay.repository.HsmRepository;
import id.co.awan.tap2pay.repository.TerminalRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.util.encoders.Hex;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class Tap2PayService {

    private final HsmRepository hsmRepository;
    private final TerminalRepository terminalRepository;

    public Merchant
    validateMerchant(
            Terminal terminal,
            String merchantId,
            String merchantKey
    ) {

        Merchant merchant = terminal.getMerchant();

        if (!merchantId.equals(merchant.getId())) {
            throw new IllegalArgumentException("Merchant ID for this Terminal isn't valid");
        }

        if (!merchantKey.equals(merchant.getKey())) {
            throw new IllegalArgumentException("Key for merchant ID isn't valid");
        }

        return merchant;
    }

    public Terminal
    validateTerminal(
            String terminalId,
            String terminalKey
    ) {

        return terminalRepository.findByIdAndKey(terminalId, terminalKey)
                .orElseThrow(() -> new IllegalArgumentException("Terminal validation exception"));

    }

    @Transactional
    public void
    resetRegisteredCard(
            String hashCardUUID
    ) {

        Hsm hsm = hsmRepository.findById(hashCardUUID)
                .orElse(null);

        if (hsm == null) {
            throw new IllegalArgumentException("Card not found");
        }

        hsm.setPin(null);
        hsm.setOwnerAddress(null);
        hsm.setSecretKey(null);

//        hsmRepository.save(hsm);
    }

    @Transactional
    public String
    createHsm(
            String saltPk,
            String ownerAddress
    ) throws NoSuchAlgorithmException {

        Hsm hsm = hsmRepository.findById(saltPk).orElse(null);

        if (hsm != null) {
            log.info("hsm found");
            return hsm.getSecretKey();
        }

        SecureRandom instanceStrong = SecureRandom.getInstanceStrong();
        String secretKey = Hex.toHexString(instanceStrong.generateSeed(32));

        Hsm entity = new Hsm();

        entity.setId(saltPk);
        entity.setSecretKey(secretKey);
        entity.setOwnerAddress(ownerAddress.toLowerCase());

        hsmRepository.save(entity);

        return secretKey;
    }

    public String
    accessCard(
            String saltPk,
            String ownerAddress
    ) throws NoSuchAlgorithmException {

        Hsm hsm = hsmRepository.findById(saltPk).orElse(null);

        if (hsm != null) {
            return "Wrong Card Number or PIN";
        }

        SecureRandom instanceStrong = SecureRandom.getInstanceStrong();
        String secretKey = Hex.toHexString(instanceStrong.generateSeed(32));

        Hsm entity = new Hsm();

        entity.setId(saltPk);
        entity.setSecretKey(secretKey);
        entity.setOwnerAddress(ownerAddress.toLowerCase());

        hsmRepository.save(entity);

        return secretKey;
    }

    public Boolean
    isHsmExist(
            String ownerAddress
    ) {
        return hsmRepository.existsByOwnerAddress(ownerAddress.toLowerCase());
    }

    @Transactional
    public void
    createCard(
            String hashCard,
            String hashPin,
            String ownerAddress
    ) throws NoSuchAlgorithmException {

        Optional<Hsm> hsmResult = hsmRepository.findById(hashCard);

        // Cord not issued
        if (hsmResult.isEmpty()) {
            throw new IllegalArgumentException("Card UUID not valid");
        }

        Hsm hsm = hsmResult.get();

        // Cord already registered with some address
        if (hsm.getOwnerAddress() != null) {
            throw new IllegalArgumentException("Card already registered");
        }

        hsm.setOwnerAddress(ownerAddress.toLowerCase());
        hsm.setPin(hashPin);
        String secretKey = Hex.toHexString(SecureRandom.getInstanceStrong().generateSeed(32));
        hsm.setSecretKey(secretKey);

        hsmRepository.save(hsm);

    }

}
