package id.co.awan.tap2pay.controller;

import id.co.awan.tap2pay.model.dto.*;
import id.co.awan.tap2pay.model.entity.Hsm;
import id.co.awan.tap2pay.model.entity.Merchant;
import id.co.awan.tap2pay.model.entity.Terminal;
import id.co.awan.tap2pay.repository.HsmRepository;
import id.co.awan.tap2pay.service.Tap2PayService;
import id.co.awan.tap2pay.utils.EthSignUtils;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/tap2pay")
@RequiredArgsConstructor
public class Tap2PayController {


    private final Tap2PayService tap2PayService;
    private final HsmRepository hsmRepository;

    @Operation(
            summary = "Request user secret key"
    )
    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    @Transactional
    public ResponseEntity<?>
    createHsm(
            @RequestBody
            CreateHsm req
    ) throws NoSuchAlgorithmException, SignatureException {

        String saltPkMessage = req.getSaltPkMessage();
        String ethSignMessage = req.getEthSignMessage();
        String walletAddress = req.getWalletAddress();

        String addressRecovered = EthSignUtils.ecRecoverAddress(saltPkMessage, ethSignMessage);

        if (!addressRecovered.equalsIgnoreCase(walletAddress)) {
            throw new IllegalArgumentException("Signature Address recover not match signer");
        }

        String secretKey = tap2PayService.createHsm(saltPkMessage, addressRecovered);

        return ResponseEntity.ok(secretKey);
    }

    @Operation(
            summary = "Request check user secret key"
    )
    @GetMapping(
    )
    @Transactional
    public ResponseEntity<String>
    createHsm(
            @RequestParam(name = "ownerAddress")
            String ownerAddress
    ) {

        Boolean hsmExist = tap2PayService.isHsmExist(ownerAddress);
        return ResponseEntity.ok(hsmExist.toString());
    }


    @Operation(
            summary = "RegisterCard"
    )
    @PostMapping(
            path = "/card-register",
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<String>
    cardRegistration(
            @RequestBody
            PostRegisterCard req
    ) throws NoSuchAlgorithmException, SignatureException {


        String hashCard = req.getHashCard();
        String hashPin = req.getHashPin();
        String message = hashCard.concat(hashPin);

        String addressRecovered = EthSignUtils.ecRecoverAddress(message, req.getEthSignMessage());

        if (!addressRecovered.equalsIgnoreCase(req.getSignerAddress())) {
            throw new IllegalArgumentException("Signature Address recover not match signer");
        }

        tap2PayService.createCard(hashCard, hashPin, addressRecovered);

        return ResponseEntity.ok(null);
    }

    @Operation(
            summary = "Query Cards"
    )
    @PostMapping(
            path = "/cards",
            consumes = MediaType.TEXT_PLAIN_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<List<String>>
    getCards(
            @RequestBody
            String ethSignMessage
    ) throws SignatureException {

        String addressRecovered = EthSignUtils.ecRecoverAddress("CARD_QUERY", ethSignMessage);

        List<String> response = hsmRepository.findAllByOwnerAddress(addressRecovered.toLowerCase())
                .stream()
                .map(Hsm::getId)
                .toList();


        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Access Card"
    )
    @PostMapping(
            path = "/access-card",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.TEXT_PLAIN_VALUE
    )
    public ResponseEntity<String>
    accessCard(
            @RequestBody
            PostAccessCard req
    ) throws SignatureException {

        String addressRecovered = EthSignUtils.ecRecoverAddress("CARD_ACCESS", req.getEthSignMessage());

        if (!addressRecovered.equalsIgnoreCase(req.getSignerAddress())) {
            throw new IllegalArgumentException("Signature Address recover not match signer");
        }


        Optional<Hsm> response = hsmRepository.findByIdAndPinAndOwnerAddress(
                req.getHashCard(),
                req.getHashPin(),
                addressRecovered
        );

        return response
                .map(hsm -> ResponseEntity.ok(hsm.getSecretKey()))
                .orElseGet(() -> ResponseEntity.ok(null));

    }

    @Operation(
            summary = "Access Card"
    )
    @PostMapping(
            path = "/resetRegisteredCardTest",
            consumes = MediaType.TEXT_PLAIN_VALUE,
            produces = MediaType.TEXT_PLAIN_VALUE
    )
    public ResponseEntity<String>
    resetRegisteredCardForTest(
            @RequestBody
            String hashCardUUID
    ) {

        tap2PayService.resetRegisteredCard(hashCardUUID);

        return ResponseEntity.ok(hashCardUUID);
    }

    @Operation(
            summary = "Inquiry Payment"
    )
    @PostMapping(
            path = "/payment-inquiry",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<PostInquiryPaymentResponse>
    inquiryPayment(
            @RequestBody
            PostInquiryPaymentRequest request
    ) {

        Terminal terminal = tap2PayService.validateTerminal(
                request.getTerminalId(),
                request.getTerminalKey()
        );

        Merchant merchant = tap2PayService.validateMerchant(
                terminal,
                request.getMerchantId(),
                request.getMerchantKey()
        );

        PostInquiryPaymentResponse response = PostInquiryPaymentResponse.builder()
                .merchantName(merchant.getName())
                .merchantAddress(merchant.getAddress())
                .build();

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Inquiry Payment"
    )
    @PostMapping(
            path = "/payment",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<String>
    payment(
            @RequestBody
            PostPaymentRequest request
    ) {

        Terminal terminal = tap2PayService.validateTerminal(
                request.getTerminalId(),
                request.getTerminalKey()
        );

        tap2PayService.validateMerchant(
                terminal,
                request.getMerchantId(),
                request.getMerchantKey()
        );

        Hsm hsm = hsmRepository.findByIdAndPin(request.getHashCard(), request.getHashPin())
                .orElse(null);

        if (hsm == null) {
            throw new IllegalArgumentException("Hsm not found");
        }

        return ResponseEntity.ok(hsm.getSecretKey());
    }


}
