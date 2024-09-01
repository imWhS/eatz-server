package imwhs.eatz_server.dto;

import lombok.Data;

@Data
public class UpdateRecipeDto {

    private String title;

    private String description;

    private String url;

    private String imageUrl;

}
