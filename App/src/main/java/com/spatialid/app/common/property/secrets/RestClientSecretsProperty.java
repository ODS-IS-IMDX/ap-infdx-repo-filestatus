// © 2025 NTT DATA Japan Co., Ltd. & NTT InfraNet All Rights Reserved.

package com.spatialid.app.common.property.secrets;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.spatialid.app.common.aws.BaseSecretsValue;
import com.spatialid.app.common.constants.SecretsKeyConstants;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

/**
 * RestClientのシークレット情報を保持するクラス．
 * 
 * @author matsumoto kentaro
 * @version 1.1 2024/10/18
 */
@Value
@EqualsAndHashCode(callSuper = true)
@Builder
@Jacksonized
public class RestClientSecretsProperty extends BaseSecretsValue {

    /**
     * API利用権限コンテナのドメイン．
     */
    @JsonProperty(SecretsKeyConstants.AUTHORITY_DOMAIN)
    private final String authorityDomain;

    /**
     * アクセス履歴コンテナのドメイン．
     */
    @JsonProperty(SecretsKeyConstants.AC_HISTORY_DOMAIN)
    private final String accessHistoryDomain;

}