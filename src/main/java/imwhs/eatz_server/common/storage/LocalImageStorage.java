package imwhs.eatz_server.common.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * LocalImageStorage 클래스입니다.
 * 이미지 파일 저장소로 로컬 파일 시스템을 사용합니다.
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class LocalImageStorage implements ImageStorage {

    // 현재 애플리케이션이 실행되고 있는 디렉터리 경로.
    private final String applicationDirectory = System.getProperty("user.dir");

    @Value("${eatz.storage.base-directory}")
    private String storageBaseDirectory;

    @Value("${eatz.storage.image.base-directory}")
    private String imageBaseDirectory;

    /**
     * {@inheritDoc}
     */
    @Override
    public String save(String directory, String fileName, MultipartFile file) {
        // 애플리케이션 루트 디렉터리입니다.
        Path rootPath = Paths.get(applicationDirectory);

        // 이미지 타입의 파일을 저장하기 위한 파일 시스템 내 베이스 경로입니다.
        Path baseDirectoryPath = Paths.get(storageBaseDirectory, imageBaseDirectory);

        // 파일이 저장될 디렉터리의 상대 경로입니다. ex) uploads/images/profiles
        Path targetDirectoryPath = baseDirectoryPath.resolve(directory);

        // 이미지의 실제 저장 경로입니다. 애플리케이션 루트 디렉터리와 이미지가 저장될 디렉터리의 상대 저장 경로를 결합합니다.
        Path saveDirectoryPath = rootPath.resolve(targetDirectoryPath);

        // 저장할 파일 이름을 포함한 전체 경로입니다. 실제 파일 시스템 저장을 위해 사용합니다.
        Path filePath = saveDirectoryPath.resolve(fileName);

        // 클라이언트가 저장한 이미지를 요청하기 위해 필요한 상대 경로입니다.
        Path requestPath = targetDirectoryPath.resolve(fileName);

        try {
            if (!Files.exists(filePath)) {
                log.info("{}를 저장하려는 {}는 존재하지 않는 디렉터리입니다. 필요한 디렉터리를 생성합니다.", fileName, saveDirectoryPath);
                Files.createDirectories(saveDirectoryPath);
            }
            file.transferTo(filePath.toFile());
            return requestPath.toString();
        } catch (IOException e) {
            log.error("이미지 저장하지 못했어요: {}", e.getMessage());
            throw new RuntimeException("이미지 업로드를 실패했어요: " + e.getLocalizedMessage());
        }
    }
}