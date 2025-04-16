// © 2025 NTT DATA Japan Co., Ltd. & NTT InfraNet All Rights Reserved.

package com.spatialid.app.common.property.secrets;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.spatialid.app.common.aws.BaseSecretsValue;
import com.spatialid.app.common.constants.SecretsKeyConstants;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@EqualsAndHashCode(callSuper = true)
@Builder
@Jacksonized
public class FileStatusSecretsProperty extends BaseSecretsValue {

    @JsonProperty(SecretsKeyConstants.REGION)
    private final String Region;

    @JsonProperty(SecretsKeyConstants.BUCKET)
    private final String Bucket;

    @JsonProperty(SecretsKeyConstants.COMMON_DOMAIN)
    private final String Common;

    @JsonProperty(SecretsKeyConstants.AUTHORITY_DOMAIN)
    private final String Authority;

}
