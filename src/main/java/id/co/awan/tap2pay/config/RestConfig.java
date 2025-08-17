package id.co.awan.tap2pay.config;

import org.apache.hc.client5.http.config.ConnectionConfig;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.BasicHttpClientConnectionManager;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.socket.ConnectionSocketFactory;
import org.apache.hc.client5.http.socket.PlainConnectionSocketFactory;
import org.apache.hc.client5.http.ssl.*;
import org.apache.hc.core5.http.config.Lookup;
import org.apache.hc.core5.http.config.RegistryBuilder;
import org.apache.hc.core5.pool.PoolConcurrencyPolicy;
import org.apache.hc.core5.ssl.SSLContexts;
import org.apache.hc.core5.ssl.TrustStrategy;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

@Configuration
public class RestConfig {

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder restTemplateBuilder) throws Exception {
        return apachePoolingRestTemplate(restTemplateBuilder);
    }

    public RestTemplate simpleRestTemplate(RestTemplateBuilder restTemplateBuilder) {

        RestTemplate restTemplate = restTemplateBuilder.build();

        // Set Factory
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(5000); // 5 seconds connection timeout
        factory.setReadTimeout(10000);   // 10 seconds read timeout

        restTemplate.setRequestFactory(factory);

        return restTemplate;
    }

    public RestTemplate apacheBasicRestTemplate(RestTemplateBuilder restTemplateBuilder) throws NoSuchAlgorithmException, KeyManagementException {

        Lookup<ConnectionSocketFactory> socketFactoryRegistry = getConnectionSocketFactoryLookup();
        BasicHttpClientConnectionManager connectionManager = new BasicHttpClientConnectionManager(socketFactoryRegistry);

        ConnectionConfig connectionConfig = getConnectionConfig();
        connectionManager.setConnectionConfig(connectionConfig);

        CloseableHttpClient httpClient = HttpClients.custom()
                .disableCookieManagement()
                .setConnectionManager(connectionManager)
                .useSystemProperties()
                .setDefaultRequestConfig(RequestConfig.custom()
                        // LEASE TIMEOUT
                        .setConnectionRequestTimeout(5L, TimeUnit.SECONDS)
                        .build())
                .build();

        RestTemplate restTemplate = restTemplateBuilder.build();
        restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(httpClient));
        return restTemplate;

    }

    public RestTemplate apachePoolingRestTemplate(RestTemplateBuilder restTemplateBuilder) throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {

        PoolingHttpClientConnectionManager connectionManager = PoolingHttpClientConnectionManagerBuilder.create()
                .useSystemProperties()
                .setDefaultConnectionConfig(getConnectionConfig())
                .setMaxConnTotal(4)
                .setMaxConnPerRoute(2)
//                .setSSLSocketFactory(getSSLConnectionSocketFactory()) //Deprecated
                .setTlsSocketStrategy(getTLSSocketStrategy())
                .setPoolConcurrencyPolicy(PoolConcurrencyPolicy.LAX)
                .build();

        CloseableHttpClient httpClient = HttpClients.custom()
                .disableCookieManagement()
                .setConnectionManager(connectionManager)
                .useSystemProperties()
                .setDefaultRequestConfig(RequestConfig.custom()
                        // LEASE TIMEOUT
                        .setConnectionRequestTimeout(5L, TimeUnit.SECONDS)
                        .build())
                .build();


        RestTemplate restTemplate = restTemplateBuilder.build();
        restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(httpClient));
        restTemplate.setErrorHandler(response -> true); // Bypass Any Error Exception

        return restTemplate;
    }

    public TlsSocketStrategy getTLSSocketStrategy() throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {

        TrustStrategy acceptingTrustStrategy = (X509Certificate[] chain, String authType) -> true;
        SSLContext sslContext = SSLContexts.custom()
                .loadTrustMaterial(null, acceptingTrustStrategy)
                .build();

        return new DefaultClientTlsStrategy(sslContext);
    }

    @Deprecated
    public SSLConnectionSocketFactory getSSLConnectionSocketFactory() throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {

        TrustStrategy acceptingTrustStrategy = (X509Certificate[] chain, String authType) -> true;
        SSLContext sslContext = SSLContexts.custom()
                .loadTrustMaterial(null, acceptingTrustStrategy)
                .build();

        return SSLConnectionSocketFactoryBuilder.create()
                .setHostnameVerifier((s, sslSession) -> true)
                .setSslContext(sslContext)
                .build();

    }

    @NotNull
    private ConnectionConfig getConnectionConfig() {
        return ConnectionConfig.custom()
                // Wait Full Established
                .setConnectTimeout(30L, TimeUnit.SECONDS)
                // Wait IO Operation / Send-Read Timeout
                .setSocketTimeout(30, TimeUnit.SECONDS)
                .build();
    }

    @NotNull
    private Lookup<ConnectionSocketFactory> getConnectionSocketFactoryLookup() throws NoSuchAlgorithmException, KeyManagementException {
        // Create a trust manager that accepts all certificates
        TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }

                    public void checkClientTrusted(X509Certificate[] certs, String authType) {
                    }

                    public void checkServerTrusted(X509Certificate[] certs, String authType) {
                    }
                }
        };

        // Create SSL context that uses the trust-all manager
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, trustAllCerts, new java.security.SecureRandom());

        // Create SSL socket factory with hostname verification disabled
        SSLConnectionSocketFactory sslSocketFactory = new SSLConnectionSocketFactory(
                sslContext,
                NoopHostnameVerifier.INSTANCE
        );

        // Register socket factories for HTTP and HTTPS
        return RegistryBuilder
                .<ConnectionSocketFactory>create()
                .register("http", PlainConnectionSocketFactory.getSocketFactory())
                .register("https", sslSocketFactory)
                .build();

    }
}