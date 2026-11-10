package com.sleepcare.server.common.config;

import com.sleepcare.server.common.owner.OwnerIdInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final OwnerIdInterceptor ownerIdInterceptor;

    public WebConfig(OwnerIdInterceptor ownerIdInterceptor) {
        this.ownerIdInterceptor = ownerIdInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(ownerIdInterceptor).addPathPatterns("/api/v1/**");
    }
}
