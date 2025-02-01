package imwhs.eatz_server.dto.rating;

import com.fasterxml.jackson.annotation.JsonFormat;
import imwhs.eatz_server.domain.recipe.Rating;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * RatingByUserResponseDto 클래스입니다.
 * <ul>
 *     <li>평가의 기본 정보와 평가가 달린 레시피의 기본(최소) 정보를 포함하는 DTO입니다.</li>
 *     <li>사용자가 등록한 평가 목록을 조회하는 상황에서, 평가 목록과 같이 평가가 컬렉션에 포함되어졌을 때<br/>
 *     컬렉션 내 모든 평가를 보다 효율적으로 조회해야 하는 경우에 주로 사용합니다.</li>
 * </ul>
 */
// TODO: DTO 클래스 공통 필드 상속
@Data
@AllArgsConstructor
public class RatingByUserDto {

    private Long id;

    /** 평가가 달린 레시피의 기본 정보 */
    private RatingRecipeDto recipe;

    private Integer score;

    private String content;

    private boolean isHidden;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime deletedAt;

    public RatingByUserDto(
            Long id,
            RatingRecipeDto recipe,
            Integer score,
            String content,
            boolean isHidden) {
        this.id = id;
        this.recipe = recipe;
        this.score = score;
        this.content = content;
        this.isHidden = isHidden;
    }

    public RatingByUserDto(Rating rating) {
        this.id = rating.getId();
        this.recipe = new RatingRecipeDto(rating.getRecipe());
        this.score = rating.getScore();
        this.content = rating.getContent();
        this.isHidden = rating.isHidden();
        this.createdAt = rating.getCreatedAt();
        this.updatedAt = rating.getUpdatedAt();
        this.deletedAt = rating.getDeletedAt();
    }

}
