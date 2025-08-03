package id.co.awan.tap2pay.service;

import id.co.awan.tap2pay.model.entity.Hsm;
import id.co.awan.tap2pay.repository.HsmRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class HSMService {

    private final HsmRepository hsmRepository;

    public Optional<Hsm> getHsm(
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

    public Optional<Hsm> getHsm(
            String hashCard,
            String hashPin
    ) {
        return hsmRepository.findByIdAndPin(
                hashCard,
                hashPin
        );
    }

    public List<String> getCards(String ownerAddress) {

        return hsmRepository.findAllByOwnerAddress(ownerAddress)
                .stream()
                .map(Hsm::getId)
                .toList();

    }

}
