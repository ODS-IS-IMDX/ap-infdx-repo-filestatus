// © 2025 NTT DATA Japan Co., Ltd. & NTT InfraNet All Rights Reserved.

package com.spatialid.app.common.accesshistory.dto;

import com.fasterxml.jackson.databind.JsonNode;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

/**
 * アクセス履歴登録の更新時のリクエストを保持するDTO．
 * 
 * @author matsumoto kentaro
 * @version 1.1 2024/12/05
 */
@Value
@AllArgsConstructor
@Builder
public class RegAccessHistoryDto {
    
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
     * リクエスト内容．
     */
    private JsonNode request;


}
