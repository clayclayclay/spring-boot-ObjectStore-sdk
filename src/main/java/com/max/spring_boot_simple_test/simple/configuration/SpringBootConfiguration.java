package com.max.spring_boot_simple_test.simple.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import com.max.spring_boot_simple_test.simple.exception.GlobalHandlerExceptionResolver;
import org.springframework.context.annotation.CommonAnnotationBeanPostProcessor;

@Configuration
public class SpringBootConfiguration {
    
    
    @Bean(name = "multipartResolver")
    public MultipartResolver mutipartResolver() {
        CommonsMultipartResolver com = new CommonsMultipartResolver();
        com.setDefaultEncoding("utf-8");
        return com;
    }
    
    @Bean(name = "globalHandlerExceptionResolver")
    public GlobalHandlerExceptionResolver GlobalHandlerExceptionResolver() {
        return new GlobalHandlerExceptionResolver();
    }
}
