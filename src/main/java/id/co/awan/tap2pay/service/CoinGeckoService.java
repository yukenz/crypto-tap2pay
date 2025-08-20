package id.co.awan.tap2pay.service;

import com.fasterxml.jackson.databind.JsonNode;
import id.co.awan.tap2pay.service.abstracts.CoinGeckoServiceAbstract;
import id.co.awan.tap2pay.utils.LogUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class CoinGeckoService extends CoinGeckoServiceAbstract {

    public CoinGeckoService(RestTemplate restTemplate) {
        super(restTemplate);
    }

    public JsonNode
    ping() {

        ResponseEntity<JsonNode> responseEntity = super.executeGetRest("/api/v3/ping");

        final JsonNode RESPONSE = responseEntity.getBody();
        Assert.notNull(RESPONSE, "Rest Template Response shouldn't be null");
        LogUtils.logHttpResponseFromGet("ping", this.getClass(), RESPONSE);

        return responseEntity.getBody();
    }

    public JsonNode
    coinPrice(String currency, String erc20Address, Integer precision) {

        final LinkedMultiValueMap<String, String> QUERY_PARAM = new LinkedMultiValueMap<>();
        QUERY_PARAM.add("contract_addresses", erc20Address);
        QUERY_PARAM.add("vs_currencies", currency);
        QUERY_PARAM.add("include_market_cap", "false");
        QUERY_PARAM.add("include_24hr_vol", "false");
        QUERY_PARAM.add("include_24hr_change", "false");
        QUERY_PARAM.add("include_last_updated_at", "false");
        QUERY_PARAM.add("precision", precision.toString());

        ResponseEntity<JsonNode> responseEntity = super.executeGetRest("/api/v3/simple/token_price/id", QUERY_PARAM);

        final JsonNode RESPONSE = responseEntity.getBody();
        Assert.notNull(RESPONSE, "Rest Template Response shouldn't be null");
        LogUtils.logHttpResponseFromGet("coinPrice", this.getClass(), RESPONSE);

        return responseEntity.getBody();
    }


}

