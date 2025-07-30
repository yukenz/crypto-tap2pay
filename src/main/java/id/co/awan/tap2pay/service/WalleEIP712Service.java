package id.co.awan.tap2pay.service;


import id.co.awan.tap2pay.repository.WalleEIP712Repository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.DynamicBytes;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Bytes32;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.abi.datatypes.generated.Uint8;
import org.web3j.utils.Numeric;

@Service
public class WalleEIP712Service {

    @Value("${web3j.rpc-url}")
    String rpcUrl;

    @Value("${web3j.contract.walle-eip712}")
    String walleEIP712Address;

    private Address getSignerCardSelfService(
            Uint8 operation,
            Bytes32 hashCard,
            Bytes32 hashPin,
            DynamicBytes signature
    ) throws Exception {

        WalleEIP712Repository instance = WalleEIP712Repository.getInstance(rpcUrl, walleEIP712Address);
        return instance.getSignerCardSelfService(operation, hashCard, hashPin, signature)
                .send();
    }

    public Address getSignerCardRequestPayment(
            Bytes32 hashCard, Bytes32 hashPin,
            Utf8String merchantId, Utf8String merchantKey,
            Utf8String terminalId, Utf8String terminalKey,
            Uint256 paymentAmount,
            DynamicBytes signature
    ) throws Exception {

        WalleEIP712Repository instance = WalleEIP712Repository.getInstance(rpcUrl, walleEIP712Address);
        return instance.getSignerCardRequestPayment(
                        hashCard, hashPin,
                        merchantId, merchantKey,
                        terminalId, terminalKey,
                        paymentAmount,
                        signature
                )
                .send();
    }


    /**
     * Validasi EIP712 CardSelfService
     *
     * @return Recovered Address
     * @throws Exception Exception apabila signer tidak match dengan recovered address
     */
    public String validateCardSelfService(
            String hashCard,
            String hashPin,
            String signTypedMessage,
            long enumOperation,
            String signerAddres
    ) throws Exception {

        Bytes32 _hashCard = new Bytes32(Numeric.hexStringToByteArray(hashCard));
        Bytes32 _hashPin = new Bytes32(Numeric.hexStringToByteArray(hashPin));
        DynamicBytes _signature = new DynamicBytes((Numeric.hexStringToByteArray(signTypedMessage)));
        Uint8 _enumOperation = new Uint8(enumOperation); // Register is 0 | Access is 1

        Address rawRecoveredAddress = getSignerCardSelfService(
                _enumOperation,
                _hashCard,
                _hashPin,
                _signature
        );

        String recoveredAddress = rawRecoveredAddress.getValue();

        if (!recoveredAddress.equalsIgnoreCase(signerAddres)) {
            throw new IllegalArgumentException("Signature Address recover not match signer");
        }

        return recoveredAddress;
    }


}
