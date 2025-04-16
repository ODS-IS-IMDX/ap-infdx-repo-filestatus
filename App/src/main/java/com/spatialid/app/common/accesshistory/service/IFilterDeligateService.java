// © 2025 NTT DATA Japan Co., Ltd. & NTT InfraNet All Rights Reserved.

package com.spatialid.app.common.accesshistory.service;

import org.springframework.web.util.ContentCachingResponseWrapper;

import com.spatialid.app.common.accesshistory.utils.RequestCachingForFilterWrapper;

import jakarta.servlet.FilterChain;

/**
 * 委譲されたアクセス履歴フィルターの処理を提供するインターフェース．
 * 
 * @author matsumoto kentaro
 * @version 1.1 2024/11/29
 */
public interface IFilterDeligateService {
    
    /**
     * 委譲されたアクセス履歴登録・アクセス履歴登録(更新)を提供する．
     * 
     * @param requestWrapper キャッシュされたリクエスト
     * @param responseWrapper キャッシュされたレスポンス
     * @param filterChain {@link FilterChain}
     * @throws Exception
     */
    public void doFilterInternal(RequestCachingForFilterWrapper requestWrapper,
            ContentCachingResponseWrapper responseWrapper,
            FilterChain filterChain) throws Exception;
    
}
