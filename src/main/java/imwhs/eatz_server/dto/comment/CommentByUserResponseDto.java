package imwhs.eatz_server.dto.comment;

import com.fasterxml.jackson.annotation.JsonFormat;
import imwhs.eatz_server.domain.recipe.Comment;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * CommentByUserResponseDto 클래스입니다.
 * <ul>
 *     <li>댓글의 기본 정보와 댓글이 달린 레시피의 기본(최소) 정보를 포함하는 DTO입니다.</li>
 *     <li>사용자가 등록한 댓글 목록을 조회하는 상황에서, 댓글 목록과 같이 댓글이 컬렉션에 포함되어졌을 때<br/>
 *     컬렉션 내 모든 댓글을 보다 효율적으로 조회해야 하는 경우에 주로 사용합니다.</li>
 * </ul>
 */
// TODO: DTO 클래스 공통 필드 상속
@Data
@AllArgsConstructor
public class CommentByUserResponseDto {

    private Long id;

    /** 댓글이 달린 레시피의 기본 정보 */
    private RecipeBasicDto recipe;

    private String content;

    private boolean isHidden;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime deletedAt;

    public CommentByUserResponseDto(
            Long id,
            RecipeBasicDto recipe,
            String content,
            boolean isHidden) {
        this.id = id;
        this.recipe = this.recipe;
        this.content = content;
        this.isHidden = isHidden;
    }

}
