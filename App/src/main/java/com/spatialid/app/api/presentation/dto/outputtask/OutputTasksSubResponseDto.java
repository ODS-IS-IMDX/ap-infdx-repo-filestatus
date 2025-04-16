// © 2025 NTT DATA Japan Co., Ltd. & NTT InfraNet All Rights Reserved.

package com.spatialid.app.api.presentation.dto.outputtask;

import lombok.Data;

/**
 *  ファイル作成状況確認にてクライアント側に渡す情報を扱うResponse。
 * 
 * @author kawashima naoya
 * @version 1.1 2024/08/23
 */
@Data
public class OutputTasksSubResponseDto {

	/** タスク状況 */
	private String taskStatus;
	
	/** ファイル格納先 */
	private String filePath;
	
	/** エラー内容 */
	private String error;
	
}