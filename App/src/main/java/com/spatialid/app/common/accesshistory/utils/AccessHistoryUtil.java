// © 2025 NTT DATA Japan Co., Ltd. & NTT InfraNet All Rights Reserved.

package com.spatialid.app.common.accesshistory.utils;

import java.util.Base64;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spatialid.app.common.exception.subexception.AccessDeniedException;

/**
 * アクセス履歴管理におけるユーティリティメソッドをまとめたクラス．
 * 
 * @author matsumoto kentaro
 * @version 1.1 2024/12/2
 */
public class AccessHistoryUtil {
    
    /**
     * jwtから利用者システムIDを抽出する．
     * <p>
     * jwt/利用者システムIDについて、下記の値が設定されていた場合は空文字が返却される．<br>
     *  ・リクエストヘッダーにjwtが存在しない<br>
     *  ・AuthorizationヘッダーにBearerが含まれていない<br>
     *  ・jwtのヘッダー部/ペイロード部/署名部が欠落している<br>
     *  ・利用者システムIDが項目欠損・値がnull/空文字に設定されている
     * </p>
     * 
     * 
     * @param jwt リクエストヘッダーから取得したJWT
     * @return JWTのペイロードから取得した利用者システムID
     * @throws JsonProcessingException
     */
    public static final String getServicerId(String jwt) throws JsonProcessingException {
        
        if (StringUtils.isEmpty(jwt) || !(StringUtils.contains(jwt, "Bearer"))) {
            
            return "";
            
        }
        
        final String[] separatedJwt = jwt.replace("Bearer", "").strip().split("\\.");

        if (separatedJwt.length < 3) {
            
            return "";
            
        }
        
        final Base64.Decoder decoder = Base64.getUrlDecoder();
        
        final String payLoad = new String(decoder.decode(separatedJwt[1]));
        
        final ObjectMapper objectMapper = new ObjectMapper();
        
        final JsonNode openSystemIdNode = objectMapper.readTree(payLoad).get("open_system_id");
        
        // 項目欠損・値がnull/空文字の場合
        if (openSystemIdNode == null || openSystemIdNode.isNull() || StringUtils.isEmpty(openSystemIdNode.asText())) {
            
            return "";
            
        }
        
        final String openSystemId = openSystemIdNode.asText();
        
        return openSystemId.substring(openSystemId.length() - 5);
        
    }
    
}
