// © 2025 NTT DATA Japan Co., Ltd. & NTT InfraNet All Rights Reserved.

package com.spatialid.app.common.accesshistory.service;

import org.springframework.stereotype.Service;
import org.springframework.web.util.ContentCachingResponseWrapper;

import com.spatialid.app.common.accesshistory.dto.RegAccessHistoryDto;
import com.spatialid.app.common.accesshistory.utils.RequestCachingForFilterWrapper;

import jakarta.servlet.FilterChain;

/**
 * 委譲されたアクセス履歴フィルターの処理を行うサービスクラス．<br>
 * フィルターの実装クラスはAOPによるウェービングの対象にできないため、本クラスをロギングの対象とする．
 * 
 * @author matsumoto kentaro
 * @version 1.1 2024/11/29
 */
@Service
public class FilterDeligateServiceImpl implements IFilterDeligateService {
    
    /**
     * APIの呼び出しを行うサービスクラス．
     */
    private final IApiCallingService apiCallingService;
    
    public FilterDeligateServiceImpl(IApiCallingService apiCallingService) {
        
        this.apiCallingService = apiCallingService;
        
    }
    
    /**
     * 委譲されたアクセス履歴登録・アクセス履歴処理を行う．
     * 
     * @param requestWrapper キャッシュされたリクエスト
     * @param responseWrapper キャッシュされたレスポンス
     * @param filterChain {@link FilterChain}
     * @throws Exception
     */
    @Override
    public void doFilterInternal(RequestCachingForFilterWrapper requestWrapper,
            ContentCachingResponseWrapper responseWrapper,
            FilterChain filterChain) throws Exception {
                
        RegAccessHistoryDto registedAccessHistoryDto = null;
        
        try {
            
            //アクセス履歴を登録
            registedAccessHistoryDto = apiCallingService.callRegistAccessHistory(requestWrapper);
            
            // 各APIの処理に進む
            filterChain.doFilter(requestWrapper, responseWrapper);
            
            //レスポンス内容などをアクセス履歴として更新
            apiCallingService.callUpdateAccessHistory(registedAccessHistoryDto, responseWrapper);

            responseWrapper.copyBodyToResponse();
            
        } catch (Exception e) {
                        
            throw e;
            
        }
                
    }
    
}
