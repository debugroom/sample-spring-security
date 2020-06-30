package org.debugroom.sample.spring.security.management.config;

import org.debugroom.sample.spring.security.management.app.web.interceptor.SetMenuInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@ComponentScan("org.debugroom.sample.spring.security.management.app.web")
@Configuration
public class MvcConfig implements WebMvcConfigurer {

    @Bean
    SetMenuInterceptor setMenuInterceptor(){
        return new SetMenuInterceptor();
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/");
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(setMenuInterceptor());
    }
}
