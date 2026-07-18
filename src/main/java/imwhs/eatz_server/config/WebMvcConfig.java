package imwhs.eatz_server.config;

import imwhs.eatz_server.resolver.AuthenticatedEatzUserIdArgumentResolver;
import imwhs.eatz_server.resolver.PageableValidationHandlerMethodArgumentResolver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final AuthenticatedEatzUserIdArgumentResolver authenticatedEatzUserIdArgumentResolver;
    private final PageableValidationHandlerMethodArgumentResolver pageableValidationHandlerMethodArgumentResolver;

    /**
     * 서버가 위치한 환경의 로컬 저장소(storage) 디렉토리의 절대 경로. 루트를 포함해야 합니다.
     * Ex. /Users/wonhee/eatz_workspace/
     */
    @Value("${eatz.storage.root-directory-path}")
    private String storageRootDirectoryPath;

    /**
     * 서버가 위치한 환경의 로컬 저장소(storage)에 업로드된 파일이 저장될 디렉토리의 이름
     * Ex. uploads
     */
    @Value("${eatz.storage.base-directory}")
    private String storageBaseDirectory;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 서버가 위치한 로컬 저장소에 저장된 정적 리소스를 요청받았을 때, 리소스로서 serving 할 파일이 위치한 로컬 저장소의 절대 경로로
        // 연결(mapping)할 Resource Handler를 설정 및 추가합니다.
        // 서버가 실행 중인 로컬 또는 물리 서버의 로컬 저장소 디렉토리에 위치한 파일을 serving할 때만 사용됩니다.
        // Ex. 클라이언트가 "http://localhost:8080/uploads/어쩌구저쩌구" URI로 정적 리소스를 GET 요청하면,
        //     Resource Handler가 "file:///Users/wonhee/eatz_workspace/uploads/" 디렉토리에서
        //     바로 정적 리소스를 serving 할 수 있게 연결합니다.
        String locationUri = Paths.get(storageRootDirectoryPath, storageBaseDirectory).toUri().toString();

        registry.addResourceHandler("/" + storageBaseDirectory + "/**")
                .addResourceLocations(locationUri);
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        // 기본 Pageable보다 높은 우선 순위를 부여합니다.
        resolvers.add(0, pageableValidationHandlerMethodArgumentResolver);

        resolvers.add(authenticatedEatzUserIdArgumentResolver);
    }
}
