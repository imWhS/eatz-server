package imwhs.eatz_server.storage;

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

    // 현재 애플리케이션이 실행되고 있는 디렉터리
    private final String applicationDirectory = System.getProperty("user.dir");

    // 저장소 베이스 디렉터리
    @Value("${eatz.storage.base-directory}")
    private String storageBaseDirectory;

    /**
     * {@inheritDoc}
     */
    @Override
    public String save(String filePath, MultipartFile file) {
        // 애플리케이션 루트 디렉터리입니다.
        Path rootPath = Paths.get(applicationDirectory);

        // 애플리케이션 루트 + 베이스 디렉터리(uploads) + 전달받은 파일 경로(image/user_profiles/xxx.png)
        Path saveFilePath = rootPath.resolve(Paths.get(storageBaseDirectory, filePath));

        // 저장할 파일이 위치할 디렉터리
        Path saveDirectoryPath = saveFilePath.getParent();

        try {
            if (!Files.exists(saveDirectoryPath)) {
                log.info("{} 파일 저장을 위해 디렉터리를 생성할게요: {}", file.getOriginalFilename(), saveDirectoryPath);
                Files.createDirectories(saveDirectoryPath);
            }
            file.transferTo(saveFilePath.toFile());
            return filePath;
        } catch (IOException e) {
            log.error("이미지를 저장하지 못했어요: {}", e.getMessage());
            throw new RuntimeException("이미지 업로드를 실패했어요: " + e.getLocalizedMessage());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(String file) {
        Path rootPath = Paths.get(applicationDirectory);
        Path filePath = rootPath.resolve(Paths.get(file));
        log.info("로컬 파일 시스템에서 {}을 삭제할게요. ({})", file, filePath);

        try {
            if (Files.exists(filePath)) {
                Files.delete(filePath);
                log.info("로컬 파일 시스템에서 '{}' 파일을 삭제했어요. ({})", file, filePath);
            } else {
                log.info("로컬 파일 시스템에서 '{}' 파일이 없어서 삭제하지 못했어요. ({})", file, filePath);
            }
        } catch (IOException e) {
            log.error("'{}' 파일을 삭제하는 도중 오류가 발생했어요: {}", filePath, e.getMessage());
            throw new RuntimeException("이미지 삭제 처리를 실패했어요: " + e.getLocalizedMessage());
        }
    }

}