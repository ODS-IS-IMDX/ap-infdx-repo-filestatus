// © 2025 NTT DATA Japan Co., Ltd. & NTT InfraNet All Rights Reserved.

package com.spatialid.app;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.spatialid.app.api.presentation.aspect.AccessControlInterceptor;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final AccessControlInterceptor accessControlInterceptor;

    public WebMvcConfig(AccessControlInterceptor accessControlInterceptor) {

        this.accessControlInterceptor = accessControlInterceptor;

    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(accessControlInterceptor);

    }

}