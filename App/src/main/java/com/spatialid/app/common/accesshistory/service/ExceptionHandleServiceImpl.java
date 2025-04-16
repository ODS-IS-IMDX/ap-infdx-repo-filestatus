// © 2025 NTT DATA Japan Co., Ltd. & NTT InfraNet All Rights Reserved.

package com.spatialid.app.common.accesshistory.service;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.util.ContentCachingResponseWrapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spatialid.app.common.constant.ExceptionConstant;
import com.spatialid.app.common.constant.RestApiConstants;
import com.spatialid.app.common.resource.ErrorResponse;

/**
 * アクセス履歴のフィルターで発生した例外のハンドリングを行うサービスクラス．
 * 
 * @author matsumoto kentaro
 * @version 1.1 2024/11/28
 */
@Service
public class ExceptionHandleServiceImpl implements IExceptionHandleService {
    
    /**
     * メッセージソース．
     */
    private final MessageSource messageSource;
    
    /**
     * Jsonオブジェクトマッパー．
     */
    private final ObjectMapper objectMapper;
    
    public ExceptionHandleServiceImpl(MessageSource messageSource,
            ObjectMapper objectMapper) {
        
        this.messageSource = messageSource;
        this.objectMapper = objectMapper;
    }
    
    /**
     * アクセス履歴の初回登録時に発生した例外のハンドリングを行う．
     * 
     * @param responseWrapper レスポンス
     * @param status {@link HttpStatus} 返却するHTTPステータス
     */
    @Override
    public void writeErrorResponse(ContentCachingResponseWrapper responseWrapper, HttpStatus status) {
        
        responseWrapper.setStatus(status.value());
        
        responseWrapper.setContentType(MediaType.APPLICATION_JSON.toString());
        
        responseWrapper.setCharacterEncoding(StandardCharsets.UTF_8.name());
        
        try {
            
            final String errorResponseAsStr = switch(status) {
            
                case INTERNAL_SERVER_ERROR -> create500Response(status);
                
                case FORBIDDEN -> create403Response(status);
                
                case NOT_FOUND -> create404Response(status);
            
                default -> create500Response(status);
            
            };
            
            responseWrapper.getWriter().write(errorResponseAsStr);

        } catch(Exception e) {
            
            // catch節から呼び出されるため、非検査例外にラップして送出する
            throw new RuntimeException(e);
            
        }
        
        
    }
    
    /**
     * 500エラー用のレスポンス文言を作成する．
     * 
     * @param status HTTPステータスコード
     * @return エラーレスポンス文言
     * @throws JsonProcessingException
     */
    private String create500Response(HttpStatus status) throws JsonProcessingException {
        
        final String message = messageSource.getMessage(ExceptionConstant.KEY_MSG_INTERNAL_SERVER, null, Locale.JAPAN);
        
        final ErrorResponse errorResponse = createErrorResponse(status.getReasonPhrase(), message);
        
        return objectMapper.writeValueAsString(errorResponse);
        
    }
    
    /**
     * 403エラー用のレスポンス文言を作成する．
     * 
     * @param status HTTPステータスコード
     * @return エラーレスポンス文言
     * @throws JsonProcessingException
     */
    private String create403Response(HttpStatus status) throws JsonProcessingException {
        
        final String message = messageSource.getMessage(ExceptionConstant.KEY_MSG_FORBIDDEN, null, Locale.JAPAN);
        
        final ErrorResponse errorResponse = createErrorResponse(status.getReasonPhrase(), message);
        
        return objectMapper.writeValueAsString(errorResponse);
        
    }
    
    /**
     * 404エラー用のレスポンス文言を作成する．
     * 
     * @param status HTTPステータスコード
     * @return エラーレスポンス文言
     * @throws JsonProcessingException
     */
    private String create404Response(HttpStatus status) throws JsonProcessingException {
        
        final String message = messageSource.getMessage(ExceptionConstant.KEY_MSG_URI_NOTFOUND, null, Locale.JAPAN);
        
        final ErrorResponse errorResponse = createErrorResponse(status.getReasonPhrase(), message);
        
        return objectMapper.writeValueAsString(errorResponse);
        
    }
    
    /**
     * エラーレスポンスオブジェクトを生成する．
     * <p>
     * エラーレスポンスの生成過程で以下の処理を行う．<br>
     * ・HTTPステータスフレーズに接頭辞を付与<br>
     * ・エラー発生日時の取得
     * </p>
     * 
     * @param rawCode 接頭辞のないHTTPステータスフレーズ
     * @param message エラー内容
     * @return {@link ErrorResponse} エラーレスポンスオブジェクト
     */
    private ErrorResponse createErrorResponse(String rawCode, String message) {

        final StringBuilder codeBuilder = new StringBuilder();
        
        final String code = codeBuilder.append(ExceptionConstant.CODE_PREFIX)
            .append(" ")
            .append(rawCode)
            .toString();
        
        final DateTimeFormatter fm = DateTimeFormatter.ofPattern(RestApiConstants.DATE_FORMAT);
        
        final String detail = LocalDateTime.now()
                .format(fm);
        
        return ErrorResponse.builder()
                .code(code)
                .message(message)
                .detail(detail)
                .build();
        
    }
    
}
