package imwhs.eatz_server.dto.comment;

import com.fasterxml.jackson.annotation.JsonFormat;
import imwhs.eatz_server.domain.Comment;
import lombok.Data;

import java.time.LocalDateTime;

// TODO: DTO 클래스 공통 필드 상속
@Data
public class CommentResponseDto {

    private Long id;

    private CommentUserDto user;

    private CommentRecipeDto recipe;

    private String content;

    private boolean isHidden;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime deletedAt;

    public CommentResponseDto(
            Long id,
            CommentUserDto user,
            CommentRecipeDto recipe,
            String content,
            boolean isHidden) {
        this.id = id;
        this.user = user;
        this.recipe = recipe;
        this.content = content;
        this.isHidden = isHidden;
    }

    public CommentResponseDto(Comment comment) {
        this.id = comment.getId();
        this.user = new CommentUserDto(comment.getUser());
        this.recipe = new CommentRecipeDto(comment.getRecipe());
        this.content = comment.getContent();
        this.isHidden = comment.isHidden();
        this.createdAt = comment.getCreatedAt();
        this.updatedAt = comment.getUpdatedAt();
        this.deletedAt = comment.getDeletedAt();
    }

}
