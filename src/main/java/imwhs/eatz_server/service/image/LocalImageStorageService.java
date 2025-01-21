package imwhs.eatz_server.service.image;

import imwhs.eatz_server.common.EatzUserAuthUtil;
import imwhs.eatz_server.config.properties.EatzProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class LocalImageStorageService implements ImageStorageService {

    private final EatzProperties eatzProperties;

    @Override
    public String uploadProfileImage(MultipartFile file) {
        return save(file, "profile", EatzUserAuthUtil.getUsername());
    }

    @Override
    public String save(MultipartFile file, String directory, String fileName) {
        Path directoryPath = Paths.get(eatzProperties.getBaseDirectory(), directory);

        try {
            if (!Files.exists(directoryPath)) {
                Files.createDirectories(directoryPath);
            }

            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null) {
                throw new RuntimeException("파일 이름이 유효하지 않아요.");
            }

            // 원본 파일 이름에서 확장자를 추출합니다.
            String extension = getExtension(file);

            String extensionFileName = fileName + extension;
            Path filePath = directoryPath.resolve(extensionFileName);
            file.transferTo(filePath.toFile());

            log.info("{} 이미지를 {}에 저장했어요.", file.getOriginalFilename(), filePath);
            return null;
        } catch (IOException e) {
            throw new RuntimeException("이미지 업로드를 실패했어요: " + e.getLocalizedMessage());
        }
    }

    /**
     * 로컬 파일 시스템의 특정 디렉터리에 이미지를 저장합니다.
     * @param file 저장하려는 이미지
     * @param directory 이미지를 저장하려는 디렉터리
     * @return 업로드된 이미지의 URL. 서버 외부에서 클라이언트가 접근할 수 있는 경로에 해당합니다.
     */
//    @Override
    public String upload_old(MultipartFile file, String directory) {
        System.out.println("LocalImageStorageService.upload - 현재 사용자 이메일 주소: " + EatzUserAuthUtil.getEmail());
        // 업로드 요청받은 이미지를 저장하기 위한 디렉터리의 경로를 구성합니다.
        Path directoryPath = Paths.get(eatzProperties.getBaseDirectory(), directory); // baseDirectory + directory
        log.info("directoryPath: {}", directoryPath);

        try {
            // 경로를 구성하기 위한 디렉터리가 존재하지 않는다면, 필요한 디렉터리를 모두 생성합니다.
            if (!Files.exists(directoryPath)) {
                Files.createDirectories(directoryPath);

            }

            // 이미지의 원본 파일 이름을 가져옵니다.
            String originalFilename = file.getOriginalFilename();

            if (originalFilename == null) {
                throw new RuntimeException("파일 이름이 유효하지 않아요.");
            }

            // 원본 파일 이름에서 확장자를 추출합니다.
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            if (extension.isEmpty()) {
                throw new RuntimeException("파일 유형이 유효하지 않아요.");
            }

            // UUID를 이용해 서버에 업로드하려는 이미지에 대한 고유한 이름을 생성합니다.
            String fileName = UUID.randomUUID() + extension;
            log.info("로컬 스토리지에 새 이미지 파일을 저장할게요. fileName: {}, originalFileName: {}, directory: {}", fileName, originalFilename, directory);

            // 로컬 파일 시스템에 이미지를 실제로 저장하기 위한 경로를 구성합니다.
            Path filePath = directoryPath.resolve(fileName);
            log.info("filePath: {}", filePath);

            // 로컬 파일 시스템에 파일을 저장합니다.
            file.transferTo(filePath.toFile());

            // 업로드한 파일의 URL 주소를 반환합니다.
            return "/uploads/" + directory + "/" + fileName;
        } catch (Exception e) {
            throw new RuntimeException("이미지 업로드를 실패했어요: " + e.getLocalizedMessage());
        }
    }

    /**
     * 로컬 저장소의 특정 이미지에 접근할 수 있는 URI 주소를 가져옵니다.
     * @param directory 이미지가 저장되어 있는 디렉터리
     * @param imageName 이미지 이름
     * @return 이미지 URI 주소
     */
    @Override
    public String getImagePath(String directory, String imageName) {
        Path imagePath = Paths.get(eatzProperties.getBaseDirectory(), directory, imageName);
        return imagePath.toUri().toString();
    }

    /**
     * 파일의 확장자를 가져옵니다.
     */
    private String getExtension(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();

        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));

        if (extension.isEmpty()) {
            throw new RuntimeException("파일 유형이 유효하지 않아요.");
        }

        return extension;
    }

}
