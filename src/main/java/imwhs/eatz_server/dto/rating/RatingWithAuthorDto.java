package imwhs.eatz_server.dto.rating;

import imwhs.eatz_server.domain.Rating;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 평가의 기본 정보와 평가를 등록한 사용자의 기본(최소) 정보 전달할 때 사용합니다.
 * <ul>
 *     <li> 레시피 별 평가 목록을 조회하는 상황에서 평가 목록과 같이 평가가 컬렉션에 포함되어졌을 때,
 *          컬렉션 내 모든 평가를 보다 효율적으로 조회해야 하는 경우에 주로 사용합니다. </li>
 * </ul>
 */
// TODO: Dto 클래스 공통 필드 상속
@Data
@AllArgsConstructor
public class RatingWithAuthorDto {

    private Long id;

    /** 평가를 등록한 사용자의 기본 정보 */
    private UserOfRatingDto user;

    private Integer score;

    private String content;

    private Boolean isHidden;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public RatingWithAuthorDto(
            Long id,
            UserOfRatingDto user,
            Integer score,
            String content,
            Boolean isHidden) {
        this.id = id;
        this.user = user;
        this.score = score;
        this.content = content;
        this.isHidden = isHidden;
    }

    public RatingWithAuthorDto(Rating rating) {
        this.id = rating.getId();
        this.user = new UserOfRatingDto(rating.getAuthor());
        this.score = rating.getScore();
        this.content = rating.getContent();
        this.isHidden = rating.getIsHidden();
        this.createdAt = rating.getCreatedAt();
        this.updatedAt = rating.getUpdatedAt();
    }

}
