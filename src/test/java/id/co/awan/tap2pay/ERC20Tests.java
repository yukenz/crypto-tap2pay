package id.co.awan.tap2pay;

import id.co.awan.tap2pay.service.ERC20Service;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigInteger;

@ExtendWith({SpringExtension.class})
@ContextConfiguration(classes = ERC20Service.class)
@TestPropertySource(properties = {
        "web3j.rpc-url=https://testnet-rpc.monad.xyz/",
        "web3j.contract.m-idrx=0x8c0829302B138B0f7ad44660548b02D2b98919f4",
})
class ERC20Tests {

    @Autowired
    ERC20Service erc20Service;

    @Test
    void testTotalSupply() throws Exception {

        BigInteger totalSupply = erc20Service.getTotalSupply();
        System.out.println(totalSupply);

    }

    @Test
    void getAccountBalance() throws Exception {

        BigInteger accountBalance = erc20Service.getAccountBalance("0xf39Fd6e51aad88F6F4ce6aB8827279cffFb92266");
        System.out.println(accountBalance);

    }


}
