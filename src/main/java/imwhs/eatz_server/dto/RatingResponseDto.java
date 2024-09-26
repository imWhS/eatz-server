package imwhs.eatz_server.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import imwhs.eatz_server.domain.Rating;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

// TODO: DTO 클래스 공통 필드 상속
@Data
public class RatingResponseDto {

    private Long id;

    private RatingUserDto user;

    private RatingRecipeDto recipe;

    private int score;

    private String content;

    private boolean isHidden;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime deletedAt;

    public RatingResponseDto(
            Long id,
            RatingUserDto user,
            RatingRecipeDto recipe,
            int score,
            String content,
            boolean isHidden) {
        this.id = id;
        this.user = user;
        this.recipe = recipe;
        this.score = score;
        this.content = content;
        this.isHidden = isHidden;
    }

    public RatingResponseDto(Rating rating) {
        this.id = rating.getId();
        this.user = new RatingUserDto(rating.getUser());
        this.recipe = new RatingRecipeDto(rating.getRecipe());
        this.score = rating.getScore();
        this.content = rating.getContent();
        this.isHidden = rating.isHidden();
        this.createdAt = rating.getCreatedAt();
        this.updatedAt = rating.getUpdatedAt();
        this.deletedAt = rating.getDeletedAt();
    }

}
