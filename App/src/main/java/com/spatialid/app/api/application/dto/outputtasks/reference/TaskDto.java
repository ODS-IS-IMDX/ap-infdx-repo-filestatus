// © 2025 NTT DATA Japan Co., Ltd. & NTT InfraNet All Rights Reserved.

package com.spatialid.app.api.application.dto.outputtasks.reference;

import com.fasterxml.jackson.databind.JsonNode;

import lombok.Data;

/**
 * GetOutputTasksResponseDtoのフィールドの一つ.
 * 
 * @author ukai jun
 * @version 1.1 2024/08/23
 */
@Data
public class TaskDto {

	/** タスクID */
	private String taskId;

	/** タスク作成日時 */
	private String taskCreateDate;

	/** タスク実行日時 */
	private String taskStartDate;

	/** タスク状況 */
	private String taskStatus;

	/** 処理区分 */
	private String processClass;

	/** 利用者システムID */
	private String servicerId;

	/** ファイル格納先URL */
	private String fileUrl;

	/** リクエスト内容 */
	private JsonNode request;

	/** エラー内容 */
	private String errorDetail;

	/** 更新日時 */
	private String updateTime;
}