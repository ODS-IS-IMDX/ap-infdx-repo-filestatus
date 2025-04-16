// © 2025 NTT DATA Japan Co., Ltd. & NTT InfraNet All Rights Reserved.

package com.spatialid.app.common.accesshistory.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.ContentCachingResponseWrapper;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spatialid.app.common.accesshistory.constants.AccessHistoryConstants;
import com.spatialid.app.common.accesshistory.dto.RegAccessHistoryDto;
import com.spatialid.app.common.accesshistory.dto.UpdAccessHistoryDto;
import com.spatialid.app.common.accesshistory.utils.RequestCachingForFilterWrapper;
import com.spatialid.app.common.exception.subexception.AccessHistoryUpdateException;
import com.spatialid.app.common.exception.subexception.InternalApiCallingException;
import com.spatialid.app.common.exception.subexception.UriNotFoundException;

/**
 * APIの呼び出しを実装するサービスクラス．<br>
 * {@link IApiCallingService}の実装クラス．
 * 
 * @author matsumoto kentaro
 * @version 1.1 2024/11/22
 */
@Service
public class ApiCallingServiceImpl implements IApiCallingService {
        
    /**
     * アクセス履歴API用のApiClient．
     */
    private final RestClient restClient;
    
    /**
     * リクエスト内容を生成するファクトリ．
     */
    private final AccessHistoryFactory accessHistoryFactory;
    
    /**
     * Jsonオブジェクトマッパー．
     */
    private final ObjectMapper objectMapper;
    
    public ApiCallingServiceImpl(@Qualifier("accessHistoryApiClient") RestClient restClient,
            AccessHistoryFactory accessHistoryFactory,
            ObjectMapper objectMapper) {
        
        this.restClient = restClient;
        this.accessHistoryFactory = accessHistoryFactory;
        this.objectMapper = objectMapper;
        
    }
    
    /**
     * アクセス履歴登録APIの呼び出しを行う．
     * <p>
     * アクセス履歴の登録はレスポンスボディが存在せずに成功時には、204が返却される想定．
     * </p>
     * 
     * @param requestWrapper リクエスト
     * @return アクセス履歴の登録内容
     * @throws Exception 
     */
    @Override
    public RegAccessHistoryDto callRegistAccessHistory(RequestCachingForFilterWrapper requestWrapper) throws Exception {
        
        RegAccessHistoryDto registAccessHistoryDto = null;

        try {
            
            // アクセス履歴登録を行うリクエストDTOをファクトリクラスで作成
            registAccessHistoryDto = accessHistoryFactory.createDto(RegAccessHistoryDto.class,
                    Map.of(AccessHistoryConstants.FACTORY_PARAM_KEY_REQUEST, requestWrapper));
            
            final ResponseEntity<Void> rawResponse = restClient.method(HttpMethod.POST)
                    .uri(AccessHistoryConstants.UPSERT_ACCESS_HISTORY_URI)
                    .accept(MediaType.APPLICATION_JSON)
                    .body(registAccessHistoryDto)
                    .retrieve()
                    .toBodilessEntity();
            
            if (rawResponse.getStatusCode().isSameCodeAs(HttpStatusCode.valueOf(404))) {
                
                throw new UriNotFoundException();
                
            } else if(!(rawResponse.getStatusCode().isSameCodeAs(HttpStatusCode.valueOf(204)))) {
                    
                throw new InternalApiCallingException(AccessHistoryConstants.UPSERT_ACCESS_HISTORY_NAME);
                    
            }
            
            return registAccessHistoryDto;
            
        } catch (Exception e) {
            
            throw e;
            
        }
        
    }
    
    /**
     * アクセス履歴登録(更新)APIの呼び出しを行う．
     * <p>
     * 更新処理時に発生したあらゆるエラーは、{@link AccessHistoryUpdateException}にラップして再送出される．<br>
     * アクセス履歴登録(更新)はレスポンスボディが存在せずに成功時には、204が返却される想定．
     * </p>
     * 
     * @param registedAccessHistoryDto 既に登録されているアクセス履歴の内容
     * @exception Exception
     */
    @Override
    public void callUpdateAccessHistory(RegAccessHistoryDto registedAccessHistoryDto,
            ContentCachingResponseWrapper responseWrapper) throws Exception {
        
        UpdAccessHistoryDto updAccessHistoryDto = null;
        
        try {
            
            // アクセス履歴登録(更新)を行うリクエストDTOをファクトリクラスで作成
            updAccessHistoryDto = accessHistoryFactory.createDto(UpdAccessHistoryDto.class,
                    Map.of(AccessHistoryConstants.FACTORY_PARAM_KEY_REGISTED_PARAM, registedAccessHistoryDto,
                            AccessHistoryConstants.FACTORY_PARAM_KEY_RESPONSE, responseWrapper));
            
            final ResponseEntity<Void> rawResponse = restClient.method(HttpMethod.POST)
                    .uri(AccessHistoryConstants.UPSERT_ACCESS_HISTORY_URI)
                    .accept(MediaType.APPLICATION_JSON)
                    .body(updAccessHistoryDto)
                    .retrieve()
                    .toBodilessEntity();
            
            if (!(rawResponse.getStatusCode().isSameCodeAs(HttpStatusCode.valueOf(204)))) {
                
                throw new InternalApiCallingException(AccessHistoryConstants.UPSERT_ACCESS_HISTORY_NAME);
                
            }
            
        } catch (Exception e) {
            
            final Map<String, Object> upsertAccessHistoryMap = objectMapper.convertValue(updAccessHistoryDto, new TypeReference<Map<String, Object>>() {});
            
            throw new AccessHistoryUpdateException(upsertAccessHistoryMap, e);
            
        }
        
    }
                
}
