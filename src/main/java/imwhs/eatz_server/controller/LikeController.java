package imwhs.eatz_server.controller;

import imwhs.eatz_server.domain.LikesType;
import imwhs.eatz_server.dto.ApiResponse;
import imwhs.eatz_server.dto.LikeDetailDto;
import imwhs.eatz_server.dto.LikeDto;
import imwhs.eatz_server.dto.LikeRequestDto;
import imwhs.eatz_server.service.LikeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v0/likes")
@RestController
public class LikeController {

    private final LikeService likeService;

    @PostMapping
    public ResponseEntity<ApiResponse<LikeDto>> toggleLikeOf(@RequestBody LikeRequestDto dto) {
        LikeDto likeDto = likeService.toggleLikeOf(dto.getEntityId(), dto.getType());
        return ResponseEntity.ok(ApiResponse.success(likeDto));
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
    public ResponseEntity<ApiResponse<LikeDetailDto>> getLikeDetails(@RequestParam Long entityId,
                                                      @RequestParam LikesType type) {
        LikeDetailDto dto = likeService.getLikeDetails(entityId, type);
        return ResponseEntity.ok(ApiResponse.success(dto));
    }

}
