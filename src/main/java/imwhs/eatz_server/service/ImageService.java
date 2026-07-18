package imwhs.eatz_server.service;

import imwhs.eatz_server.ImageCategory;
import imwhs.eatz_server.storage.FileStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class ImageService {

    private final FileStorage fileStorage;

    /**
     * 이미지를 업로드합니다.
     * @param image 이미지
     * @param category 이미지 카테고리
     * @return 업로드된 이미지의 URI(URL).
     *         Ex. "uploads/images/users/profiles/profile_123.png"
     */
    public String upload(MultipartFile image, ImageCategory category) {
        String contentType = image.getContentType();

        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("파일의 유형이 " + contentType + "이에요. 이미지 파일만 업로드할 수 있어요. ");
        }

        if (image.isEmpty() || image.getSize() == 0) {
            throw new IllegalArgumentException("업로드하려는 이미지가 유효하지 않아요.");
        }

        String imageName = image.getOriginalFilename();
        String imagePath = category.getPath() + generateAnonymisedImageName(imageName);
        return fileStorage.save(imagePath, image);
    }

    /**
     * URI에 해당하는 이미지를 삭제합니다.
     * @param imageUri 삭제할 이미지의 URI(URL).
     *                  Ex. "uploads/images/users/profiles/profile_123.png"
     */
    public void delete(String imageUri) {
        if (imageUri.isBlank()) { throw new IllegalArgumentException("삭제하려는 이미지의 URI(URL)이 유효하지 않아요."); }
        fileStorage.deleteByUri(imageUri);
    }

    private String generateAnonymisedImageName(String imageName) {
        String anonymisedImageName = UUID.randomUUID().toString();
        int extensionIndex = imageName.lastIndexOf(".");
        String extension;
        if (extensionIndex == -1) {
            // 확장자가 없는 경우
            return anonymisedImageName;
        }
        else {
            extension = imageName.substring(extensionIndex);
            return anonymisedImageName + extension;
        }
    }

}
