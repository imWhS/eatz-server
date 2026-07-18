package imwhs.eatz_server.dto.comment;

import imwhs.eatz_server.dto.recipe.RecipeEssentialDto;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class CommentEssentialWithRecipeDto {

    private Long id;

    private RecipeEssentialDto recipe;

    private String content;

    private Boolean isHidden;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}
