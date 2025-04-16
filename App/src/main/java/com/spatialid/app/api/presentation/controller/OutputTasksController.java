// © 2025 NTT DATA Japan Co., Ltd. & NTT InfraNet All Rights Reserved.

package com.spatialid.app.api.presentation.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.spatialid.app.api.application.dto.outputtasks.reference.GetOutputTasksRequestDto;
import com.spatialid.app.api.application.dto.outputtasks.reference.GetOutputTasksResponseDto;
import com.spatialid.app.api.application.dto.outputtasks.reference.TaskDto;
import com.spatialid.app.api.application.service.task.IOutputTasks;
import com.spatialid.app.api.constants.OutputTaskConstants;
import com.spatialid.app.api.presentation.dto.outputtask.OutputTasksResponseDto;
import com.spatialid.app.api.presentation.dto.outputtask.OutputTasksSubResponseDto;
import com.spatialid.app.common.aws.AwsS3Manager;
import com.spatialid.app.common.constant.RestApiConstants;
import com.spatialid.app.common.exception.subexception.InternalApiCallingException;
import com.spatialid.app.common.exception.subexception.NotFoundException;
import com.spatialid.app.common.exception.subexception.ParamErrorException;
import com.spatialid.app.common.property.secrets.FileStatusSecretsProperty;

import jakarta.servlet.http.HttpServletRequest;

/**
 * ファイル作成状況確認APIを管理するControllerクラス。
 * 
 * @author kawashima naoya
 * @version 1.1 2024/08/23
 * 
 */
@RestController
public class OutputTasksController {

    /**
     * Serviceインターフェース。
     */
    @Autowired
    IOutputTasks outputTasks;

    /**
     * apprication.propertiesから取得した署名付きURL有効期限
     */
    @Value("${aws.s3.signature.duration}")
    private int durationMinutes;

    /** エラーメッセージを取得する為のメッセージソース. */
    @Autowired
    MessageSource messageSource;

    private final FileStatusSecretsProperty fileStatusSecretsProperty;

    // コンストラクタでS3クライアントとアプリケーション設定を受け取る
    public OutputTasksController(FileStatusSecretsProperty fileStatusSecretsProperty) {
        this.fileStatusSecretsProperty = fileStatusSecretsProperty;
    }

    /**
     * ファイル作成状況確認API
     * 
     * @param requestDto ファイル作成状況確認リクエストDto
     * @return DBから取得したレスポンス
     * @throws Exception
     */
    @GetMapping(OutputTaskConstants.GET_URI)
    public ResponseEntity<OutputTasksResponseDto> confirmFileCreateStatus(@RequestParam(required = false) String taskId,
            HttpServletRequest request) throws Exception {

        if (taskId == null || taskId.trim().equals("")) {
            Map<String, String> taskInfo = new HashMap<String, String>();
            taskInfo.put("taskId", taskId);
            throw new ParamErrorException(taskInfo);
        }

        GetOutputTasksRequestDto taskReq = new GetOutputTasksRequestDto();

        taskReq.setTaskId(taskId);
        taskReq.setServicerId((String) request.getAttribute(RestApiConstants.SERVICER_ID));

        // 2.設備データ出力タスク参照
        ResponseEntity<GetOutputTasksResponseDto> taskRes = outputTasks.getTask(taskReq);

        // ステータスコードを取得
        HttpStatusCode statusCode = taskRes.getStatusCode();

        if (!statusCode.is2xxSuccessful()) {

            // HTTPステータスコードが200以外はエラー
            throw new InternalApiCallingException(OutputTaskConstants.MESSAGE);

        }

        GetOutputTasksResponseDto responseBody = taskRes.getBody();
        int count = responseBody.getTaskList().size();

        if (count == 0) {
            // タスク件数が0の場合、リソースなし例外を生成
            Map<String, String> item = new HashMap<String, String>();
            item.put("taskId", taskId);
            throw new NotFoundException(item);
        }

        // タスク
        TaskDto task = responseBody.getTaskList().getFirst();
        // タスク状況
        String taskStatus = task.getTaskStatus();
        // ファイル格納先
        String fileUrl = task.getFileUrl();
        // エラー内容
        String errorDetail = task.getErrorDetail();

        // 4.署名付きURL生成
        // 5.レスポンス設定
        OutputTasksResponseDto res = new OutputTasksResponseDto();
        OutputTasksSubResponseDto sub = new OutputTasksSubResponseDto();

        res.setDataModelType(RestApiConstants.TEST1);

        // タスク状況 ※常に設定
        sub.setTaskStatus(taskStatus);

        if (RestApiConstants.TASK_STATUS_COMPLETED.equals(taskStatus)) {
            // タスク状況が"2:完了"の場合

            // 署名付きURL
            sub.setFilePath(generatePresignedUrl(fileUrl));

            // 通常のレスポンス
        } else if (RestApiConstants.TASK_STATUS_ERROR.equals(taskStatus)) {
            // タスク状況が"9:エラー"の場合

            // エラー内容
            sub.setError(errorDetail);
        }

        res.setAttribute(sub);

        return ResponseEntity.ok(res);
    }

    /**
     * 署名付きURL作成
     * 
     * @param fileUrl ファイル格納先URL
     * @return 署名付きURL
     * @throws JsonProcessingException
     */
    private String generatePresignedUrl(String fileUrl) throws JsonProcessingException {

        String bucket = fileStatusSecretsProperty.getBucket();
        String region = fileStatusSecretsProperty.getRegion();

        // 除去文字列
        String substringToRemove = RestApiConstants.S3PATH + bucket + RestApiConstants.SLASH;
        // ファイルURL文字列から指定した部分を除去
        String keyName = fileUrl.replace(substringToRemove, "");

        // 署名付きURL
        String url = AwsS3Manager.createPresignedGetUrl(bucket, keyName, durationMinutes, region);

        return url;
    }
}
