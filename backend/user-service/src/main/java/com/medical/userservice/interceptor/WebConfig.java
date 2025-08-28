package com.medical.userservice.interceptor;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    private final DownstreamAuthInterceptor interceptor;
    public WebConfig(DownstreamAuthInterceptor i) { this.interceptor = i; }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(interceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/actuator/**", "/public/**"); // keep health open
    }
}
