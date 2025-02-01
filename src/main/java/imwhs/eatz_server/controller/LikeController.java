package imwhs.eatz_server.controller;

import imwhs.eatz_server.domain.likes.LikesType;
import imwhs.eatz_server.dto.ApiResponse;
import imwhs.eatz_server.dto.likes.LikesDetailDto;
import imwhs.eatz_server.dto.likes.LikesDto;
import imwhs.eatz_server.dto.likes.LikesRequestDto;
import imwhs.eatz_server.service.LikeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v0/likes")
@RestController
public class LikeController {

    private final LikeService likeService;

    @PostMapping
    public ResponseEntity<ApiResponse<LikesDto>> toggleLikeOf(@RequestBody LikesRequestDto dto) {
        LikesDto likesDto = likeService.toggleLikeOf(dto.getEntityId(), dto.getType());
        return ResponseEntity.ok(ApiResponse.success(likesDto));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Boolean>> isLikedByUser(
            @RequestParam Long userId,
            @RequestParam Long entityId,
            @RequestParam LikesType type) {
        return ResponseEntity.ok(
                ApiResponse.success(likeService.isLikedByUser(userId, entityId, type)));
    }

    @GetMapping("/details")
    public ResponseEntity<ApiResponse<LikesDetailDto>> getLikeDetails(@RequestParam Long entityId,
                                                                      @RequestParam LikesType type) {
        LikesDetailDto dto = likeService.getLikeDetails(entityId, type);
        return ResponseEntity.ok(ApiResponse.success(dto));
    }

}
