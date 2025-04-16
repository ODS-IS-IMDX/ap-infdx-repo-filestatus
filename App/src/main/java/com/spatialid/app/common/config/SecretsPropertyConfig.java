// © 2025 NTT DATA Japan Co., Ltd. & NTT InfraNet All Rights Reserved.

package com.spatialid.app.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.spatialid.app.common.aws.AwsSecretManager;
import com.spatialid.app.common.property.secrets.FileStatusSecretsProperty;
import com.spatialid.app.common.property.secrets.RestClientSecretsProperty;

/**
 * シークレット情報を保持するオブジェクトをBean登録する設定クラス．
 * 
 * @author matsumoto kentaro
 * @version 1.1 2024/10/18
 */
@Configuration
public class SecretsPropertyConfig {

    private final String secretName;

    public SecretsPropertyConfig(@Value("${cloud.aws.secretmanager.secretname}") String secretName) {

        this.secretName = secretName;

    }

    @Bean
    public FileStatusSecretsProperty fileStatusSecretsProperty() throws Exception {

        final FileStatusSecretsProperty fileStatusSecretsProperty = AwsSecretManager.getSecretsValue(secretName,
                FileStatusSecretsProperty.class);

        return fileStatusSecretsProperty;

    }

    @Bean
    public RestClientSecretsProperty restClientSecretsProperty() throws Exception {

        final RestClientSecretsProperty restClientSecretsProperty = AwsSecretManager.getSecretsValue(secretName,
                RestClientSecretsProperty.class);

        return restClientSecretsProperty;

    }

}
