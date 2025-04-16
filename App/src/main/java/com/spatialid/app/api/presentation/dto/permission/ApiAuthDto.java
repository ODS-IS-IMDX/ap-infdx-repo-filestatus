// © 2025 NTT DATA Japan Co., Ltd. & NTT InfraNet All Rights Reserved.

package com.spatialid.app.api.presentation.dto.permission;

import java.util.List;

import lombok.Data;

/**
 * API利用権限情報 GetApiAuthResponseDtoのフィールドの一つ。
 * 
 * @author ukai jun
 * @version 1.1 2024/08/29
 */
@Data
public class ApiAuthDto {

    /** 利用者グループID */
    private String servicerGroupId;

    /** API情報リスト */
    private List<ApiInfoDto> apiInfoList;

    /** 利用者システムID */
    private List<String> servicerIdList;

}
