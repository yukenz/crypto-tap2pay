package id.co.awan.tap2pay;

import id.co.awan.tap2pay.service.WalleEIP712Service;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.DynamicBytes;
import org.web3j.abi.datatypes.generated.Bytes32;
import org.web3j.abi.datatypes.generated.Uint8;
import org.web3j.utils.Numeric;

@ExtendWith({SpringExtension.class})
@ContextConfiguration(classes = WalleEIP712Service.class)
@TestPropertySource(properties = {
        "web3j.rpc-url=http://localhost:8545/",
        "web3j.contract.walle-eip712=0xe7f1725E7734CE288F8367e1Bb143E90bb3F0512",
})
class WalleEIP712Tests {

    @Autowired
    WalleEIP712Service walleEIP712Service;


    @Test
    void testGetSignerCardSelfService() throws Exception {

//        Bytes32 hashCard = new Bytes32(Hash.sha3("94a57839-b7ef-4ff7-a1d6-54d37315a635".getBytes()));
//        Bytes32 hashPin = new Bytes32(Hash.sha3("1234".getBytes()));

        Bytes32 hashCard = new Bytes32(Numeric.hexStringToByteArray("0xa86fd2fba383be6bb4b450c9001ea7444f651c42333174e2935b62443c182c38"));
        Bytes32 hashPin = new Bytes32(Numeric.hexStringToByteArray("0x387a8233c96e1fc0ad5e284353276177af2186e7afa85296f106336e376669f7"));

        DynamicBytes signature = new DynamicBytes(Numeric.hexStringToByteArray("0x120823085a2bcecfe1852faad1ffd6951bbf20aaa03e60d213526689df3cc6e76f1694f49b1c550b58f4e5b6d5554aec4d48d7df18648df62259674fb0bbd8e91c"));

        System.out.println(Numeric.toHexString(hashCard.getValue()));
        System.out.println(Numeric.toHexString(hashPin.getValue()));
        System.out.println(Numeric.toHexString(signature.getValue()));

        Address accountBalance = walleEIP712Service.getSignerCardSelfService(
                Uint8.DEFAULT,
                hashCard,
                hashPin,
                signature
        );

        System.out.println(accountBalance.getValue());

    }


}
