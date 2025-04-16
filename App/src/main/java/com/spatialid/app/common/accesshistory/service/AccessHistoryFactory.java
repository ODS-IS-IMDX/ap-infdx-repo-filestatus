// © 2025 NTT DATA Japan Co., Ltd. & NTT InfraNet All Rights Reserved.

package com.spatialid.app.common.accesshistory.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.util.ContentCachingResponseWrapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spatialid.app.common.accesshistory.constants.AccessHistoryConstants;
import com.spatialid.app.common.accesshistory.dto.RegAccessHistoryDto;
import com.spatialid.app.common.accesshistory.dto.UpdAccessHistoryDto;
import com.spatialid.app.common.accesshistory.utils.AccessHistoryUtil;
import com.spatialid.app.common.accesshistory.utils.RequestCachingForFilterWrapper;
import com.spatialid.app.common.constant.RestApiConstants;
import com.spatialid.app.common.exception.subexception.AccessDeniedException;
import com.spatialid.app.common.resource.ErrorResponse;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.ServletInputStream;

/**
 * リクエストボディ・GETパラメータから{@link JsonNode}型のリクエスト内容を生成するファクトリクラス．<br>
 * Getパラメータ・リクエストボディをリクエスト内容として扱う．
 * 
 * @author matsumoto kentaro
 * @version 1.1 2024/11/22
 */
@Service
public class AccessHistoryFactory {
    
    /**
     * 指定されたDTOに対応する生成ロジックを保持するマップ．
     */
    private final Map<Class<?>, Function<Map<String, Object>, ?>> creators = new HashMap<>();
    
    /**
     * Jsonオブジェクトマッパー．
     */
    private final ObjectMapper objectMapper;
    
    public AccessHistoryFactory(ObjectMapper objectMapper) {
        
        this.objectMapper = objectMapper;
        
    }
    
    /**
     * 初期化処理を行う．
     * <p>
     * ファクトリクラスとしてのDTOの生成を定義して、マップ型で保持する．
     * </p>
     * 
     * @throws Exception
     */
    @PostConstruct
    private void init() throws Exception {
        
        // アクセス履歴登録処理時の生成ロジック
        creators.put(RegAccessHistoryDto.class, params -> {
            
            final RequestCachingForFilterWrapper requestWrapper = (RequestCachingForFilterWrapper) params.get(AccessHistoryConstants.FACTORY_PARAM_KEY_REQUEST);
            
            try {
                
                final String servicerId = AccessHistoryUtil.getServicerId(requestWrapper.getHeader("Authorization"));
                
                // jwt・利用者システムIDの設定値に不備があった場合
                if ("".equals(servicerId)) {
                    
                    throw new AccessDeniedException();
                    
                }
                
                return RegAccessHistoryDto.builder()
                        .requestUri(requestWrapper.getRequestURI())
                        .servicerId(servicerId)
                        .accessDate(getNowAsString())
                        .request(createRequestContent(requestWrapper))
                        .build();
                
            } catch (AccessDeniedException e) {
                
                throw e;
            
            }catch (Exception e) {
                
                // 例外をラップして再スロー
                throw new RuntimeException(e);
                
            }
            
        });
        
        // アクセス履歴更新処理時の生成ロジック
        creators.put(UpdAccessHistoryDto.class, params -> {
            
            final RegAccessHistoryDto registedAccessHistoryDto = (RegAccessHistoryDto) params.get(AccessHistoryConstants.FACTORY_PARAM_KEY_REGISTED_PARAM);
            
            final ContentCachingResponseWrapper responseWrapper = (ContentCachingResponseWrapper) params.get(AccessHistoryConstants.FACTORY_PARAM_KEY_RESPONSE);
            
            try {
                
                final ErrorResponse errorResponse = objectMapper.readValue(responseWrapper.getContentInputStream(), ErrorResponse.class);
                
                return UpdAccessHistoryDto.builder()
                        .requestUri(registedAccessHistoryDto.getRequestUri())
                        .servicerId(registedAccessHistoryDto.getServicerId())
                        .accessDate(registedAccessHistoryDto.getAccessDate())
                        .httpStatus(responseWrapper.getStatus())
                        .errorDetail(errorResponse.getMessage())
                        .build();
                
            } catch (Exception e) {
                
                throw new RuntimeException(e);
                
            }
                        
        });
                
    }
    
    /**
     * 指定されたDTOの生成を行う．
     * <p>
     * 生成するDTOはClassインスタンスを引数に渡す事で指定する．<br>
     * 生成されたDTOに設定する値は、マップ型にすべて格納して引数に渡す．<br>
     * 使用例：
     * </p>
     * <pre>
     * <code>
     * {@code @Autowired}
     * private AccessHistoryFactory accessHistoryFactory;
     * 
     * private final MyDto myDto = accessHistoryFactory.createDto(MyClass.class, Map.of("key", ParamsDto));
     * </code>
     * </pre>
     * 
     * @param <T> 返り値の型情報l
     * @param requiredClass 生成するDTO
     * @param params DTO生成時に設定するパラメータ
     * @return 値が設定されたDTO
     */
    @SuppressWarnings("unchecked")
    public <T> T createDto(Class<T> requiredClass, Map<String, Object> params) {
        
        final Function<Map<String, Object>, ?> creator = creators.get(requiredClass);
        
        // 要求されたDTOの生成ロジックが実装されていなかった場合
        if (creators == null) {
            
            throw new IllegalArgumentException();
            
        }
        
        return (T) creator.apply(params);
        
    }
        
    /**
     * {@link JsonNode}型のリクエスト内容を生成する．
     * 
     * @param requestWrapper リクエスト
     * @return {@link JsonNode}型に変換されたリクエスト内容
     * @throws JsonProcessingException リクエストが{@link JsonNode}型への変換が不可能な構造をしていた場合
     * @throws IOException リクエストボディの読み取りに失敗した場合
     */
    private JsonNode createRequestContent(RequestCachingForFilterWrapper requestWrapper) throws JsonProcessingException, IOException {
        
        final String httpMethod = requestWrapper.getMethod();
        
        JsonNode requestContent = null;
        
        if (HttpMethod.GET.toString().equals(httpMethod)) {
            
            requestContent = parseGetParameter(requestWrapper.getParameterMap());
            
        } else if(HttpMethod.POST.toString().equals(httpMethod) || HttpMethod.PUT.toString().equals(httpMethod)) {
            
            requestContent = parseRequestBody(requestWrapper.getInputStream());
            
        }
        
        return  requestContent;
        
    }
    
    /**
     * Getパラメータの配列を{@link JsonNode}型に変換する．
     * <p>
     * パラメータの型は、本メソッドでは決定できないためすべて文字列として変換する．
     * </p>
     * 
     * @param parameters Map型に変換したGetパラメータ
     * @return {@link JsonNode}型に変換されたGetパラメータ
     * @throws JsonProcessingException パラメータを格納したMapが、{@link JsonNode}型への変換が不可能な構造をしていた場合
     */
    private JsonNode parseGetParameter(Map<String, String[]> parameters) throws JsonProcessingException {
        
        final Map<String, String> contentMap = parameters.entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> String.join(",", entry.getValue())));
        
        final String jsonStr = objectMapper.writeValueAsString(contentMap);
        
        return objectMapper.readTree(jsonStr);
        
    }
    
    /**
     * {@link ServletInputStream}からリクエストボディを読み取り、{@link JsonNode}型に変換する．
     * 
     * @param requestBodyAsStream リクエストボディ
     * @return {@link JsonNode}型に変換されたリクエストボディ
     * @throws JsonProcessingException リクエストボディが{@link JsonNode}型への変換が不可能な構造をしていた場合
     * @throws IOException {@link ServletInputStream}からリクエストボディの読み取りに失敗した場合
     */
    private JsonNode parseRequestBody(ServletInputStream requestBodyAsStream) throws JsonProcessingException, IOException {
        
        return objectMapper.readTree(requestBodyAsStream);
        
    }
    
    /**
     * 現在日時を文字列型で返却する．
     * 
     * @return 現在日時
     */
    private String getNowAsString() {
        
        final LocalDateTime now = LocalDateTime.now();
        
        final DateTimeFormatter format = DateTimeFormatter.ofPattern(RestApiConstants.HIGH_ACCURACY_DATE_FORMAT);
        
        return now.format(format);
        
    }
    
}
