package id.co.awan.tap2pay.repository;

import id.co.awan.tap2pay.model.evm.WalleEIP712;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.ReadonlyTransactionManager;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.ContractGasProvider;
import org.web3j.tx.gas.DefaultGasProvider;

public class WalleEIP712Repository extends WalleEIP712 implements AutoCloseable {

    protected WalleEIP712Repository(String WalleEIP712Address, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        super(WalleEIP712Address, web3j, transactionManager, contractGasProvider);
    }

    @Override
    public void close() throws Exception {
        super.web3j.close();
    }

    public static WalleEIP712Repository getInstance(String rpcUrl, String contractAddress) {
        Web3j web3j = Web3j.build(new HttpService(rpcUrl));
        ReadonlyTransactionManager transactionManager = new ReadonlyTransactionManager(web3j, "0x0000000000000000000000000000000000000000");
        DefaultGasProvider contractGasProvider = new DefaultGasProvider();
        return new WalleEIP712Repository(contractAddress, web3j, transactionManager, contractGasProvider);
    }


}
