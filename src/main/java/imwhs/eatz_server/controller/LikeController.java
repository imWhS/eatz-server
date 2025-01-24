package imwhs.eatz_server.controller;

import imwhs.eatz_server.domain.LikesType;
import imwhs.eatz_server.dto.ApiResponse;
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

    /*
    좋아요 버튼을 누르는 상황
    0. 기본적으로 좋아요는 인증된 사용자만 가능하기 때문에, username 같은 사용자 정보는 SecurityContext에서 조회 가능
    1. 레시피 화면 또는 레시피 목록에서 레시피 항목의 '좋아요' 버튼 탭 - 레시피 id, 레시피를 등록한 사용자 username
    2. 레시피 댓글 목록에서 댓글 항목의 '좋아요' 버튼 탭 - 댓글 id, 댓글을 남긴 사용자 username
     */
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

}
