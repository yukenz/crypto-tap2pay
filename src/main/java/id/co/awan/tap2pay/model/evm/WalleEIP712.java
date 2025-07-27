package id.co.awan.tap2pay.model.evm;

import io.reactivex.Flowable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.CustomError;
import org.web3j.abi.datatypes.DynamicArray;
import org.web3j.abi.datatypes.DynamicBytes;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Bytes1;
import org.web3j.abi.datatypes.generated.Bytes32;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.abi.datatypes.generated.Uint8;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.RemoteFunctionCall;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.BaseEventResponse;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tuples.generated.Tuple7;
import org.web3j.tx.Contract;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.ContractGasProvider;

/**
 * <p>Auto generated code.
 * <p><strong>Do not modify!</strong>
 * <p>Please use the <a href="https://docs.web3j.io/command_line.html">web3j command line tools</a>,
 * or the org.web3j.codegen.SolidityFunctionWrapperGenerator in the 
 * <a href="https://github.com/LFDT-web3j/web3j/tree/main/codegen">codegen module</a> to update.
 *
 * <p>Generated with web3j version 1.7.0.
 */
@SuppressWarnings("rawtypes")
public class WalleEIP712 extends Contract {
    public static final String BINARY = "Bin file was not provided";

    public static final String FUNC_EIP712DOMAIN = "eip712Domain";

    public static final String FUNC_GETSIGNERCARDREQUESTPAYMENT = "getSignerCardRequestPayment";

    public static final String FUNC_GETSIGNERCARDSELFSERVICE = "getSignerCardSelfService";

    public static final Event EIP712DOMAINCHANGED_EVENT = new Event("EIP712DomainChanged", 
            Arrays.<TypeReference<?>>asList());
    ;

    public static final CustomError INVALIDSHORTSTRING_ERROR = new CustomError("InvalidShortString", 
            Arrays.<TypeReference<?>>asList());
    ;

    public static final CustomError STRINGTOOLONG_ERROR = new CustomError("StringTooLong", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
    ;

    @Deprecated
    protected WalleEIP712(String contractAddress, Web3j web3j, Credentials credentials,
                          BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected WalleEIP712(String contractAddress, Web3j web3j, Credentials credentials,
                          ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, credentials, contractGasProvider);
    }

    @Deprecated
    protected WalleEIP712(String contractAddress, Web3j web3j,
                          TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    protected WalleEIP712(String contractAddress, Web3j web3j,
                          TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public RemoteFunctionCall<Tuple7<Bytes1, Utf8String, Utf8String, Uint256, Address, Bytes32, DynamicArray<Uint256>>> eip712Domain(
            ) {
        final Function function = new Function(FUNC_EIP712DOMAIN, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bytes1>() {}, new TypeReference<Utf8String>() {}, new TypeReference<Utf8String>() {}, new TypeReference<Uint256>() {}, new TypeReference<Address>() {}, new TypeReference<Bytes32>() {}, new TypeReference<DynamicArray<Uint256>>() {}));
        return new RemoteFunctionCall<Tuple7<Bytes1, Utf8String, Utf8String, Uint256, Address, Bytes32, DynamicArray<Uint256>>>(function,
                new Callable<Tuple7<Bytes1, Utf8String, Utf8String, Uint256, Address, Bytes32, DynamicArray<Uint256>>>() {
                    @Override
                    public Tuple7<Bytes1, Utf8String, Utf8String, Uint256, Address, Bytes32, DynamicArray<Uint256>> call(
                            ) throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);
                        return new Tuple7<Bytes1, Utf8String, Utf8String, Uint256, Address, Bytes32, DynamicArray<Uint256>>(
                                (Bytes1) results.get(0), 
                                (Utf8String) results.get(1), 
                                (Utf8String) results.get(2), 
                                (Uint256) results.get(3), 
                                (Address) results.get(4), 
                                (Bytes32) results.get(5), 
                                (DynamicArray<Uint256>) results.get(6));
                    }
                });
    }

    public RemoteFunctionCall<Address> getSignerCardRequestPayment(Bytes32 hashCard,
            Bytes32 hashPin, Utf8String merchantId, Utf8String merchantKey, Utf8String terminalId,
            Utf8String terminalKey, Uint256 paymentAmount, DynamicBytes signature) {
        final Function function = new Function(FUNC_GETSIGNERCARDREQUESTPAYMENT, 
                Arrays.<Type>asList(hashCard, hashPin, merchantId, merchantKey, terminalId, terminalKey, paymentAmount, signature), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function);
    }

    public RemoteFunctionCall<Address> getSignerCardSelfService(Uint8 operation, Bytes32 hashCard,
            Bytes32 hashPin, DynamicBytes signature) {
        final Function function = new Function(FUNC_GETSIGNERCARDSELFSERVICE, 
                Arrays.<Type>asList(operation, hashCard, hashPin, signature), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function);
    }

    public static List<EIP712DomainChangedEventResponse> getEIP712DomainChangedEvents(
            TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = staticExtractEventParametersWithLog(EIP712DOMAINCHANGED_EVENT, transactionReceipt);
        ArrayList<EIP712DomainChangedEventResponse> responses = new ArrayList<EIP712DomainChangedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            EIP712DomainChangedEventResponse typedResponse = new EIP712DomainChangedEventResponse();
            typedResponse.log = eventValues.getLog();
            responses.add(typedResponse);
        }
        return responses;
    }

    public static EIP712DomainChangedEventResponse getEIP712DomainChangedEventFromLog(Log log) {
        Contract.EventValuesWithLog eventValues = staticExtractEventParametersWithLog(EIP712DOMAINCHANGED_EVENT, log);
        EIP712DomainChangedEventResponse typedResponse = new EIP712DomainChangedEventResponse();
        typedResponse.log = log;
        return typedResponse;
    }

    public Flowable<EIP712DomainChangedEventResponse> eIP712DomainChangedEventFlowable(
            EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(log -> getEIP712DomainChangedEventFromLog(log));
    }

    public Flowable<EIP712DomainChangedEventResponse> eIP712DomainChangedEventFlowable(
            DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(EIP712DOMAINCHANGED_EVENT));
        return eIP712DomainChangedEventFlowable(filter);
    }

    @Deprecated
    public static WalleEIP712 load(String contractAddress, Web3j web3j, Credentials credentials,
                                   BigInteger gasPrice, BigInteger gasLimit) {
        return new WalleEIP712(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    @Deprecated
    public static WalleEIP712 load(String contractAddress, Web3j web3j,
                                   TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new WalleEIP712(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static WalleEIP712 load(String contractAddress, Web3j web3j, Credentials credentials,
                                   ContractGasProvider contractGasProvider) {
        return new WalleEIP712(contractAddress, web3j, credentials, contractGasProvider);
    }

    public static WalleEIP712 load(String contractAddress, Web3j web3j,
                                   TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return new WalleEIP712(contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static class EIP712DomainChangedEventResponse extends BaseEventResponse {
    }
}
