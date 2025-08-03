package id.co.awan.tap2pay.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuration for anything about Web MVC
 *
 * @author yukenz
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        WebMvcConfigurer.super.configureContentNegotiation(configurer);
        configurer
                .favorParameter(false)
                .ignoreAcceptHeader(true)
                .defaultContentType(org.springframework.http.MediaType.APPLICATION_JSON);
    }

    /**
     * Allow all CORS requests
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        WebMvcConfigurer.super.addCorsMappings(registry);
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("*")
                .allowedHeaders("*")
                .allowCredentials(true);

    }
}