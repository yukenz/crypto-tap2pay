package id.co.awan.tap2pay.service;


import id.co.awan.tap2pay.repository.ERC20Repository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.web3j.abi.datatypes.Address;

import java.math.BigInteger;

@Service
public class ERC20Service {

    @Value("${web3j.rpc-url}")
    String rpcUrl;

    @Value("${web3j.contract.m-idrx}")
    String erc20Address;

    public BigInteger getTotalSupply() throws Exception {

        var instance = ERC20Repository
                .getInstance(rpcUrl, erc20Address);

        return instance.totalSupply()
                .send()
                .getValue();
    }

    public BigInteger getAccountBalance(
            String walletAddress
    ) throws Exception {

        var instance = ERC20Repository
                .getInstance(rpcUrl, erc20Address);

        return instance.balanceOf(new Address(walletAddress))
                .send()
                .getValue();
    }

}
