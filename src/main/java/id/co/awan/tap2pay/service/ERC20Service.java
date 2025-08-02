package id.co.awan.tap2pay.service;

import id.co.awan.tap2pay.repository.ERC20Repository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.RemoteFunctionCall;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.gas.DefaultGasProvider;

import java.math.BigInteger;

@Service
public class ERC20Service {

    @Value("${web3j.rpc-url}")
    String rpcUrl;

    @Value("${web3j.contract.m-idrx}")
    String erc20Address;

    public BigInteger getTotalSupply() throws ResponseStatusException {


        try (ERC20Repository instance = ERC20Repository.getInstance(rpcUrl, erc20Address)) {
            return instance.totalSupply()
                    .send()
                    .getValue();
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    public BigInteger getAccountBalance(
            String walletAddress
    ) throws ResponseStatusException {

        try (ERC20Repository instance = ERC20Repository.getInstance(rpcUrl, erc20Address)) {

            return instance.balanceOf(new Address(walletAddress))
                    .send()
                    .getValue();
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    public BigInteger getAllowance(
            String walletAddress,
            String allowanceAddress
    ) throws ResponseStatusException {

        try (ERC20Repository instance = ERC20Repository.getInstance(rpcUrl, erc20Address)) {

            return instance.allowance(new Address(walletAddress), new Address(allowanceAddress))
                    .send()
                    .getValue();
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }


    /**
     * @throws ResponseStatusException akan error apabila ketika simulasi transaksi gagal, termasuk jika gas adalah kosong
     */
    public BigInteger calculateTransferFromTotalSafeGasPrice(
            String cardAddress,
            String ownerAddress,
            String merchantAddress,
            BigInteger amount,
            long percentTotalGasPriceAdditional
    ) throws Exception {

        BigInteger totalGasPrice = calculateTransferFromTotalGasPrice(cardAddress, ownerAddress, merchantAddress, amount);
        return totalGasPrice.multiply(BigInteger.valueOf(100L + percentTotalGasPriceAdditional))
                .divide(new BigInteger("100"));
    }

    /**
     * @throws ResponseStatusException akan error apabila ketika simulasi transaksi gagal, termasuk jika gas adalah kosong
     */
    public BigInteger calculateTransferFromTotalGasPrice(
            String cardAddress,
            String ownerAddress,
            String merchantAddress,
            BigInteger amount
    ) throws Exception {

        try (
                Web3j web3j = Web3j.build(new HttpService(rpcUrl));
                ERC20Repository instance = ERC20Repository.getInstance(rpcUrl, erc20Address)
        ) {

            RemoteFunctionCall<TransactionReceipt> transferFromRfc = instance.transferFrom(
                    new Address(ownerAddress),
                    new Address(merchantAddress),
                    new Uint256(amount)
            );

            BigInteger gasPrice = web3j.ethGasPrice().send().getGasPrice();
            BigInteger nonce = web3j
                    .ethGetTransactionCount(cardAddress, DefaultBlockParameterName.LATEST)
                    .send()
                    .getTransactionCount();

            Transaction transaction = new Transaction(
                    cardAddress,
                    nonce,
                    gasPrice,
                    DefaultGasProvider.GAS_LIMIT,
                    erc20Address,
                    BigInteger.ZERO,
                    transferFromRfc.encodeFunctionCall()
            );

            BigInteger totalGasUsed = web3j.ethEstimateGas(transaction)
                    .send()
                    .getAmountUsed();

            return totalGasUsed.multiply(gasPrice);

        } catch (Exception e) {
            throw e;
        }
    }

}
