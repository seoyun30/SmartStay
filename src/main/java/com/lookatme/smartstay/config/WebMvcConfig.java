package com.lookatme.smartstay.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Value("${uploadPath}") //file:///C:/shop/
    String uploadPath;  //uploadPath = file:///C:/shop/

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        registry.addResourceHandler("/images/**", "/css/**")
                .addResourceLocations(uploadPath, "classpath:/static/css/");
    }
}
