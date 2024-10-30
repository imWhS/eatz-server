package imwhs.eatz_server.dto.recipe;

import imwhs.eatz_server.domain.EatzUser;
import imwhs.eatz_server.domain.Recipe;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateRecipeDto {

    @NotNull
    private String title;

    @NotNull
    private String url;

    @NotNull
    private String imageUrl;

    private String description;

    // TODO: 생성 날짜, 수정 날짜, 삭제 날짜 추가

    public CreateRecipeDto(String title, String url, String imageUrl, String description) {
        this.title = title;
        this.url = url;
        this.imageUrl = imageUrl;
        this.description = description;
    }

    public Recipe toEntity(EatzUser user) {
        return Recipe.create(user, title, url, imageUrl, description);
    }

}
