// © 2025 NTT DATA Japan Co., Ltd. & NTT InfraNet All Rights Reserved.

package com.spatialid.app.common.accesshistory.service;

import org.springframework.web.util.ContentCachingResponseWrapper;

import com.spatialid.app.common.accesshistory.dto.RegAccessHistoryDto;
import com.spatialid.app.common.accesshistory.utils.RequestCachingForFilterWrapper;

/**
 * APIの呼び出しを提供するインターフェース．
 * 
 * @author matsumoto kentaro
 * @version 1.1 2024/11/22
 */
public interface IApiCallingService {
    
    /**
     * アクセス履歴登録APIの呼び出しを提供する．
     * 
     * @param requestWrapper リクエスト
     * @return アクセス履歴の登録内容
     * @throws Exception
     */
    public RegAccessHistoryDto callRegistAccessHistory(RequestCachingForFilterWrapper requestWrapper) throws Exception;
    
    /**
     * アクセス履歴更新APIの呼び出しを提供する．
     * 
     * @param registedAccessHistoryDto 既に登録されているアクセス履歴の内容
     * @param responseWrapper APIから返却されたレスポンス
     * @throws Exception
     */
    public void callUpdateAccessHistory(RegAccessHistoryDto registedAccessHistoryDto, ContentCachingResponseWrapper responseWrapper) throws Exception;
    
}
