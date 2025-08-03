package id.co.awan.tap2pay.utils;

import id.co.awan.tap2pay.exception.EthSignValidationException;
import org.web3j.crypto.Keys;
import org.web3j.crypto.Sign;
import org.web3j.utils.Numeric;

import java.math.BigInteger;
import java.security.SignatureException;

public class EthSignUtils {

    /**
     * Melakukan EC Recover pada Signature yang dibuat menggunakan eth_sign
     *
     * @param message        Message
     * @param ethSignMessage Signature Message
     * @return Recovered Address
     */
    public static String ecRecoverAddress(String message, String ethSignMessage) throws SignatureException {

        BigInteger ecPubKey = Sign.signedPrefixedMessageToKey(
                message.getBytes(),
                Sign.signatureDataFromHex(ethSignMessage)
        );

        return Numeric.prependHexPrefix(Keys.getAddress(ecPubKey));
    }

    /**
     * Melakukan EC Recover pada Signature yang dibuat menggunakan eth_sign
     *
     * @param message        Message
     * @param ethSignMessage Signature Message
     * @return Recovered Address
     */
    public static String ecRecoverAddressWithValidation(String message, String ethSignMessage, String signerAddress) throws SignatureException, EthSignValidationException {

        BigInteger ecPubKey = Sign.signedPrefixedMessageToKey(
                message.getBytes(),
                Sign.signatureDataFromHex(ethSignMessage)
        );

        String addressRecovered = Numeric.prependHexPrefix(Keys.getAddress(ecPubKey));

        if (!addressRecovered.equalsIgnoreCase(signerAddress)) {
            throw new EthSignValidationException("Signature Address recover not match signer");
        }

        return addressRecovered;
    }

}
