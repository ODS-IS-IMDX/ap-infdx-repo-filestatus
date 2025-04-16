// © 2025 NTT DATA Japan Co., Ltd. & NTT InfraNet All Rights Reserved.

package com.spatialid.app.common.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

import com.spatialid.app.common.accesshistory.filter.AccessHistoryFilter;

/**
 * フィルターを登録するクラス．
 * 
 * @author matsumoto kentaro
 * @version 1.1 2024/11/21
 */
@Configuration
public class FilterConfig {
    
    /**
     * アクセス履歴フィルター．
     */
    private final AccessHistoryFilter accessHistoryFilter;
    
    public FilterConfig(AccessHistoryFilter accessHistoryFilter) {
        
        this.accessHistoryFilter = accessHistoryFilter;
        
    }
    
    /**
     * アクセス履歴を記録するフィルターを登録する．
     * <p>
     * フィルターの適用URLパターンはブラックリスト形式で実装を行うため、本メソッドではあらゆるリクエストに対してフィルターを適用する．<br>
     * 除外パターンは{@link AccessHistoryFilter#shouldNotFilter(jakarta.servlet.http.HttpServletRequest)}で実装を行う．
     * </p>
     * 
     * @return アクセス履歴フィルターが設定された{@link FilterRegistrationBean}
     */
    @Bean
    public FilterRegistrationBean<AccessHistoryFilter> accessHistoryFilterBean() {
        
        final FilterRegistrationBean<AccessHistoryFilter> filterRegistrationBean = new FilterRegistrationBean<AccessHistoryFilter>();
        
        filterRegistrationBean.setFilter(accessHistoryFilter);
        
        filterRegistrationBean.addUrlPatterns("/*");
        
        filterRegistrationBean.setOrder(Ordered.LOWEST_PRECEDENCE);
        
        return filterRegistrationBean;
        
    }
    
}
