package imwhs.eatz_server.controller.api.recipe;

import imwhs.eatz_server.dto.comment.*;
import imwhs.eatz_server.resolver.AuthenticatedEatzUserId;
import imwhs.eatz_server.service.comment.CommentService;
import imwhs.eatz_server.service.ReportService;
import imwhs.eatz_server.service.comment.CommentQueryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/**
 * 레시피의 댓글을 관리하기 위한 API를 제공하는 컨트롤러입니다.
 */
@RequestMapping("/api/v0/recipes/{recipeId}/comments")
@RequiredArgsConstructor
@RestController
public class RecipeCommentController {

    private final CommentService commentService;
    private final CommentQueryService commentQueryService;

    /**
     * 레시피에 새 댓글을 등록합니다.
     * @param recipeId 레시피의 ID
     * @param request 댓글 생성 및 등록 요청 정보
     * @return 생성된 댓글 ID
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommentBasicDto registerComment(
            @PathVariable Long recipeId,
            @AuthenticatedEatzUserId Long userId,
            @Valid @RequestBody CreateCommentRequest request) {
        return commentService.register(recipeId, userId, request.getContent());
    }

    /**
     * 특정 레시피의 댓글을 업데이트합니다.
     * @param id 댓글의 ID
     * @param request 댓글 업데이트 요청 정보
     */
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public CommentBasicDto updateComment(
            @PathVariable Long recipeId,
            @PathVariable Long id,
            @AuthenticatedEatzUserId Long userId,
            @Valid @RequestBody UpdateCommentRequest request) {
        return commentService.update(recipeId, id, userId, request.getContent());
    }

    /**
     * 특정 레시피의 댓글을 삭제 처리합니다.
     * @param id 댓글의 ID
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void markCommentAsDeleted(@PathVariable Long id, @AuthenticatedEatzUserId Long userId) {
        commentService.markAsDeleted(id, userId);
    }

    /**
     * 레시피에 추가된 모든 댓글 목록을 가져옵니다.
     * <ul>
     *     <li> 차단한 사용자의 댓글은 조회 대상에서 제외합니다. </li>
     * </ul>
     * @param recipeId 레시피의 ID
     * @param userId 현재 로그인한 사용자의 ID
     * @param pageable 페이징 정보
     * @return 댓글 목록
     */
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Page<CommentBasicDto> getAllComments(
            @PathVariable Long recipeId,
            @AuthenticatedEatzUserId(required = false) Long userId,
            @PageableDefault(page = 0, size = 10) Pageable pageable) {
        return commentQueryService.getAllBasicsByRecipeId(recipeId, userId, pageable);
    }

//    @PostMapping("/{id}/report")
//    @ResponseStatus(HttpStatus.CREATED)
//    public ReportCreationInfoResponse reportComment(
//            @PathVariable Long id,
//            @RequestBody ReportBasicCreateRequest request,
//            @AuthenticatedEatzUserId Long userId) {
//        return reportService.register(
//                userId,
//                id,
//                ReportResource.COMMENT,
//                request.getCategoryId(),
//                request.getResourceContent(),
//                request.getDescription());
//    }

}
