package imwhs.eatz_server.queryservice;

import imwhs.eatz_server.domain.EatzUser;
import imwhs.eatz_server.domain.Recipe;
import imwhs.eatz_server.domain.Role;
import imwhs.eatz_server.dto.CreateEatzUserDto;
import imwhs.eatz_server.dto.EatzUserResponseDto;
import imwhs.eatz_server.repository.EatzUserRepository;
import imwhs.eatz_server.repository.RecipeRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class EatzUserQueryServiceTest {

    @Autowired
    private EatzUserQueryService userQueryService;

    @Autowired
    private EatzUserRepository userRepository;

    @Autowired
    private RecipeRepository recipeRepository;

    @Test
    @DisplayName("Recipe로 특정 EatzUser가 정상적으로 조회되는지 테스트합니다.")
    void findByUserTest() {
        // given
        EatzUser user = EatzUser.create("heextory", "heextory@icloud.com", "1q2w3e4r!", Role.MEMBER);
        userRepository.save(user);

        Recipe recipe = Recipe.create(user, "Kimchi Pasta", "https://blog.naver.com", "https://imgcdn.naver.com", "맛있는 김치 파스타!");
        recipeRepository.save(recipe);

        // when
        EatzUserResponseDto foundUserByRecipe = userQueryService.findByRecipe(recipe.getId());

        // then
        Assertions.assertThat(foundUserByRecipe).isNotNull();
        Assertions.assertThat(foundUserByRecipe.getId()).isEqualTo(user.getId());
        Assertions.assertThat(foundUserByRecipe.getCreatedAt()).isEqualTo(user.getCreatedAt());
        Assertions.assertThat(foundUserByRecipe.getRecipeList().contains(recipe)).isEqualTo(true);
    }

}