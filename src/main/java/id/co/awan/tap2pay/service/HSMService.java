package id.co.awan.tap2pay.service;

import id.co.awan.tap2pay.model.entity.Hsm;
import id.co.awan.tap2pay.repository.HsmRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class HSMService {

    private final HsmRepository hsmRepository;

    @Transactional
    public void
    changePin(
            String hashCard,
            String hashPin,
            String newHashPin,
            String ownerAddress
    ) {

        Hsm hsm = getHsm(hashCard, hashPin, ownerAddress)
                .orElseThrow(() -> new IllegalArgumentException("HSM Not Found"));
        hsm.setPin(newHashPin);

        hsmRepository.save(hsm);
    }

    public Optional<Hsm>
    getHsm(
            String hashCard,
            String hashPin,
            String ownerAddress
    ) {
        return hsmRepository.findByIdAndPinAndOwnerAddress(
                hashCard,
                hashPin,
                ownerAddress
        );
    }

    public Optional<Hsm>
    getHsm(
            String hashCard,
            String hashPin
    ) {
        return hsmRepository.findByIdAndPin(
                hashCard,
                hashPin
        );
    }


    @Transactional
    public void resetHsm(String hashCardUUID) {

        Hsm hsm = hsmRepository.findById(hashCardUUID)
                .orElse(null);

        if (hsm == null) {
            throw new IllegalArgumentException("Card not found");
        }

        hsm.setPin(null);
        hsm.setOwnerAddress(null);
        hsm.setSecretKey(null);

        hsmRepository.save(hsm);
    }


}
