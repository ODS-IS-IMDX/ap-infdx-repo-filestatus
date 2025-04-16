// © 2025 NTT DATA Japan Co., Ltd. & NTT InfraNet All Rights Reserved.

package com.spatialid.app.common.accesshistory.filter;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingResponseWrapper;

import com.spatialid.app.common.accesshistory.constants.AccessHistoryConstants;
import com.spatialid.app.common.accesshistory.service.FilterDeligateServiceImpl;
import com.spatialid.app.common.accesshistory.service.IExceptionHandleService;
import com.spatialid.app.common.accesshistory.service.IFilterDeligateService;
import com.spatialid.app.common.accesshistory.utils.RequestCachingForFilterWrapper;
import com.spatialid.app.common.exception.subexception.AccessDeniedException;
import com.spatialid.app.common.exception.subexception.AccessHistoryUpdateException;
import com.spatialid.app.common.exception.subexception.UriNotFoundException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * アクセス履歴を記録するフィルタークラス．
 * 
 * @author matsumoto kentaro
 * @version 1.1 2024/11/21
 */
@Component
public class AccessHistoryFilter extends OncePerRequestFilter {
    
    /**
     * 委譲されたフィルター処理を行うサービスクラス．
     */
    private final IFilterDeligateService filterDeligateService;
    
    /**
     * アクセス履歴登録のエラーレスポンス書き込みを行うサービスクラス．
     */
    private final IExceptionHandleService exceptionHandleService;
    
    public AccessHistoryFilter(IFilterDeligateService filterDeligateService,
            IExceptionHandleService exceptionHandleService) {
        
        this.filterDeligateService = filterDeligateService;
        this.exceptionHandleService = exceptionHandleService;
        
    }
    
    /**
     * アクセス履歴機能を実装する．<br>
     * AOPによるロギングを行うため、処理のほとんどを{@link FilterDeligateServiceImpl}に委譲する．<br>
     * <p>
     * 主にリクエスト・レスポンスのラップとエラーハンドリングを行う．<br>
     * <br>
     * エラーハンドリング：<br>
     * 　アクセス履歴登録処理から404でレスポンスが返却された場合、APIの処理に進まずにフィルターからエラーレスポンスを返却する．<br>
     * 　アクセス履歴登録処理で例外が発生した場合、APIの処理に進まずにフィルターからエラーレスポンスを返却する．<br>
     *  アクセス履歴登録処理でアクセス拒否例外が発生した場合、APIの処理に進まずにフィルターからエラーレスポンスを返却する．<br>
     * 　アクセス履歴更新処理で例外が発生した場合、APIから返却されたレスポンスをそのまま返却する．
     * 
     * </p>
     * 
     * @param request リクエスト
     * @param response レスポンス
     * @param filterChain {@link FilterChain}
     * @throws IOException
     * @throws ServletException
     */
    @Override
    public void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws IOException, ServletException {
        
        final RequestCachingForFilterWrapper requestWrapper = new RequestCachingForFilterWrapper(request);
        
        final ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);
        
        try {
            
            // リクエスト・レスポンスに対するフィルター処理を委譲する．
            filterDeligateService.doFilterInternal(requestWrapper, responseWrapper, filterChain);
            
        } catch (UriNotFoundException e) {
            
            // 存在しないリクエストuriでアクセスを試みた場合、エラーレスポンスを返却する．
            exceptionHandleService.writeErrorResponse(responseWrapper, HttpStatus.NOT_FOUND);
            
            responseWrapper.copyBodyToResponse();
            
        } catch (AccessHistoryUpdateException e) {
            
            // アクセス履歴登録(更新)に失敗した場合、レスポンスをそのまま返却する．
            responseWrapper.copyBodyToResponse();
            
        } catch (AccessDeniedException e) {
            
            exceptionHandleService.writeErrorResponse(responseWrapper, HttpStatus.FORBIDDEN);
            
            responseWrapper.copyBodyToResponse();
            
        } catch (Exception e) {
            
            exceptionHandleService.writeErrorResponse(responseWrapper, HttpStatus.INTERNAL_SERVER_ERROR);
            
            // アクセス履歴登録に失敗した場合、エラーレスポンスを返却する．
            responseWrapper.copyBodyToResponse();
            
        }
        
    }
    
    /**
     * フィルターを適用可否を制御する．
     * <p>
     * リクエストオブジェクトから取得したURIと、あらかじめ定義された非適用リストと突き合わせて適用可否を判断する．<br>
     * フィルターの適用を行わないURIパターンは{@link AccessHistoryConstants#EXCLUDE_URI_PATTERN}に定義される．
     * </p>
     * 
     * @param request リクエスト
     */
    @Override
    public boolean shouldNotFilter(HttpServletRequest request) {
        
        return AccessHistoryConstants.EXCLUDE_URI_PATTERN
                .stream()
                .anyMatch(p -> new AntPathMatcher().match(p, request.getRequestURI()));
        
    }
        
}
