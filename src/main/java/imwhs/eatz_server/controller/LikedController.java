package imwhs.eatz_server.controller;

import imwhs.eatz_server.domain.liked.LikedType;
import imwhs.eatz_server.dto.ApiResponse;
import imwhs.eatz_server.dto.liked.LikedDetailDto;
import imwhs.eatz_server.dto.liked.LikedDto;
import imwhs.eatz_server.dto.liked.LikedRequestDto;
import imwhs.eatz_server.service.LikedService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v0/liked")
@RestController
public class LikedController {

    private final LikedService likedService;

    @PostMapping
    public ResponseEntity<ApiResponse<LikedDto>> toggleLikeOf(@RequestBody LikedRequestDto dto) {
        LikedDto likedDto = likedService.toggleLikeOf(dto.getEntityId(), dto.getType());
        return ResponseEntity.ok(ApiResponse.success(likedDto));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Boolean>> isLikedByUser(
            @RequestParam Long userId,
            @RequestParam Long entityId,
            @RequestParam LikedType type) {
        return ResponseEntity.ok(
                ApiResponse.success(likedService.isLikedByUser(userId, entityId, type)));
    }

    @GetMapping("/details")
    public ResponseEntity<ApiResponse<LikedDetailDto>> getLikedDetails(@RequestParam Long entityId,
                                                                       @RequestParam LikedType type) {
        LikedDetailDto dto = likedService.getLikedDetails(entityId, type);
        return ResponseEntity.ok(ApiResponse.success(dto));
    }

}
