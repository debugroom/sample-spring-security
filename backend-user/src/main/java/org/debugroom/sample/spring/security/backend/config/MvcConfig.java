package org.debugroom.sample.spring.security.backend.config;

import org.debugroom.sample.spring.security.backend.app.web.interceptor.BackendAuditLoggingInterceptor;
import org.debugroom.sample.spring.security.common.apinfra.exception.CommonExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@ComponentScan("org.debugroom.sample.spring.security.backend.app.web")
@Configuration
public class MvcConfig implements WebMvcConfigurer {

    @Bean
    CommonExceptionHandler commonExceptionHandler(){
        return new CommonExceptionHandler();
    }

    @Bean
    BackendAuditLoggingInterceptor backendAuditLoggingInterceptor(){
        return new BackendAuditLoggingInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(backendAuditLoggingInterceptor());
    }

}
