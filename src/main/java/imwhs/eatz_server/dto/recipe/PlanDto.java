package imwhs.eatz_server.dto.recipe;

import imwhs.eatz_server.dto.rating.RatingSummaryDto;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

/**
 * PlanDto 클래스입니다.
 */
@Data
@AllArgsConstructor
public class PlanDto {

    private Long id;

    /**
     * 레시피 ID.
     */
    private Long recipeId;

    /**
     * 레시피를 등록한 사용자 ID.
     */
    private Long userId;

    /**
     * 레시피 제목.
     * TODO: tmp
     */
    private String title;

    /**
     * 레시피 대표 이미지 URL 주소.
     */
    private String imageUrl;

    /**
     * 레시피가 플래너에 저장된 날짜.
     */
    private LocalDate scheduledAt;

    /**
     * 레시피에 등록된 평가 요약 정보. (from Rating)
     */
    private RatingSummaryDto rating;

    public PlanDto(
            Long id,
            Long recipeId,
            Long userId,
            String title,
            String imageUrl,
            LocalDate scheduledAt
            ) {
        this.id = id;
        this.recipeId = recipeId;
        this.title = title;
        this.imageUrl = imageUrl;
        this.userId = userId;
        this.scheduledAt = scheduledAt;
    }

}
