package imwhs.eatz_server.dto.recipe;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 *  레시피의 핵심 정보와 작성자 정보를 함께 전달할 때 사용하는 DTO입니다.
 */
@EqualsAndHashCode
@Getter
@AllArgsConstructor
public class RecipeEssentialWithAuthorDto {

    /**
     * 레시피 ID
     */
    private Long id;

    /**
     * 레시피 제목
     */
    private String title;

    /**
     * 레시피 대표 이미지 URL 주소
     */
    private String imageUrl;

    /**
     * 레시피의 댓글 기능 사용 여부
     */
    private boolean isCommentEnabled;

    /**
     * 레시피 작성자 ID
     */
    private Long authorId;

    /**
     * 레시피 작성자 사용자 이름
     */
    private String authorUsername;

    /**
     * 레시피 작성자 대표 이미지 URL
     */
    private String authorImageUrl;

}
