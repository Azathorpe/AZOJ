package org.example.azoi.configuration;

import org.example.azoi.utils.JwtInterceptor;
import org.example.azoi.utils.anno.CurrentUserResolver;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    private final JwtInterceptor jwtInterceptor;
    private final CurrentUserResolver currentUserResolver;

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