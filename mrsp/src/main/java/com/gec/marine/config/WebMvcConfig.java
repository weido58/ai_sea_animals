package com.gec.marine.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")  // 允许所有路径
                .allowedOriginPatterns("*") // 允许所有来源（Spring Boot 2.4+ 用这个）
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // 允许方法
                .allowedHeaders("*") // 允许所有请求头
                .allowCredentials(true) // 允许携带cookie
                .maxAge(3600); // 预检请求缓存时间
    }
}
