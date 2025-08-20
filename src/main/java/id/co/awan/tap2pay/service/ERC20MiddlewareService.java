package id.co.awan.tap2pay.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import id.co.awan.tap2pay.service.abstracts.Web3MiddlewareServiceAbstract;
import id.co.awan.tap2pay.utils.LogUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigInteger;

@Service
public class ERC20MiddlewareService extends Web3MiddlewareServiceAbstract {

    public enum ScOperation {
        SIMULATE,
        WRITE
    }

    public ERC20MiddlewareService(RestTemplate restTemplate) {
        super(restTemplate);
    }

    @Value("${web3-mdw.master-key-wallet}")
    private String masterPrivateKey;

    /*
     * =================================================================================================================
     * INQUIRY
     * =================================================================================================================
     */

    public BigInteger totalSupply(
            String chain,
            String erc20Address
    ) throws ResponseStatusException {

        final ObjectNode REQUEST = JsonNodeFactory.instance.objectNode();
        REQUEST.put("chain", chain);
        REQUEST.put("erc20Address", erc20Address);

        String reqToken = LogUtils.logHttpRequest(this.getClass(), "totalSupply", REQUEST);
        ResponseEntity<JsonNode> responseEntity = executePostRest("/api/web3/erc20/read/totalSupply", REQUEST);
        JsonNode responseJson = super.parseResponseJsonNode(responseEntity);
        LogUtils.logHttpResponse(reqToken, this.getClass(), responseJson);

        String data = responseJson.at("/data").asText(null);
        return new BigInteger(data);

    }

    public BigInteger allowance(
            String chain,
            String erc20Address,
            String sourceAddress,
            String destinationAddress
    ) throws ResponseStatusException {

        final ObjectNode REQUEST = JsonNodeFactory.instance.objectNode();
        REQUEST.put("chain", chain);
        REQUEST.put("erc20Address", erc20Address);
        REQUEST.put("sourceAddress", sourceAddress);
        REQUEST.put("destinationAddress", destinationAddress);

        String reqToken = LogUtils.logHttpRequest(this.getClass(), "allowance", REQUEST);
        ResponseEntity<JsonNode> responseEntity = executePostRest("/api/web3/erc20/read/allowance", REQUEST);
        JsonNode responseJson = super.parseResponseJsonNode(responseEntity);
        LogUtils.logHttpResponse(reqToken, this.getClass(), responseJson);

        String data = responseJson.at("/data").asText(null);
        return new BigInteger(data);

    }

    /*
     * =================================================================================================================
     * TRANSACTION
     * =================================================================================================================
     */

    public String transfer(
            String chain,
            String erc20Address,
            String destinationAddress,
            String amount,
            ScOperation scOperation
    ) throws ResponseStatusException {
        return transfer(
                chain,
                masterPrivateKey,
                erc20Address,
                destinationAddress,
                amount,
                scOperation
        );
    }

    public String transfer(
            String chain,
            String privateKey,
            String erc20Address,
            String destinationAddress,
            String amount,
            ScOperation scOperation
    ) throws ResponseStatusException {

        final ObjectNode REQUEST = JsonNodeFactory.instance.objectNode();
        REQUEST.put("chain", chain);
        REQUEST.put("privateKey", privateKey);
        REQUEST.put("erc20Address", erc20Address);
        REQUEST.put("destinationAddress", destinationAddress);
        REQUEST.put("amount", amount);

        final String URL_PATH = switch (scOperation) {
            case SIMULATE -> "/api/web3/erc20/simulate/transfer";
            case WRITE -> "/api/web3/erc20/write/transfer";
        };

        String reqToken = LogUtils.logHttpRequest(this.getClass(), "transfer", REQUEST);
        ResponseEntity<JsonNode> responseEntity = executePostRest(URL_PATH, REQUEST);
        JsonNode responseJson = super.parseResponseJsonNode(responseEntity);
        LogUtils.logHttpResponse(reqToken, this.getClass(), responseJson);

        String trxReceipt = responseJson.at("/trxReceipt").asText(null);
        String estimateWei = responseJson.at("/estimateWei").asText(null);

        return switch (scOperation) {
            case SIMULATE -> estimateWei;
            case WRITE -> trxReceipt;
        };
    }


    public String transferFrom(
            String chain,
            String privateKey,
            String erc20Address,
            String sourceAddress,
            String destinationAddress,
            String amount,
            ScOperation scOperation
    ) throws ResponseStatusException {

        final ObjectNode REQUEST = JsonNodeFactory.instance.objectNode();
        REQUEST.put("chain", chain);
        REQUEST.put("privateKey", privateKey);
        REQUEST.put("erc20Address", erc20Address);
        REQUEST.put("sourceAddress", sourceAddress);
        REQUEST.put("destinationAddress", destinationAddress);
        REQUEST.put("amount", amount);

        final String URL_PATH = switch (scOperation) {
            case SIMULATE -> "/api/web3/erc20/simulate/transferFrom";
            case WRITE -> "/api/web3/erc20/write/transferFrom";
        };

        String reqToken = LogUtils.logHttpRequest(this.getClass(), "transferFrom", REQUEST);
        ResponseEntity<JsonNode> responseEntity = executePostRest(URL_PATH, REQUEST);
        JsonNode responseJson = super.parseResponseJsonNode(responseEntity);
        LogUtils.logHttpResponse(reqToken, this.getClass(), responseJson);

        String trxReceipt = responseJson.at("/trxReceipt").asText(null);
        String estimateWei = responseJson.at("/estimateWei").asText(null);


        return switch (scOperation) {
            case SIMULATE -> estimateWei;
            case WRITE -> trxReceipt;
        };
    }

    public String approve(
            String chain,
            String privateKey,
            String erc20Address,
            String destinationAddress,
            String amount,
            ScOperation scOperation
    ) throws ResponseStatusException {

        final ObjectNode REQUEST = JsonNodeFactory.instance.objectNode();
        REQUEST.put("chain", chain);
        REQUEST.put("privateKey", privateKey);
        REQUEST.put("erc20Address", erc20Address);
        REQUEST.put("destinationAddress", destinationAddress);
        REQUEST.put("amount", amount);

        final String URL_PATH = switch (scOperation) {
            case SIMULATE -> "/api/web3/erc20/simulate/approve";
            case WRITE -> "/api/web3/erc20/write/approve";
        };

        String reqToken = LogUtils.logHttpRequest(this.getClass(), "approve", REQUEST);
        ResponseEntity<JsonNode> responseEntity = executePostRest(URL_PATH, REQUEST);
        JsonNode responseJson = super.parseResponseJsonNode(responseEntity);
        LogUtils.logHttpResponse(reqToken, this.getClass(), responseJson);

        String trxReceipt = responseJson.at("/trxReceipt").asText(null);
        String estimateWei = responseJson.at("/estimateWei").asText(null);

        return switch (scOperation) {
            case SIMULATE -> estimateWei;
            case WRITE -> trxReceipt;
        };
    }

}
