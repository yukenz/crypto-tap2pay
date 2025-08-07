package id.co.awan.tap2pay;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Hex;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;

public class HashTests {

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    @Test
    void sha512Test() throws NoSuchAlgorithmException, NoSuchProviderException {

        String input = "Postman-157856885120010000.00VT-server-HJMpl9HLr_ntOKt5mRONdmKj";

        MessageDigest digest = MessageDigest.getInstance("SHA-512", "BC");
        byte[] hashBytes = digest.digest(input.getBytes(StandardCharsets.UTF_8));

        String signatrue = Hex.toHexString(hashBytes);
        System.out.println(signatrue);

    }

}
