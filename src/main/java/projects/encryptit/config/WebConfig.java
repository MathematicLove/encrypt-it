package projects.encryptit.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Конфигурация веб-приложения.
 * WebClientConfig не нужен - мы используем RestTemplate + WebFlux для реактивности
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * Бин RestTemplate для синхронных HTTP запросов.
     * @return RestTemplate
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    /**
     * Настройка обработчиков ресурсов.
     * @param registry реестр обработчиков ресурсов
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Стандартные пути Spring Boot (без /static/)
        registry.addResourceHandler("/css/**", "/js/**", "/images/**")
                .addResourceLocations("classpath:/static/css/", "classpath:/static/js/", "classpath:/static/images/");
        // Альтернативный путь с /static/ для совместимости
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/");
        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
    }
}