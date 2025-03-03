package imwhs.eatz_server.dto.comment;

import com.fasterxml.jackson.annotation.JsonFormat;
import imwhs.eatz_server.dto.recipe.RecipeBasicDto;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class CommentWithRecipeDto {

    private Long id;

    private RecipeBasicDto recipe;

    private String content;

    private boolean isHidden;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

}
