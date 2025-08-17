package id.co.awan.tap2pay.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigInteger;

@Service
@RequiredArgsConstructor
public class ERC20MiddlewareService {

    public enum ScOperation {
        SIMULATE,
        WRITE
    }

    @Value("${web3-mdw.host}")
    private String web3MiddlewareHost;

    @Value("${web3-mdw.authorization}")
    private String web3MiddlewareAuthorization;

    private final RestTemplate restTemplate;

    /*
     * =================================================================================================================
     * ORCHESTRATION
     * =================================================================================================================
     */

    @NotNull
    private ResponseEntity<JsonNode> executePostRest(final String erc20MiddlewarePath, final ObjectNode request) {

        final LinkedMultiValueMap<String, String> HEADERS = new LinkedMultiValueMap<>();
        HEADERS.add(HttpHeaders.CONTENT_TYPE, "application/json");
        HEADERS.add(HttpHeaders.ACCEPT, "application/json");
        HEADERS.add(HttpHeaders.AUTHORIZATION, web3MiddlewareAuthorization);

        return restTemplate.exchange(
                web3MiddlewareHost + erc20MiddlewarePath,
                HttpMethod.POST,
                new HttpEntity<>(request, HEADERS),
                JsonNode.class
        );
    }

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

        ResponseEntity<JsonNode> responseEntity = executePostRest("/api/web3/erc20/read/totalSupply", REQUEST);
        JsonNode responseJson = responseEntity.getBody();

        if (responseJson == null) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Response Body is Null");
        } else {
            String error = responseJson.at("/error").asText("01");
            String errorDetail = responseJson.at("/errorDetail").asText(null);
            String data = responseJson.at("/data").asText(null);

            if (data == null) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, error + "|" + errorDetail);
            } else {
                return new BigInteger(data);
            }
        }

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

        ResponseEntity<JsonNode> responseEntity = executePostRest("/api/web3/erc20/read/allowance", REQUEST);
        JsonNode responseJson = responseEntity.getBody();

        if (responseJson == null) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Response Body is Null");
        } else {
            String error = responseJson.at("/error").asText("01");
            String errorDetail = responseJson.at("/errorDetail").asText(null);
            String data = responseJson.at("/data").asText(null);

            if (data == null) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, error + "|" + errorDetail);
            } else {
                return new BigInteger(data);
            }
        }

    }


    /*
     * =================================================================================================================
     * TRANSACTION
     * =================================================================================================================
     */

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

        ResponseEntity<JsonNode> responseEntity = executePostRest(URL_PATH, REQUEST);
        JsonNode responseJson = responseEntity.getBody();

        if (responseJson == null) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Response Body is Null");
        } else {
            String error = responseJson.at("/error").asText("01");
            String errorDetail = responseJson.at("/errorDetail").asText(null);
            String trxReceipt = responseJson.at("/trxReceipt").asText(null);
            String estimateWei = responseJson.at("/estimateWei").asText(null);

            if (trxReceipt == null && estimateWei == null) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, error + "|" + errorDetail);
            } else {
                return switch (scOperation) {
                    case SIMULATE -> estimateWei;
                    case WRITE -> trxReceipt;
                };
            }
        }
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

        ResponseEntity<JsonNode> responseEntity = executePostRest(URL_PATH, REQUEST);
        JsonNode responseJson = responseEntity.getBody();

        if (responseJson == null) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Response Body is Null");
        } else {
            String error = responseJson.at("/error").asText("01");
            String errorDetail = responseJson.at("/errorDetail").asText(null);
            String trxReceipt = responseJson.at("/trxReceipt").asText(null);
            String estimateWei = responseJson.at("/estimateWei").asText(null);

            if (trxReceipt == null && estimateWei == null) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, error + "|" + errorDetail);
            } else {
                return switch (scOperation) {
                    case SIMULATE -> estimateWei;
                    case WRITE -> trxReceipt;
                };
            }
        }
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

        ResponseEntity<JsonNode> responseEntity = executePostRest(URL_PATH, REQUEST);
        JsonNode responseJson = responseEntity.getBody();

        if (responseJson == null) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Response Body is Null");
        } else {
            String error = responseJson.at("/error").asText("01");
            String errorDetail = responseJson.at("/errorDetail").asText(null);
            String trxReceipt = responseJson.at("/trxReceipt").asText(null);
            String estimateWei = responseJson.at("/estimateWei").asText(null);

            if (trxReceipt == null && estimateWei == null) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, error + "|" + errorDetail);
            } else {
                return switch (scOperation) {
                    case SIMULATE -> estimateWei;
                    case WRITE -> trxReceipt;
                };
            }
        }
    }


}
