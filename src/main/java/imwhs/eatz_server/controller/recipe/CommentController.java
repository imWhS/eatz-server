package imwhs.eatz_server.controller.recipe;

import imwhs.eatz_server.dto.ApiResponse;
import imwhs.eatz_server.dto.Paged;
import imwhs.eatz_server.dto.comment.CommentWithUserResponseDto;
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

@RequestMapping("/api/v0/recipes/{id}/comments")
@RequiredArgsConstructor
@Controller
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<ApiResponse<Long>> addComment(@PathVariable Long id, @RequestBody CommentCreateDto dto) {
        Long commentId = commentService.registerComment(id, dto.getContent());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(commentId));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Paged<CommentWithUserResponseDto>>> findComments(
            @PathVariable Long id,
            @PageableDefault(page = 0, size = 10) Pageable pageable) {
        Page<CommentWithUserResponseDto> comments = commentService.findCommentsByRecipe(id, pageable);
        return ResponseEntity.ok(ApiResponse.success(comments));
    }

}
