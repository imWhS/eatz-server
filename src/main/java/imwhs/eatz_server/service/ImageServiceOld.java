package imwhs.eatz_server.service;

import imwhs.eatz_server.storage.ImageStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ImageServiceOld {

    private final ImageStorage imageStorage;

//    /**
//     * 파일을 프로필 이미지로 업로드합니다.
//     * <ul>
//     *     <li> 업로드 이미지는 "profile_image_{사용자 이름}_확장자" 형식으로 새 파일 이름이 부여됩니다. </li>
//     * </ul>
//     * @param file 업로드하려는 파일
//     * @param category 업로드하려는 파일의 이미지 카테고리
//     * @return 업로드된 파일의 경로
//     */
//    @Transactional(rollbackFor = Exception.class)
//    public String uploadImage(MultipartFile file, ImageStorageCategory category) {
//        log.info("HTTP 요청 이미지({})를 {} 카테고리로 저장합니다.", file.getOriginalFilename(), category.name());
//
//        String extension = extractExtension(file);
//        String filename = category.getFileNamePrefix() + UUID.randomUUID().toString() + extension;
//        String path = category.getPath() + filename;
//    }
//
//    /**
//     * 파일을 프로필 이미지로 업로드합니다.
//     * @param username 프로필 이미지를 업데이트할 사용자의 사용자 이름
//     * @param file 업로드하려는 파일
//     * @return 업로드된 파일의 경로
//     */
//    @Transactional(rollbackFor = Exception.class)
//    public String uploadProfileImage(String username, MultipartFile file) {
//        log.info("이미지({})를 {} 사용자의 프로필 이미지로 저장합니다.", file.getOriginalFilename(), username);
//        return imageStorage.save(
//                ImageStorageCategory.USER_PROFILE.getPath(),
//                username + extractExtension(file),
//                file);
//    }

    /**
     * 이미지 베이스 디렉터리 하위 계층에 저장된 이미지를 삭제합니다.
     * @param filePath 삭제할 이미지가 저장되어 있는 경로. 일반적으로 uploads/images/로 시작합니다.
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteImage(String filePath) {
        imageStorage.delete(filePath);
    }

    /**
     * 파일의 확장자를 추출합니다.
     * @param file 확장자를 추출할 파일
     * @return 파일의 확장자
     * @throws RuntimeException 파일의 이름이 유효하지 않거나 확장자가 없는 경우
     */
    private String extractExtension(MultipartFile file) {
        String filename = file.getOriginalFilename();
        if (filename == null || filename.isBlank()) {
            throw new RuntimeException("파일의 이름이 유효하지 않아요.");
        }

        int dotIndex = filename.lastIndexOf(".");
        if (dotIndex == -1 || dotIndex == filename.length() - 1) {
            throw new RuntimeException("파일의 확장자가 유효하지 않아요.");
        }

        return filename.substring(dotIndex);
    }

}
