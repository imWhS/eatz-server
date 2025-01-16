package imwhs.eatz_server.service.image;

import imwhs.eatz_server.config.properties.EatzServerProperties;
import imwhs.eatz_server.config.properties.ImageStorageProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class LocalImageStorageService implements ImageStorageService {

    private final EatzServerProperties eatzServerProperties;

    private final ImageStorageProperties imageStorageProperties;

    @Override
    public String upload(MultipartFile file, String directory) {
        Path directoryPath = Paths.get(imageStorageProperties.getBaseDirectory(), directory); // baseDirectory + directory
        log.info("directoryPath: {}", directoryPath);

        try {
            // 경로를 구성하는 디렉터리가 존재하지 않는다면 필요한 디렉터리를 모두 생성합니다.
            if (!Files.exists(directoryPath)) {
                Files.createDirectories(directoryPath);
            }

            String originalFilename = file.getOriginalFilename();

            if (originalFilename == null) {
                throw new RuntimeException("파일 이름이 유효하지 않아요.");
            }

            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String fileName = UUID.randomUUID() + extension;
            log.info("로컬 스토리지에 새 이미지 파일을 저장할게요. fileName: {}, originalFileName: {}, directory: {}", fileName, originalFilename, directory);

            Path filePath = directoryPath.resolve(fileName);
            log.info("filePath: {}", filePath);
            file.transferTo(filePath.toFile());

            return "/uploads/" + directory + "/" + fileName;
        } catch (Exception e) {
            throw new RuntimeException("이미지 업로드를 실패했어요: " + e.getLocalizedMessage());
        }
    }

    @Override
    public String getImagePath(String folder, String imageName) {
        Path imagePath = Paths.get(imageStorageProperties.getBaseDirectory(), folder, imageName);
        return imagePath.toUri().toString();
    }

}
