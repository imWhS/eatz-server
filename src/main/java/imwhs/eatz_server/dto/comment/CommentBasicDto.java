package imwhs.eatz_server.dto.comment;

import imwhs.eatz_server.domain.Comment;
import imwhs.eatz_server.dto.eatzuser.EatzUserEssentialDto;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 댓글(Comment)의 기본 정보를 전달할 때 사용하는 DTO입니다.
 * <ul>
 *      <li> Comment의 핵심 및 대부분의 정보와 EatzUser 등의 연관 관계 엔티티의 정보 및 현재 로그인 사용자의 context를 포함합니다. </li>
 *      <li> 주로 컬렉션으로 조회할 때 사용합니다. </li>
 * </ul>
 */
@Data
@AllArgsConstructor
public class CommentBasicDto {

    /**
     * 댓글의 ID
     */
    private Long id;

    /**
     * 댓글의 내용
     */
    private String content;

    /**
     * 댓글의 작성자
     */
    private EatzUserEssentialDto author;

    /**
     * 댓글의 숨김 처리 여부
     */
    private Boolean isHidden;

    /**
     * 댓글이 등록된 시간
     */
    private LocalDateTime createdAt;

    /**
     * 댓글이 마지막으로 수정된 시간
     */
    private LocalDateTime updatedAt;

    public CommentBasicDto(Comment comment) {
        this.id = comment.getId();
        this.content = comment.getContent();
        this.createdAt = comment.getCreatedAt();
        this.updatedAt = comment.getUpdatedAt();
        this.author = new EatzUserEssentialDto(comment.getAuthor());
        this.isHidden = comment.getIsHidden();
    }

}
