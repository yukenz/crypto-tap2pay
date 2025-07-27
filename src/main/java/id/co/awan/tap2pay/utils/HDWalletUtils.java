package id.co.awan.tap2pay.utils;

import org.springframework.lang.Nullable;
import org.web3j.crypto.Bip32ECKeyPair;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.MnemonicUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.utils.Numeric;

import java.io.IOException;
import java.math.BigInteger;

public class HDWalletUtils {

    public static Credentials createCredential(
            String mnemonic,
            @Nullable String passphrase,
            int indexAddress
    ) {
        byte[] seed = MnemonicUtils.generateSeed(mnemonic, passphrase);
        Bip32ECKeyPair masterKeyPair = Bip32ECKeyPair.generateKeyPair(seed);

        int[] path = {44 | Bip32ECKeyPair.HARDENED_BIT, 60 | Bip32ECKeyPair.HARDENED_BIT, Bip32ECKeyPair.HARDENED_BIT, 0, indexAddress};
        Bip32ECKeyPair derivedKeyPair = Bip32ECKeyPair.deriveKeyPair(masterKeyPair, path);

        return Credentials.create(derivedKeyPair);
    }

    public static Credentials loadCredentialByECPrivateKey(
            String privateKey
    ) throws Exception {
        byte[] pkBytes = Numeric.hexStringToByteArray(privateKey);
        ECKeyPair ecKeyPair = Bip32ECKeyPair.create(pkBytes);
        return Credentials.create(ecKeyPair);
    }

    public static BigInteger getNonce(Web3j web3j, String address) throws IOException {

        return web3j
                .ethGetTransactionCount(address, DefaultBlockParameterName.LATEST)
                .send()
                .getTransactionCount();
    }
}
