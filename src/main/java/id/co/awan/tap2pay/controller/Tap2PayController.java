package id.co.awan.tap2pay.controller;

import id.co.awan.tap2pay.model.dto.*;
import id.co.awan.tap2pay.model.entity.Hsm;
import id.co.awan.tap2pay.model.entity.Merchant;
import id.co.awan.tap2pay.model.entity.Terminal;
import id.co.awan.tap2pay.repository.HsmRepository;
import id.co.awan.tap2pay.service.ERC20Service;
import id.co.awan.tap2pay.service.Tap2PayService;
import id.co.awan.tap2pay.service.WalleEIP712Service;
import id.co.awan.tap2pay.utils.EthSignUtils;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    private final ERC20Service erc20Service;
    private final WalleEIP712Service walleEIP712Service;


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
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));

    }


    @Operation(
            summary = "Inquiry Merchant"
    )
    @PostMapping(
            path = "/merchant-inquiry",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<PostInquiryMerchantResponse>
    merchantInquiry(
            @RequestBody
            PostInquiryMerchantRequest request
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

        PostInquiryMerchantResponse response = PostInquiryMerchantResponse.builder()
                .merchantName(merchant.getName())
                .merchantAddress(merchant.getAddress())
                .build();

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Inquiry Merchant"
    )
    @PostMapping(
            path = "/open-payment",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<PostOpenPaymentResponse>
    openPayment(
            @RequestBody
            PostOpenPaymentRequest request
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


        return ResponseEntity.ok(null);
    }

    @Operation(
            summary = "Do Payment, actually do Inquiry"
    )
    @PostMapping(
            path = "/payment",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<PostPaymentResponse>
    payment(
            @RequestBody
            PostPaymentRequest request
    ) throws Exception {

        // Validate Terminal
        Terminal terminal = tap2PayService.validateTerminal(
                request.getTerminalId(),
                request.getTerminalKey()
        );

        // Validate Merchant
        tap2PayService.validateMerchant(
                terminal,
                request.getMerchantId(),
                request.getMerchantKey()
        );


        // Validate Card via HSM
        Hsm hsm = hsmRepository.findByIdAndPin(request.getHashCard(), request.getHashPin())
                .orElse(null);
        if (hsm == null) {
            throw new IllegalArgumentException("Hsm not found");
        }

        // Validate wallet balance enought
//        BigInteger ownerBalance = erc20Service.getAccountBalance(hsm.getOwnerAddress());
//        if (ownerBalance.compareTo(request.getPaymentAmount()) < 0) {
//            throw new IllegalArgumentException("Owner balance less than payment amount");
//        }

        PostPaymentResponse response = PostPaymentResponse.builder()
                .fromAddress(hsm.getOwnerAddress())
                .secretKey(hsm.getSecretKey())
                .build();

        return ResponseEntity.ok(response);
    }


}
