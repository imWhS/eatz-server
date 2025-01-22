package imwhs.eatz_server.service;

import imwhs.eatz_server.auth.EatzUserAuthUtil;
import imwhs.eatz_server.common.storage.ImageStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImageService {

    private final ImageStorage imageStorage;

    /**
     * 파일을 프로필 이미지로 업로드합니다.
     * @param file 업로드하려는 파일.
     * @return 업로드된 파일의 경로.
     */
    public String uploadProfileImage(MultipartFile file) {
        log.info("HTTP 요청 데이터로 전송 받은 이미지({})를 저장합니다.", file.getOriginalFilename());
        return imageStorage.save(
                "profiles",
                EatzUserAuthUtil.getUsername() + getExtension(file),
                file);
    }

    /**
     * 파일을 프로필 이미지로 업로드합니다.
     * @param username 프로필 이미지를 업데이트할 사용자의 사용자 이름.
     * @param file 업로드하려는 파일.
     * @return 업로드된 파일의 경로.
     */
    public String uploadProfileImage(String username, MultipartFile file) {
        log.info("이미지({})를 {} 사용자의 프로필 이미지로 저장합니다.", file.getOriginalFilename(), username);
        return imageStorage.save(
                "profiles",
                username + getExtension(file),
                file);
    }

    /**
     * 이미지 베이스 디렉터리 하위 계층에 저장된 이미지를 삭제합니다.
     * @param filePath 삭제할 이미지가 저장되어 있는 경로. 일반적으로 uploads/images/로 시작합니다.
     */
    public void deleteImage(String filePath) {
        imageStorage.delete(filePath);
    }

    /**
     * 파일의 확장자를 가져옵니다.
     * @param file 확장자를 추출할 파일.
     * @return 파일의 확장자.
     * @throws RuntimeException 파일 이름이 유효하지 않거나 확장자가 없는 경우.
     */
    private String getExtension(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.trim().isEmpty()) {
            throw new RuntimeException("파일 이름이 유효하지 않아요.");
        }

        int dotIndex = originalFilename.lastIndexOf(".");
        if (dotIndex == -1 || dotIndex == originalFilename.length() - 1) {
            throw new RuntimeException("파일 확장자가 유효하지 않아요.");
        }

        return originalFilename.substring(dotIndex);
    }

}
