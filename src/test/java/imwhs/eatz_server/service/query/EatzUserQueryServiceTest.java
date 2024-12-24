package imwhs.eatz_server.service.query;

import imwhs.eatz_server.domain.EatzUser;
import imwhs.eatz_server.domain.Recipe;
import imwhs.eatz_server.domain.Role;
import imwhs.eatz_server.dto.PagedApiResponse;
import imwhs.eatz_server.dto.eatzuser.EatzUserSummaryDto;
import imwhs.eatz_server.repository.recipe.RecipeRepository;
import imwhs.eatz_server.repository.eatzuser.EatzUserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@SpringBootTest
class EatzUserQueryServiceTest {

    @Autowired
    EatzUserQueryService userQueryService;

    @Autowired
    EatzUserRepository userRepository;

    @Autowired
    private RecipeRepository recipeRepository;

    @Test
    @DisplayName("등록된 모든 사용자와 사용자 별 활동 요약 정보가 정상적으로 조회되는지 테스트합니다.")
    @Transactional
    void findAllUsersWithActivityTest() {
        // given
        EatzUser user1 = EatzUser.createMember(
                "1heextory",
                "1heextory@icloud.com",
                "1q2w3e4r!");
        Recipe recipe1 = Recipe.of(
                user1,
                "Apple Cake",
                "https://www.recipe.com/",
                "https://www.recipe.com/apple.png",
                "");
        userRepository.save(user1);
        recipeRepository.save(recipe1);

        EatzUser user2 = EatzUser.createMember(
                "2heextory",
                "2heextory@icloud.com",
                "1q2w3e4r!");
        Recipe recipe2 = Recipe.of(
                user2,
                "Banana Cake",
                "https://www.recipe.com/",
                "https://www.recipe.com/banana.png",
                "");
        userRepository.save(user2);
        recipeRepository.save(recipe2);
        Recipe recipe3 = Recipe.of(
                user2,
                "Watermelon Cake",
                "https://www.recipe.com/",
                "https://www.recipe.com/watermelon.png",
                "");
        userRepository.save(user2);
        recipeRepository.save(recipe3);

        EatzUser user3 = EatzUser.createMember(
                "3heextory",
                "3heextory@icloud.com",
                "1q2w3e4r!");
        Recipe recipe4 = Recipe.of(
                user3,
                "Mango Cake",
                "https://www.recipe.com/",
                "https://www.recipe.com/mango.png",
                "");
        userRepository.save(user3);
        recipeRepository.save(recipe4);

        EatzUser user4 = EatzUser.createMember(
                "4heextory",
                "4heextory@icloud.com",
                "1q2w3e4r!");
        userRepository.save(user4);

        // when
        Page<EatzUserSummaryDto> usersWithActivities = userQueryService.findAllUsersWithActivity(Pageable.unpaged());

        // then
        Assertions.assertThat(usersWithActivities.getTotalElements()).isEqualTo(4);
        Assertions.assertThat(usersWithActivities.getContent().get(0).getRecipeCount()).isEqualTo(1);
        Assertions.assertThat(usersWithActivities.getContent().get(1).getRecipeCount()).isEqualTo(2);
        Assertions.assertThat(usersWithActivities.getContent().get(2).getRecipeCount()).isEqualTo(1);
        Assertions.assertThat(usersWithActivities.getContent().get(3).getRecipeCount()).isEqualTo(0);
    }

    @Test
    @DisplayName("등록된 모든 사용자와 사용자 별 활동 요약 정보를 조회할 때, 페이징 처리가 제대로 동작하는지 테스트합니다.")
    @Transactional
    void findAllUsersWithActivity_PagingTest() {
        // given
        EatzUser user1 = EatzUser.createMember("user1", "user1@icloud.com", "password1");
        Recipe recipe1 = Recipe.of(user1, "Recipe 1", "https://www.recipe.com/", "https://www.recipe.com/img1.png", "");
        userRepository.save(user1);
        recipeRepository.save(recipe1);

        EatzUser user2 = EatzUser.createMember("user2", "user2@icloud.com", "password2");
        userRepository.save(user2);

        EatzUser user3 = EatzUser.createMember("user3", "user3@icloud.com", "password3");
        Recipe recipe2 = Recipe.of(user3, "Recipe 2", "https://www.recipe.com/", "https://www.recipe.com/img2.png", "");
        Recipe recipe3 = Recipe.of(user3, "Recipe 3", "https://www.recipe.com/", "https://www.recipe.com/img3.png", "");
        userRepository.save(user3);
        recipeRepository.save(recipe2);
        recipeRepository.save(recipe3);

        // when
        int page = 0;
        int size = 2;
        Page<EatzUserSummaryDto> foundUsersWithActivities = userQueryService.findAllUsersWithActivity(
                PageRequest.of(page, size));

        // then
        Assertions.assertThat(foundUsersWithActivities.getContent().size()).isEqualTo(2);
        Assertions.assertThat(foundUsersWithActivities.getContent().get(0).getRecipeCount()).isEqualTo(1);
        Assertions.assertThat(foundUsersWithActivities.getContent().get(0).getUsername()).isEqualTo("user1");
        Assertions.assertThat(foundUsersWithActivities.getContent().get(1).getRecipeCount()).isEqualTo(0);
        Assertions.assertThat(foundUsersWithActivities.getContent().get(1).getUsername()).isEqualTo("user2");
        Assertions.assertThat(foundUsersWithActivities.getTotalElements()).isEqualTo(3);
        Assertions.assertThat(foundUsersWithActivities.getTotalPages()).isEqualTo(2);
    }

}