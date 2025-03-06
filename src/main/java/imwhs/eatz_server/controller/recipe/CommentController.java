package imwhs.eatz_server.controller.recipe;

import imwhs.eatz_server.auth.EatzUserAuthUtil;
import imwhs.eatz_server.domain.liked.EntityType;
import imwhs.eatz_server.dto.ApiResponse;
import imwhs.eatz_server.dto.CreateBasicReportDto;
import imwhs.eatz_server.dto.Paged;
import imwhs.eatz_server.dto.comment.CommentItemDto;
import imwhs.eatz_server.dto.comment.CreateCommentDto;
import imwhs.eatz_server.service.CommentService;
import imwhs.eatz_server.service.ReportService;
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
@RequestMapping("/api/v0/recipes/{recipeId}/comments")
@RequiredArgsConstructor
@Controller
public class CommentController {

    private final CommentService commentService;

    private final ReportService reportService;

    /**
     * 레시피에 새 댓글을 등록합니다.
     * @param recipeId 레시피의 ID.
     * @param dto 등록하려는 댓글 관련 정보.
     * @return 생성된 댓글의 ID.
     */
    @PostMapping
    public ResponseEntity<ApiResponse<Long>> addComment(@PathVariable Long recipeId, @RequestBody CreateCommentDto dto) {
        Long commentId = commentService.register(recipeId, dto.getContent());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(commentId));
    }

    /**
     * 레시피에 추가된 모든 댓글 목록을 조회합니다.
     * @param recipeId 레시피의 ID.
     * @param pageable 페이징 정보.
     * @return 댓글 목록 정보.
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Paged<CommentItemDto>>> findComments(
            @PathVariable Long recipeId,
            @PageableDefault(page = 0, size = 10) Pageable pageable) {
        Page<CommentItemDto> comments = commentService.findAllByRecipe(recipeId, pageable);
        return ResponseEntity.ok(ApiResponse.success(comments));
    }

    @PostMapping("/{id}/report")
    public ResponseEntity<ApiResponse<Long>> reportComment(@PathVariable Long id, @RequestBody CreateBasicReportDto dto) {
        Long reportId = reportService.register(EatzUserAuthUtil.getId(), id, EntityType.COMMENT, dto.getContent());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(reportId));
    }

}
