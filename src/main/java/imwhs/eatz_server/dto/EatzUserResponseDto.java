package imwhs.eatz_server.dto;

import imwhs.eatz_server.domain.Recipe;
import imwhs.eatz_server.domain.Role;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class EatzUserResponseDto {

    private Long id;

    private String username;

    private String email;

    private Role role;

    private List<Recipe> recipeList;

}
