// © 2025 NTT DATA Japan Co., Ltd. & NTT InfraNet All Rights Reserved.

package com.spatialid.app.api.application.service.task;

import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.spatialid.app.api.application.dto.outputtasks.reference.GetOutputTasksRequestDto;
import com.spatialid.app.api.application.dto.outputtasks.reference.GetOutputTasksResponseDto;

/**
 * ファイル作成状況確認serviceインターフェース。
 * 
 * @author Nishikawa Hayato
 * @version 1.1 2024/09/06
 */

public interface IOutputTasks {

    ResponseEntity<GetOutputTasksResponseDto> getTask(GetOutputTasksRequestDto request) throws JsonProcessingException;

}
