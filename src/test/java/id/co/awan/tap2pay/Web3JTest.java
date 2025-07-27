package id.co.awan.tap2pay;

import id.co.awan.tap2pay.repository.ERC20Repository;
import id.co.awan.tap2pay.service.ERC20Service;
import id.co.awan.tap2pay.service.EthereumService;
import id.co.awan.tap2pay.utils.HDWalletUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.RemoteFunctionCall;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Convert;

import java.math.BigDecimal;
import java.math.BigInteger;

@Slf4j
@ExtendWith({SpringExtension.class})
@ContextConfiguration(classes = {EthereumService.class, ERC20Service.class})
@TestPropertySource(properties = {
        "web3j.rpc-url=http://localhost:8545/",
        "web3j.contract.m-idrx=0x5FbDB2315678afecb367f032d93F642f64180aa3",
        "web3j.gas-limit=100000000000000",
        "web3j.max-fee-per-gas=50000000000",
        "web3j.master-key-wallet=0xac0974bec39a17e36ba4a6b4d238ff944bacb478cbed5efcae784d7bf4f2ff80"
})
class Web3JTest {


    @Autowired
    EthereumService ethereumService;

    @Autowired
    ERC20Service erc20Service;

    @Test
    void testGasPriceService() throws Exception {

//        Credentials cardCredential = HDWalletUtils.loadCredentialByECPrivateKey("0x59c6995e998f97a5a0044966f0945389dc9e86dae88c7a8412f4603b6b78690d");

        BigInteger allowance = erc20Service.getAllowance("0xf39Fd6e51aad88F6F4ce6aB8827279cffFb92266", "0x70997970C51812dc3A010C7d01b50e0d17dc79C8");

        BigInteger totalGasPrice = erc20Service.calculateTransferFromTotalSafeGasPrice(
                "0x70997970C51812dc3A010C7d01b50e0d17dc79C8",
                "0xf39Fd6e51aad88F6F4ce6aB8827279cffFb92266",
                "0x3C44CdDdB6a900fa2b585dd299e03d12FA4293BC",
                allowance,
                100L
        );

        BigDecimal totalGasPriceInEther = Convert.fromWei(new BigDecimal(totalGasPrice), Convert.Unit.ETHER);

        System.out.println("totalGasPrice: " + totalGasPrice);
        System.out.println("totalGasPriceInEther: " + totalGasPriceInEther);
    }


    @Test
    void testGasPrice() throws Exception {

        Credentials cardCredential = HDWalletUtils.loadCredentialByECPrivateKey("0x59c6995e998f97a5a0044966f0945389dc9e86dae88c7a8412f4603b6b78690d");

        String rpcUrl = "http://localhost:8545/";
        String erc20Address = "0x5FbDB2315678afecb367f032d93F642f64180aa3";
        try (
                Web3j web3j = Web3j.build(new HttpService(rpcUrl));
                ERC20Repository instance = ERC20Repository.getTransactionalInstance(rpcUrl, erc20Address, cardCredential);

        ) {

            RemoteFunctionCall<TransactionReceipt> transferFromRfc = instance.transferFrom(
                    new Address("0xf39Fd6e51aad88F6F4ce6aB8827279cffFb92266"),
                    new Address("0x3C44CdDdB6a900fa2b585dd299e03d12FA4293BC"),
                    new Uint256(10_000L)
            );

            BigInteger gasPrice = web3j.ethGasPrice().send().getGasPrice();
            BigInteger nonce = HDWalletUtils.getNonce(web3j, cardCredential.getAddress());

            Transaction transaction = new Transaction(
                    cardCredential.getAddress(),
                    nonce,
                    gasPrice,
                    new BigInteger("1000000000000"),
                    erc20Address,
                    BigInteger.ZERO,
                    transferFromRfc.encodeFunctionCall()
            );


            BigInteger totalGasUsed = web3j.ethEstimateGas(transaction)
                    .send()
                    .getAmountUsed();

            BigInteger totalGasPrice = totalGasUsed.multiply(gasPrice);
            BigDecimal totalGasPriceInEther = Convert.fromWei(new BigDecimal(totalGasPrice), Convert.Unit.ETHER);

            System.out.println("totalGasUsed: " + totalGasUsed);
            System.out.println("totalGasPrice: " + totalGasPrice);
            System.out.println("totalGasPriceInEther: " + totalGasPriceInEther);


        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    @Test
    void testTransferEtherEIP1559() throws Exception {

        BigInteger bigInteger = ethereumService.balanceOfMasterKeyWallet();
        System.out.println("balanceOfMasterKeyWallet: " + bigInteger);

        TransactionReceipt transactionReceipt = ethereumService.transferEtherEIP1559("0xDB01dbB625e36405dBf2890204CCc3411b5B3281", BigDecimal.ONE, Convert.Unit.ETHER);
        System.out.println(transactionReceipt);
        System.out.println(transactionReceipt.getTransactionHash());

    }

}
