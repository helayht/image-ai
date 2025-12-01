package com.yht.image.ai.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${upload.path}")
    private String uploadPath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String location = uploadPath.endsWith("/") ? uploadPath : uploadPath + "/";
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + location);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册声明的拦截器
        registry.addInterceptor(new JWTInterceptor())
                .addPathPatterns("/image/ai/api/chat/**")      // 对数据获取接口进行拦截
                .addPathPatterns("/image/ai/api/message/**")
                .excludePathPatterns("/image/ai/api/auth/**")  // 放行 登录接口
                .excludePathPatterns("/image/ai/wx/portal/**");

    }

}
