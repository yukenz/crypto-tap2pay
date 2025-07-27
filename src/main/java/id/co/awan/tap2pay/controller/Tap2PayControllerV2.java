package id.co.awan.tap2pay.controller;

import id.co.awan.tap2pay.model.dto.PostAccessCard;
import id.co.awan.tap2pay.model.dto.PostRegisterCard;
import id.co.awan.tap2pay.model.entity.Hsm;
import id.co.awan.tap2pay.repository.HsmRepository;
import id.co.awan.tap2pay.service.Tap2PayService;
import id.co.awan.tap2pay.service.WalleEIP712Service;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api/v2/tap2pay")
@RequiredArgsConstructor
public class Tap2PayControllerV2 {

    private final Tap2PayService tap2PayService;
    private final HsmRepository hsmRepository;
    private final WalleEIP712Service walleEIP712Service;

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
    ) throws Exception {

        String recoveredAddress = walleEIP712Service.validateCardSelfService(
                req.getHashCard(),
                req.getHashPin(),
                req.getEthSignMessage(),
                0,
                req.getSignerAddress()

        );

        tap2PayService.createCard(req.getHashCard(), req.getHashPin(), recoveredAddress);

        return ResponseEntity.ok(null);
    }


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
            PostAccessCard req
    ) throws Exception {

        String recoveredAddress = walleEIP712Service.validateCardSelfService(
                req.getHashCard(),
                req.getHashPin(),
                req.getEthSignMessage(),
                1,
                req.getSignerAddress()
        );


        Optional<Hsm> response = hsmRepository.findByIdAndPinAndOwnerAddress(
                req.getHashCard(),
                req.getHashPin(),
                recoveredAddress
        );

        return response
                .map(hsm -> ResponseEntity.ok(hsm.getSecretKey()))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));

    }

}
