// © 2025 NTT DATA Japan Co., Ltd. & NTT InfraNet All Rights Reserved.

package com.spatialid.app.api.application.dto.outputtasks.reference;

import lombok.Data;

/**
 * 設備データ出力タスク参照APIにてクライアント側から受け取るrequest.
 * 
 * @author ukai jun
 * @version 1.1 2024/08/23
 */
@Data
public class GetOutputTasksRequestDto {

    /** タスクID */
    private String taskId;

    /** 処理区分 */
    private String processClass;

    /** 利用者システムID */
    private String servicerId;
}
