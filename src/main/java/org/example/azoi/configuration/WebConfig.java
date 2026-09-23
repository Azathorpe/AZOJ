package org.example.azoi.configuration;

import org.example.azoi.utils.JwtInterceptor;
import org.example.azoi.utils.anno.CurrentUserResolver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    private final JwtInterceptor jwtInterceptor;
    private final CurrentUserResolver currentUserResolver;

    @Value("${azoi.storage.root}")
    private String rootPath;

    @Value("${azoi.storage.avatar-dir}")
    private String avatarDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/avatars/**")
                .addResourceLocations("file:" + rootPath + "/" + avatarDir + "/")
                .setCacheControl(CacheControl.maxAge(7, TimeUnit.DAYS));
    }

    public WebConfig(JwtInterceptor jwtInterceptor, CurrentUserResolver currentUserResolver) {
        this.jwtInterceptor = jwtInterceptor;
        this.currentUserResolver = currentUserResolver;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/user/login", "/user/register");
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(currentUserResolver);
    }
}