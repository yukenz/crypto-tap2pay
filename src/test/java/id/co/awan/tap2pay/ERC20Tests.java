package id.co.awan.tap2pay;

import id.co.awan.tap2pay.service.ERC20Service;
import id.co.awan.tap2pay.service.EthereumService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.web3j.utils.Convert;

import java.math.BigInteger;

@ExtendWith({SpringExtension.class})
@ContextConfiguration(classes = {ERC20Service.class, EthereumService.class})
@TestPropertySource(properties = {
        "web3j.rpc-url=http://localhost:8545",
        "web3j.contract.m-idrx=0x5FbDB2315678afecb367f032d93F642f64180aa3",
        "web3j.master-key-wallet=0xac0974bec39a17e36ba4a6b4d238ff944bacb478cbed5efcae784d7bf4f2ff80",
})
class ERC20Tests {

    @Autowired
    ERC20Service erc20Service;

    @Autowired
    EthereumService ethereumService;

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

    @Test
    void estimateGas() throws Exception {


        BigInteger cardBalance = ethereumService.balanceOf("0x87B2C1329acE83dA545B3f2703A24Cd4150F61cd");
        BigInteger gasFee = ethereumService.gasPrice().multiply(BigInteger.valueOf(70000L)); // Safe gas 70000L for transferFrom

        System.out.println(cardBalance);

        BigInteger transferFromGas = erc20Service.calculateTransferFromTotalGasPrice(
                "0x87B2C1329acE83dA545B3f2703A24Cd4150F61cd",
                "0xf39Fd6e51aad88F6F4ce6aB8827279cffFb92266",
                "0x70997970C51812dc3A010C7d01b50e0d17dc79C8",
                BigInteger.valueOf(100L)
        );

        System.out.println(transferFromGas);

    }


}
