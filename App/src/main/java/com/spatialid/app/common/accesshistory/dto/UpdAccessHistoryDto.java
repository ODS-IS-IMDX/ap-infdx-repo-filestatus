// © 2025 NTT DATA Japan Co., Ltd. & NTT InfraNet All Rights Reserved.

package com.spatialid.app.common.accesshistory.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

/**
 * アクセス履歴登録の初回登録時のリクエストを保持するDTO．
 * 
 * @author matsumoto kentaro
 * @version 1.1 2024/12/05
 */
@Value
@AllArgsConstructor
@Builder
public class UpdAccessHistoryDto {
    
    /**
     * リクエストURI．
     */
    private String requestUri;
    
    /**
     * 利用者システムID．
     */
    private String servicerId;
    
    /**
     * アクセス日時．
     */
    private String accessDate;
    
    /**
     * HTTPステータス．
     */
    private int httpStatus;
    
    /**
     * エラー内容．
     */
    private String errorDetail;

}
