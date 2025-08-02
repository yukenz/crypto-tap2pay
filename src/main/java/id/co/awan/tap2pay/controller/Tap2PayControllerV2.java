package id.co.awan.tap2pay.controller;

import id.co.awan.tap2pay.constant.CardSelfServiceOperation;
import id.co.awan.tap2pay.model.dto.*;
import id.co.awan.tap2pay.model.entity.Hsm;
import id.co.awan.tap2pay.model.entity.Terminal;
import id.co.awan.tap2pay.service.EthereumService;
import id.co.awan.tap2pay.service.HSMService;
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
import org.springframework.web.server.ResponseStatusException;

import java.security.SignatureException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v2/tap2pay")
@RequiredArgsConstructor
public class Tap2PayControllerV2 {

    private final Tap2PayService tap2PayService;
    private final WalleEIP712Service walleEIP712Service;
    private final HSMService hsmService;
    private final EthereumService ethereumService;


    /**
     * Endpoint untuk mendapatkan kartu yang sudah didaftarkan.
     *
     * @param ethSignMessage ETH Sign berupa message <b>CARD_QUERY</b>
     * @return HTTP 200 OK dengan array HashCard
     */
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
        List<String> cards = hsmService.getCards(addressRecovered);

        return ResponseEntity.ok(cards);
    }

    /**
     * Endpoint untuk melakukan registrasi kartu.
     *
     * @param request Hash Card, Hash PIN, ETH Typed Sign, Signer Address untuk verifikasi pendaftarakan kartu.,
     * @return HTTP 200 OK dengan null body jika sukses, Non 200 jika gagal dengan keterangan JSON
     */
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
            PostRegisterCard request
    ) throws Exception {

        String recoveredAddress = walleEIP712Service.validateCardSelfService(
                request.getHashCard(),
                request.getHashPin(),
                request.getEthSignMessage(),
                CardSelfServiceOperation.REGISER.ordinal(),
                request.getSignerAddress()
        );

        tap2PayService.createCard(request.getHashCard(), request.getHashPin(), recoveredAddress);
        return ResponseEntity.ok(null);
    }


    /**
     * Endpoint untuk melakukan akses kartu.
     *
     * @param request Hash Card, Hash PIN, ETH Typed Sign, Signer Address untuk verifikasi akses kartu.,
     * @return HTTP 200 OK dengan text/plain body berisi secret key jika sukses, Non 200 jika gagal dengan keterangan JSON
     */
    @Operation(
            summary = "Access Card"
    )
    @PostMapping(
            path = "/card-access",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.TEXT_PLAIN_VALUE
    )
    public ResponseEntity<String>
    accessCard(
            @RequestBody
            PostAccessCard request
    ) throws Exception {

        String recoveredAddress = walleEIP712Service.validateCardSelfService(
                request.getHashCard(),
                request.getHashPin(),
                request.getEthSignMessage(),
                CardSelfServiceOperation.ACCESS.ordinal(),
                request.getSignerAddress()
        );

        Optional<Hsm> response = hsmService.getHsm(
                request.getHashCard(),
                request.getHashPin(),
                recoveredAddress
        );

        return response
                .map(hsm -> ResponseEntity.ok(hsm.getSecretKey()))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));

    }


    /**
     * Endpoint untuk request payment
     */
    @Operation(
            summary = "Do Payment Request"
    )
    @PostMapping(
            path = "/payment-request",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<PostPaymentResponse>
    paymentRequest(
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


        // Find HSM
        Hsm hsm = hsmService.getHsm(request.getHashCard(), request.getHashPin())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "HSM Not Found"));

        PostPaymentResponse response = PostPaymentResponse.builder()
                .fromAddress(hsm.getOwnerAddress())
                .secretKey(hsm.getSecretKey())
                .build();

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Do Payment Request"
    )
    @PostMapping(
            path = "/card-gass-recovery",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<PostPaymentResponse>
    cardGassRecovery(
            @RequestBody
            PostCardGassRecoveryRequest request
    ) {

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

        // Try to recover card gas availability
        ethereumService.recoverCardGasAvailability(request.getCardAddress());
        return ResponseEntity.ok(null);
    }

}
