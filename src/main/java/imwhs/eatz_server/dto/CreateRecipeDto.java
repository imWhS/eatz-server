package imwhs.eatz_server.dto;

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

}
