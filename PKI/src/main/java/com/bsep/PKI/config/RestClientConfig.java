package com.bsep.PKI.config;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.*;

import org.apache.http.client.HttpClient;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.SSLContexts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;


@Configuration
public class RestClientConfig {
    /*
    @Value("${server.ssl.key-store}")
    private Resource keyStore;

    @Value("${server.ssl.key-store-password}")
    private char[] keyStorePassword;

    @Value("${server.ssl.trust-store}")
    private Resource trustStore;

    @Value("${server.ssl.trust-store-password}")
    private char[] trustStorePassword;

    @Value("${max-pool-size}")
    private Integer maxPoolSize;


    @Bean
    public ClientHttpRequestFactory httpRequestFactory() {
        return new HttpComponentsClientHttpRequestFactory(httpClient());
    }

    @Bean
    public HttpClient httpClient() {

        // Trust own CA and all child certs
        Registry<ConnectionSocketFactory> socketFactoryRegistry = null;
        try {
            SSLContext sslContext = SSLContexts.custom()
                .loadKeyMaterial(keyStore.getFile(), keyStorePassword,
                                                        keyStorePassword)
                .loadTrustMaterial(trustStore.getFile(), trustStorePassword)
                .build();

            // Since only our own certs are trusted, hostname verification is probably safe to bypass
            SSLConnectionSocketFactory sslSocketFactory = new SSLConnectionSocketFactory(sslContext,
                    new HostnameVerifier() {

                        @Override
                        public boolean verify(final String hostname,
                                              final SSLSession session) {
                            return true;
                        }
                    });

            socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                    .register("http", PlainConnectionSocketFactory.getSocketFactory())
                    .register("https", sslSocketFactory)
                    .build();

        } catch (Exception e) {
            //TODO: handle exceptions
            e.printStackTrace();
        }

        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
        connectionManager.setMaxTotal(maxPoolSize);
        // This client is for internal connections so only one route is expected
        connectionManager.setDefaultMaxPerRoute(maxPoolSize);
        return HttpClientBuilder.create()
                                .setConnectionManager(connectionManager)
                                .disableCookieManagement()
                                .disableAuthCaching()
                                .build();
    }

    @Bean
    public RestTemplate getRestTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setRequestFactory(httpRequestFactory());
        return restTemplate;
    }*/

    @Bean
    public RestTemplate getRestTemplate() {
        return new RestTemplate();
    }
}
