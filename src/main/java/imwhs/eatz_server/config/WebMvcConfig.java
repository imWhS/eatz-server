package imwhs.eatz_server.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Slf4j
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Value("${eatz.local-storage-directory}")
    private String localStorageDirectory;

    @Value("${eatz.storage.base-directory}")
    private String storageBaseDirectory;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new PageableValidationHandlerMethodArgumentResolver());
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/api/v0/" + storageBaseDirectory + "/**")
                .addResourceLocations(localStorageDirectory + storageBaseDirectory + "/");
    }

}
