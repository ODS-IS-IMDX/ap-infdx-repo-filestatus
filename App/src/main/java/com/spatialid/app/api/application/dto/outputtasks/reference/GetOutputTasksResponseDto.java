// © 2025 NTT DATA Japan Co., Ltd. & NTT InfraNet All Rights Reserved.

package com.spatialid.app.api.application.dto.outputtasks.reference;

import java.util.List;

import lombok.Data;

/**
 * 設備データ出力タスク参照APIにてクライアント側に渡す情報を扱うResponse.
 * 
 * @author ukai jun
 * @version 1.1 2024/08/23
 */
@Data
public class GetOutputTasksResponseDto {
    public GetOutputTasksResponseDto() {
    }

    /** タスク情報リスト */
    private List<TaskDto> taskList;

    /**
     * GetOutputTasksResponseDtoクラスのコンストラクタ.
     * 
     * @param dataList
     */
    public GetOutputTasksResponseDto(List<TaskDto> dataList) {
        this.taskList = dataList;
    }

}