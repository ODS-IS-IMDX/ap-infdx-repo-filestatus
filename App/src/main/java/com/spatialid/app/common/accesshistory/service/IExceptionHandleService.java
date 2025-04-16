// © 2025 NTT DATA Japan Co., Ltd. & NTT InfraNet All Rights Reserved.

package com.spatialid.app.common.accesshistory.service;

import org.springframework.http.HttpStatus;
import org.springframework.web.util.ContentCachingResponseWrapper;

/**
 * アクセス履歴のフィルターで発生した例外のハンドリングを提供するインターフェース．
 * 
 * @author matsumoto kentaro
 * @version 1.1 2024/11/28
 */
public interface IExceptionHandleService {
    
    /**
     * アクセス履歴の初回登録時に発生した例外のハンドリングを提供する．
     * 
     * @param responseWrapper レスポンス
     * @param status {@link HttpStatus} 返却するHTTPステータス
     */
    public void writeErrorResponse(ContentCachingResponseWrapper responseWrapper, HttpStatus status);
    
}
