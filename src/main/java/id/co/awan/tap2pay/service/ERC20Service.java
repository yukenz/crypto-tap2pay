package id.co.awan.tap2pay.service;


import id.co.awan.tap2pay.repository.ERC20Repository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

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

}
