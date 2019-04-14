package com.bsep.WindowsAgent.config;

import com.bsep.WindowsAgent.service.interfaces.IApplicationPropertiesService;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.util.ResourceUtils;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyStore;

@Configuration
public class RestTemplateConfig {

    private IApplicationPropertiesService _applicationPropertiesService;

    @Autowired
    public RestTemplateConfig(IApplicationPropertiesService applicationPropertiesService) {
        _applicationPropertiesService = applicationPropertiesService;
    }

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) throws Exception {
        String keyStorePath = _applicationPropertiesService.getKeyStorePath();
        char[] keyStorePassword = _applicationPropertiesService.getKeyStorePassword().toCharArray();
        String trustStorePath = _applicationPropertiesService.getTrustStorePath();
        char[] trustStorePassword = _applicationPropertiesService.getTrustStorePassword().toCharArray();

        SSLContext sslContext = SSLContextBuilder.create()
                .loadKeyMaterial(getStore(keyStorePath, keyStorePassword), keyStorePassword)
                .loadTrustMaterial(ResourceUtils.getFile(trustStorePath), trustStorePassword).build();

        HttpClient client = HttpClients.custom().setSSLContext(sslContext).build();

        return builder
                .requestFactory(() -> new HttpComponentsClientHttpRequestFactory(client))
                .build();
    }

    private KeyStore getStore(String file, char[] password) throws Exception {
        KeyStore keyStore = KeyStore.getInstance("JKS");
        File key = ResourceUtils.getFile(file);
        try (InputStream in = new FileInputStream(key)) {
            keyStore.load(in, password);
        }
        return keyStore;
    }


}