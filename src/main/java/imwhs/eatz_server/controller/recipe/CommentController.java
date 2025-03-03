package imwhs.eatz_server.controller.recipe;

import imwhs.eatz_server.dto.ApiResponse;
import imwhs.eatz_server.dto.Paged;
import imwhs.eatz_server.dto.comment.CommentItemDto;
import imwhs.eatz_server.dto.comment.CommentCreateDto;
import imwhs.eatz_server.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * 레시피의 댓글을 관리하기 위한 API를 제공하는 컨트롤러입니다.
 */
@RequestMapping("/api/v0/recipes/{id}/comments")
@RequiredArgsConstructor
@Controller
public class CommentController {

    private final CommentService commentService;

    /**
     * 레시피에 새 댓글을 등록합니다.
     * @param id 레시피의 ID.
     * @param dto 등록하려는 댓글 관련 정보.
     * @return 생성된 댓글의 ID.
     */
    @PostMapping
    public ResponseEntity<ApiResponse<Long>> addComment(@PathVariable Long id, @RequestBody CommentCreateDto dto) {
        Long commentId = commentService.register(id, dto.getContent());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(commentId));
    }

    /**
     * 레시피에 추가된 모든 댓글 목록을 조회합니다.
     * @param id 레시피의 ID.
     * @param pageable 페이징 정보.
     * @return 댓글 목록 정보.
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Paged<CommentItemDto>>> findComments(
            @PathVariable Long id,
            @PageableDefault(page = 0, size = 10) Pageable pageable) {
        Page<CommentItemDto> comments = commentService.findAllByRecipe(id, pageable);
        return ResponseEntity.ok(ApiResponse.success(comments));
    }

}
