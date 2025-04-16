// © 2025 NTT DATA Japan Co., Ltd. & NTT InfraNet All Rights Reserved.
package com.spatialid.app.api.presentation.dto.permission;

import java.util.List;

import lombok.Data;

/**
 * API利用権限参照にてクライアント側に渡す情報を扱うResponse。
 * 
 * @author ukai jun
 * @version 1.1 2024/08/29
 */
@Data
public class GetApiAuthResponseDto {

    /** API利用権限情報リスト */
    private List<ApiAuthDto> apiAuthList;

}
