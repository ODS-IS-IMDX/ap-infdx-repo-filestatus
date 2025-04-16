// © 2025 NTT DATA Japan Co., Ltd. & NTT InfraNet All Rights Reserved.

package com.spatialid.app.api.presentation.aspect;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.servlet.HandlerInterceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spatialid.app.api.constants.AccessControlInterceptorConstants;
import com.spatialid.app.api.converter.JsonConverter;
import com.spatialid.app.api.presentation.dto.permission.ApiAuthDto;
import com.spatialid.app.api.presentation.dto.permission.ApiInfoDto;
import com.spatialid.app.api.presentation.dto.permission.GetApiAuthResponseDto;
import com.spatialid.app.common.constant.RestApiConstants;
import com.spatialid.app.common.exception.subexception.AccessDeniedException;
import com.spatialid.app.common.exception.subexception.InternalApiCallingException;
import com.spatialid.app.common.property.RestClientProperty;
import com.spatialid.app.common.property.secrets.RestClientSecretsProperty;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class AccessControlInterceptor implements HandlerInterceptor {
    
    private final RestClientProperty restClientProperty;
    
    private final RestClientSecretsProperty restClientSecretsProperty;
    
    private final ObjectMapper objectMapper;

    public AccessControlInterceptor(RestClientProperty restClientProperty,
            RestClientSecretsProperty restClientSecretsProperty,
            ObjectMapper objectMapper) {
        
        this.restClientProperty = restClientProperty;
        this.restClientSecretsProperty = restClientSecretsProperty;
        this.objectMapper = objectMapper;
        
    }
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {

        // リクエストからJWTを取得
        String jwt = request.getHeader("Authorization");

        // JWT：null・空文字チェック
        if (StringUtils.isEmpty(jwt) || !(StringUtils.contains(jwt, "Bearer"))) {
            throw new AccessDeniedException();
        }

        String[] separatedJwt = jwt.replace("Bearer", "").strip().split("\\.");
        if (separatedJwt.length < 2) {
            throw new AccessDeniedException();
        }

        // ペイロードをデコードして変数に格納
        Base64.Decoder decoder = Base64.getUrlDecoder();
        String payLoad = new String(decoder.decode(separatedJwt[1]));

        // payLoad：null・空文字チェック
        if (StringUtils.isEmpty(payLoad)) {
            throw new AccessDeniedException();
        }

        // Javaオブジェクト変換後の値格納用変数
        JsonConverter jsonConverter;

        // JSONをJavaオブジェクトに変換（第二引数に変換するクラスの型を指定）
        jsonConverter = objectMapper.readValue(payLoad, JsonConverter.class);

        // 公開識別子ID取得
        String openSystemId = jsonConverter.getOpen_system_id();

        // 公開識別子ID：null・空文字チェック
        if (StringUtils.isEmpty(openSystemId)) {
            throw new AccessDeniedException();
        }
        
        //利用者システムID取得
        String servicerId = openSystemId.substring(openSystemId.length()-5);

        // // 利用権限参照に渡すリクエストURI・シークレットマネージャー
        String apiUrl = String.format("%s://%s:%s%s%s%s",
                restClientProperty.getProtocol(),
                restClientSecretsProperty.getAuthorityDomain(),
                restClientProperty.getPort(),
                AccessControlInterceptorConstants.BASE_PATH,
                AccessControlInterceptorConstants.API_AUTH_ENDPOINT,
                servicerId);

        // API利用権限参照からレスポンスを取得
        ResponseEntity<GetApiAuthResponseDto> responseEntity = new ResponseEntity<GetApiAuthResponseDto>(
                HttpStatus.INTERNAL_SERVER_ERROR);

        try {
            // クライアントの作成
            RestClient restClient = RestClient.create();

            // リクエストの送信
            responseEntity = restClient.get().uri(apiUrl).retrieve().toEntity(GetApiAuthResponseDto.class);

        } catch (Exception e) {

        }

        HttpStatusCode statusCode = responseEntity.getStatusCode();

        // ステータスコードチェック
        if (!statusCode.is2xxSuccessful()) {

            throw new InternalApiCallingException(AccessControlInterceptorConstants.API_AUTH_NAME);
        }

        // リクエストURIを取得
        String requestUri = request.getRequestURI();

        // API利用権限参照レスポンスを値を変数に格納
        GetApiAuthResponseDto responseDto = responseEntity.getBody();
        List<ApiAuthDto> apiAuthDtoList = responseDto.getApiAuthList();
        List<String> uriList = new ArrayList<String>();

        for (ApiAuthDto apiAuthDto : apiAuthDtoList) {
            for (ApiInfoDto apiInfoDto : apiAuthDto.getApiInfoList()) {
                uriList.add(apiInfoDto.getUri());
            }
        }

        // リクエストURIと合致するURIがあるかチェック
        if (uriList.contains(requestUri)) {
            response.setHeader("Access-Control-Result", "true");
            request.setAttribute(RestApiConstants.SERVICER_ID, servicerId);
        } else {
            throw new AccessDeniedException();
        }

        return true;
        
    }
    
}
