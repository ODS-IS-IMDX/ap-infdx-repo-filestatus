// © 2025 NTT DATA Japan Co., Ltd. & NTT InfraNet All Rights Reserved.

package com.spatialid.app.common.config;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.web.client.RestClientBuilderConfigurer;
import org.springframework.boot.web.client.ClientHttpRequestFactories;
import org.springframework.boot.web.client.ClientHttpRequestFactorySettings;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.spatialid.app.common.aws.AwsSecretManager;
import com.spatialid.app.common.property.RestClientProperty;
import com.spatialid.app.common.property.secrets.RestClientSecretsProperty;

@Configuration
public class RestClientConfig {

    /**
     * 機密ではないRestClientの設定を保持するクラス．
     */
    private final RestClientProperty restClientProperty;

    /**
     * RestClient関連のシークレット情報を保持するクラス．
     */
    private final RestClientSecretsProperty restClientSecretsProperty;

    public RestClientConfig(RestClientSecretsProperty restClientSecretsProperty,
            RestClientProperty restClientProperty) {

        this.restClientSecretsProperty = restClientSecretsProperty;
        this.restClientProperty = restClientProperty;

    }

    @Bean
    public RestClient.Builder customRestClient(RestClientBuilderConfigurer configurer) {

        ClientHttpRequestFactorySettings setting = ClientHttpRequestFactorySettings.DEFAULTS
                .withConnectTimeout(Duration.ofSeconds(5)).withReadTimeout(Duration.ofSeconds(5));

        RestClient.Builder builder = RestClient.builder().defaultStatusHandler(status -> true, (request, response) -> {
        }).requestFactory(ClientHttpRequestFactories.get(setting));

        configurer.configure(builder);

        return builder;

    }

    @Bean
    public RestClient authorityApiClient(RestClient.Builder restClientBuilder) throws JsonProcessingException {

        StringBuilder uri = new StringBuilder();

        uri.append(restClientProperty.getProtocol()).append("://")
                .append(restClientSecretsProperty.getAuthorityDomain()).append(":")
                .append(restClientProperty.getPort());

        RestClient restClient = restClientBuilder.baseUrl(uri.toString()).build();

        return restClient;

    }

    /**
     * アクセス履歴APIの呼び出しについて基本的な設定を行う．
     * <p>
     * アクセス履歴APIの基本URLを設定する．<br>
     * アクセス履歴APIはSSL通信を行うため、プロトコル/ポートをその他APIとは別に設定する．
     * </p>
     * 
     * @param restClientBuilder
     * @return アクセス履歴の設定を保持したRestClient
     * @throws JsonProcessingException
     */
    @Bean
    RestClient accessHistoryApiClient(RestClient.Builder restClientBuilder) throws JsonProcessingException {

        final StringBuilder uri = new StringBuilder();

        uri.append(restClientProperty.getSslProtocol()).append("://")
                .append(restClientSecretsProperty.getAccessHistoryDomain()).append(":")
                .append(restClientProperty.getSslPort());

        return restClientBuilder.baseUrl(uri.toString()).build();

    }

}