// © 2025 NTT DATA Japan Co., Ltd. & NTT InfraNet All Rights Reserved.

package com.spatialid.app.api.presentation.dto.outputtask;

import lombok.Data;

/**
 * ファイル作成状況確認APIにてクライアント側から受け取るrequest。
 * 
 * @author kawashima naoya
 * @version 1.1 2024/08/23
 */
@Data
public class OutputTaksRequestDto {

    /** タスクID */
    private String taskId;

}