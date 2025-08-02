package id.co.awan.tap2pay.service;

import id.co.awan.tap2pay.utils.HDWalletUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.Transfer;
import org.web3j.utils.Convert;

import java.math.BigDecimal;
import java.math.BigInteger;

@Service
@Slf4j
public class EthereumService {

    @Value("${web3j.rpc-url}")
    private String rpcUrl;

    @Value("${web3j.master-key-wallet}")
    private String masterKeyWallet;


    /**
     * Get gas price from Chain
     */
    public BigInteger gasPrice() throws ResponseStatusException {
        try (Web3j web3 = Web3j.build(new HttpService(rpcUrl));) {
            return web3.ethGasPrice().send().getGasPrice();
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    /**
     * Get gas price from Chain
     */
    public void recoverCardGasAvailability(
            String cardAddress
    ) throws ResponseStatusException {

        BigInteger cardBalance = balanceOf(cardAddress);
        BigInteger gasFee = gasPrice().multiply(BigInteger.valueOf(70000L)); // Safe gas 70000L for transferFrom

        if (cardBalance.compareTo(gasFee) < 0) {
            BigInteger amountForRecover = gasFee.subtract(cardBalance);
            TransactionReceipt transactionReceipt = transferEtherEIP1559(cardAddress, new BigDecimal(amountForRecover), Convert.Unit.WEI);
            log.info("Gas fee recover from card adress: {} [{}]", cardAddress, transactionReceipt.getTransactionHash());
        }

    }

    /**
     * Get native balance for master address in {@code web3j.master-key-wallet}
     */
    public BigInteger balanceOfMasterKeyWallet() throws ResponseStatusException {
        try (Web3j web3 = Web3j.build(new HttpService(rpcUrl));) {
            Credentials credentials = HDWalletUtils.loadCredentialByECPrivateKey(masterKeyWallet);
            EthGetBalance send = web3.ethGetBalance(credentials.getAddress(), DefaultBlockParameterName.LATEST)
                    .send();
            return send.getBalance();
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    /**
     * Get native balance for ethereum address
     *
     * @param address EVM Address
     */
    public BigInteger balanceOf(String address) throws ResponseStatusException {
        try (Web3j web3 = Web3j.build(new HttpService(rpcUrl));) {
            EthGetBalance send = web3.ethGetBalance(address, DefaultBlockParameterName.LATEST)
                    .send();
            return send.getBalance();
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }


    /**
     * Native Ethereum Transfer using EIP1559
     *
     * @param toAddress EVM Address destination
     * @param amount    Amount for transfer
     * @param unit      Unit type for amount
     */
    public TransactionReceipt transferEtherEIP1559(
            String toAddress,
            BigDecimal amount,
            Convert.Unit unit
    ) throws ResponseStatusException {
        try (Web3j web3j = Web3j.build(new HttpService(rpcUrl));) {

            Credentials credentials = HDWalletUtils.loadCredentialByECPrivateKey(masterKeyWallet);

            // Maksimal fee yang kita mau keluarkan untuk bayar gas pada transaksi ini
            BigInteger maxTotalGasPrice = Convert.toWei("0.000003", Convert.Unit.ETHER)  // Maksimum fee yang dibayar, 0.0001 ETH
                    .toBigInteger();

            BigInteger currentBalance = web3j.ethGetBalance(credentials.getAddress(), DefaultBlockParameterName.LATEST)
                    .send().getBalance();

            // Harga per gas yang node inginkan
            BigInteger gasPrice = web3j.ethGasPrice().send().getGasPrice();
            BigInteger nonce = web3j
                    .ethGetTransactionCount(credentials.getAddress(), DefaultBlockParameterName.LATEST)
                    .send()
                    .getTransactionCount();
            org.web3j.protocol.core.methods.request.Transaction transaction = new Transaction(
                    credentials.getAddress(),
                    nonce,
                    gasPrice,
                    new BigInteger("1000000000000"), // Default Gas Limit From Anvil
                    toAddress,
                    Convert.toWei(amount, unit).toBigInteger(),
                    null
            );

            // Banyaknya unit gas yang diperlukan untuk transaksi
            BigInteger gasLimit = web3j.ethEstimateGas(transaction).send().getAmountUsed();
            BigInteger gasLimitWithBuffer = gasLimit
                    .multiply(BigInteger.valueOf(100L + 5L)) // tambah 5 persen dari total unit gas yang akan kita beli
                    .divide(new BigInteger("100"));

            // Harga per unit gas, berdasarkan maksimal fee yang kita mau keluarkan untuk bayar gas pada transaksi ini
            BigInteger wantedFeePerGas = maxTotalGasPrice.divide(gasLimit);
//                    .multiply(BigInteger.valueOf(100L + 5L)) // Beri buffer 5 persen
//                    .divide(new BigInteger("100"));

            BigInteger maxPriorityFeePerGas = wantedFeePerGas
                    .multiply(BigInteger.valueOf(1L)) // kasih tips 5 persen ke miner berdasarkan harga gas per unit
                    .divide(new BigInteger("100"));

            // Harga per gas = harga per unit yang kita inginkan - tips per unit ke miner
            BigInteger finalMaxFeePerGas = wantedFeePerGas.subtract(maxPriorityFeePerGas);

            // Mengirim transaksi menggunakan EIP-1559 dengan parameter yang sudah dihitung
            RemoteCall<TransactionReceipt> sendFundsEIP1559 = Transfer.sendFundsEIP1559(
                    web3j, credentials,
                    toAddress,
                    amount,
                    unit,
                    gasLimitWithBuffer,
                    maxPriorityFeePerGas,
                    finalMaxFeePerGas
            );

            TransactionReceipt send = sendFundsEIP1559.send();

            BigInteger lastBalance = web3j.ethGetBalance(credentials.getAddress(), DefaultBlockParameterName.LATEST)
                    .send().getBalance();

            System.out.println("Fee " + currentBalance.subtract(lastBalance));

            return send;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

}
