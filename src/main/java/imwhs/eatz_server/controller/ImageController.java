package imwhs.eatz_server.controller;

import imwhs.eatz_server.common.EatzUserAuthUtil;
import imwhs.eatz_server.dto.ApiResponse;
import imwhs.eatz_server.service.ImageService;
import imwhs.eatz_server.service.image.ImageStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v0/uploads/images")
@Controller
public class ImageController {

    private final ImageService imageService;

    /**
     * 프로필 이미지로 사용할 파일을 업로드합니다.
     * @param file 업로드하려는 파일.
     * @return 업로드된 파일의 경로를 포함하는 ApiResponse.
     */
    @PostMapping("/profiles")
    public ResponseEntity<ApiResponse<String>> uploadProfileImage(@RequestParam("file") MultipartFile file) {
        String result = imageService.saveProfileImage(file);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @GetMapping("/uploads/images/{imageName}")
    public ResponseEntity<ApiResponse<String>> getImage(@PathVariable String folder, @PathVariable String imageName) {
        log.info("{} 디렉터리의 {} 이미지를 갖고올게요!", folder, imageName);
//        String imagePath = imageStorageService.getImagePath(folder, imageName);
        return ResponseEntity.ok(ApiResponse.success(""));
    }

}
