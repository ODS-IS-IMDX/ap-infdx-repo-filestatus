// © 2025 NTT DATA Japan Co., Ltd. & NTT InfraNet All Rights Reserved.

package com.spatialid.app.api.application.service.task;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.spatialid.app.api.application.dto.outputtasks.reference.GetOutputTasksRequestDto;
import com.spatialid.app.api.application.dto.outputtasks.reference.GetOutputTasksResponseDto;
import com.spatialid.app.api.constants.OutputTaskConstants;
import com.spatialid.app.common.property.RestClientProperty;
import com.spatialid.app.common.property.secrets.FileStatusSecretsProperty;

/**
 * ファイル作成状況確認APIのServiceインターフェースの実装クラス。
 * 
 * @author Nishikawa Hayato
 * @version 1.1 2024/09/06
 */

@Service
@Component
public class OutputTasksImpl implements IOutputTasks {

    private final FileStatusSecretsProperty fileStatusSecretsProperty;
    private final RestClient restClient;
    private final RestClientProperty restClientProperty;

    public OutputTasksImpl(FileStatusSecretsProperty fileStatusSecretsProperty,
            @Qualifier("authorityApiClient") RestClient restClient, RestClientProperty restClientProperty) {

        this.fileStatusSecretsProperty = fileStatusSecretsProperty;
        this.restClient = restClient;
        this.restClientProperty = restClientProperty;

    }

    /**
     * apprication.propertiesから取得した業務共通コンテナのURL
     */
    @Override
    public ResponseEntity<GetOutputTasksResponseDto> getTask(GetOutputTasksRequestDto request)
            throws JsonProcessingException {

        // APIのURL
        String domain = fileStatusSecretsProperty.getCommon();
        String commonServerPort = restClientProperty.getPort();

        String url = "http://" + domain + ":" + commonServerPort + OutputTaskConstants.GET_OUTPUT_TASK_URI;

        // ヘッダーの設定
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // ResponseEntityを作成
        Map<String, String> params = new HashMap<>();
        params.put("taskId", request.getTaskId());
        params.put("servicerId", request.getServicerId());

        String urlTemplate;
        urlTemplate = UriComponentsBuilder.fromHttpUrl(url).queryParam("taskId", "{taskId}")
                .queryParam("servicerId", "{servicerId}").encode().toUriString();

        ResponseEntity<GetOutputTasksResponseDto> responseEntity = restClient.get().uri(urlTemplate, params).retrieve()
                .toEntity(GetOutputTasksResponseDto.class);

        return responseEntity;
    }
}