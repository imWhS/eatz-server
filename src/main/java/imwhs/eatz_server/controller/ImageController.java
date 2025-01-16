package imwhs.eatz_server.controller;

import imwhs.eatz_server.dto.ApiResponse;
import imwhs.eatz_server.service.image.ImageStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v0/images")
@Controller
public class ImageController {

    private final ImageStorageService imageStorageService;

    @PostMapping("/profile")
    public ResponseEntity<ApiResponse<String>> uploadProfileImage(@RequestParam("file") MultipartFile file) {
        String imageUrl = imageStorageService.upload(file, "profile-images");
        return ResponseEntity.ok(ApiResponse.success(imageUrl));
    }

    @GetMapping("/uploads/{folder}/{imageName}")
    public ResponseEntity<ApiResponse<String>> getImage(@PathVariable String folder, @PathVariable String imageName) {
        log.info("{} 디렉터리의 {} 이미지를 갖고올게요!", folder, imageName);
        String imageUrl = imageStorageService.getImagePath(folder, imageName);
        return ResponseEntity.ok(ApiResponse.success(imageUrl));
    }

}
