// © 2025 NTT DATA Japan Co., Ltd. & NTT InfraNet All Rights Reserved.

package com.spatialid.app.api.presentation.dto.outputtask;

import lombok.Data;

/**
 * 外部APIにてクライアント側に渡す情報を扱うResponse。
 * 
 * @author kawashima naoya
 * @version 1.1 2024/08/23
 */
@Data
public class OutputTasksResponseDto {

    /** dataModelType */
    private String dataModelType;

    /** attribute */
    private OutputTasksSubResponseDto attribute;

}