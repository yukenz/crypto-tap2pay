package id.co.awan.tap2pay.utils;

import org.web3j.crypto.Keys;
import org.web3j.crypto.Sign;
import org.web3j.utils.Numeric;

import java.math.BigInteger;
import java.security.SignatureException;

public class EthSignUtils {

    public static String ecRecoverAddress(String saltPkMessage, String ethSignMessage) throws SignatureException {
        BigInteger ecPubKey = Sign.signedPrefixedMessageToKey(
                saltPkMessage.getBytes(),
                Sign.signatureDataFromHex(ethSignMessage)
        );

        return Numeric.prependHexPrefix(Keys.getAddress(ecPubKey));
    }

}
