package id.co.awan.tap2pay.utils;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;

import java.security.SecureRandom;

@Slf4j
public class LogUtils {

    private static final SecureRandom secureRandom = new SecureRandom();

    private static String generateLogToken() {
        return "REQ_" + System.currentTimeMillis() + "_" +
                secureRandom.nextInt(999999);
    }


    public static String logHttpRequest(Class<?> clazz, String methodName, JsonNode request) {
        String reqToken = generateLogToken();
        log.info("[HTTP-REQUEST {} - {}:{}] : {}",
                reqToken,
                clazz.getSimpleName(),
                methodName + "()",
                request.toPrettyString()
        );
        return String.join("|", reqToken, methodName);
    }

    public static void logHttpResponse(String reqTokenJoin, Class<?> clazz, JsonNode response) {
        String[] reqTokenTupple = reqTokenJoin.split("\\|");
        log.info("[HTTP-RESPONSE {} - {}:{}] : {}",
                reqTokenTupple[0],
                clazz.getSimpleName(),
                reqTokenTupple[1] + "()",
                response.toPrettyString()
        );
    }

    public static void logHttpResponseWithoutToken(String methodName, Class<?> clazz, JsonNode response) {
        log.info("[HTTP-RESPONSE - {}:{}] : {}",
                clazz.getSimpleName(),
                methodName + "()",
                response.toPrettyString()
        );
    }

}
