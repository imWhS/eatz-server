package imwhs.eatz_server.service;

import imwhs.eatz_server.domain.EatzUser;
import imwhs.eatz_server.domain.Recipe;
import imwhs.eatz_server.domain.Role;
import imwhs.eatz_server.dto.*;
import imwhs.eatz_server.exception.RecipeNotFoundException;
import imwhs.eatz_server.exception.UnauthorizedAccessException;
import imwhs.eatz_server.queryservice.RecipeQueryService;
import imwhs.eatz_server.repository.EatzUserRepository;
import imwhs.eatz_server.repository.RecipeRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class RecipeServiceTest {

    @Autowired private RecipeService recipeService;

    @Autowired private RecipeQueryService recipeQueryService;

    @Autowired private EatzUserRepository userRepository;

    @Autowired private RecipeRepository recipeRepository;

    @Test
    @DisplayName("새 레시피를 등록했을 때, ID로 해당 레시피가 정상적으로 조회되는지 테스트합니다.")
    void recipeRegisterAndFindRecipeByIdTest() {
        // given
        EatzUser user = EatzUser.create(
                "heextory",
                "heextory@icloud.com",
                "1q2w3e4r!",
                Role.MEMBER);
        userRepository.save(user);
        CreateRecipeDto createRecipeDto = new CreateRecipeDto(
                "Kimchi pasta",
                "https://blog.naver.com/heextory",
                "https://www.image.com",
                "맛있는 파스타 레시피를 준비해보았어요~");

        // when
        RecipeResponseDto registeredRecipe = recipeService.registerRecipe(createRecipeDto, user.getId());
        RecipeResponseDto foundRecipe = recipeQueryService.findRecipeById(registeredRecipe.getId());

        // then
        Assertions.assertThat(foundRecipe).isNotNull();
        Assertions.assertThat(foundRecipe.getId()).isEqualTo(registeredRecipe.getId());
        Assertions.assertThat(foundRecipe.getTitle()).isEqualTo(registeredRecipe.getTitle());
        Assertions.assertThat(foundRecipe.getDescription()).isEqualTo(registeredRecipe.getDescription());
        Assertions.assertThat(foundRecipe.getUrl()).isEqualTo(registeredRecipe.getUrl());
        Assertions.assertThat(foundRecipe.getImageUrl()).isEqualTo(registeredRecipe.getImageUrl());
        Assertions.assertThat(foundRecipe.getCreatedAt()).isNotNull();
    }

    @Test
    @DisplayName("유효하지 않은 ID로 레시피를 조회하려고 할 때, 예외가 발생하는지 테스트합니다.")
    void recipeValidationTest() {
        // given
        EatzUser user = EatzUser.create(
                "heextory",
                "heextory@icloud.com",
                "1q2w3e4r!",
                Role.MEMBER);
        userRepository.save(user);

        CreateRecipeDto createRecipeDto = new CreateRecipeDto("Kimchi pasta", "https://blog.naver.com/heextory", "https://www.image.com", "맛있는 파스타 레시피를 준비해보았어요~");
        recipeService.registerRecipe(createRecipeDto, user.getId());

        // when, then
        Assertions.assertThatThrownBy(() -> recipeQueryService.findRecipeById(99999L))
                .isInstanceOf(RecipeNotFoundException.class);
    }

    @Test
    @DisplayName("특정 사용자가 등록한 모든 레시피가 정상적으로 조회되는지 테스트합니다.")
    void findAllRecipesTest() {
        // given
        EatzUser userA = EatzUser.create(
                "heextoryA",
                "heextoryA@gmail.com",
                "1q2w3e4r!",
                Role.MEMBER
        );
        EatzUser userB = EatzUser.create(
                "heextoryB",
                "heextoryB@gmail.com",
                "1q2w3e4r!",
                Role.MEMBER
        );
        userRepository.save(userA);
        userRepository.save(userB);

        CreateRecipeDto createRecipeDto1ByA = new CreateRecipeDto(
                "Kimchi pasta",
                "https://blog.naver.com/heextory",
                "https://www.image.com",
                "맛있는 파스타 레시피를 준비해보았어요~");
        CreateRecipeDto createRecipeDto2ByA = new CreateRecipeDto(
                "치즈 불닭 덮밥",
                "https://blog.naver.com/samyang",
                "https://www.buldak.com",
                "매콤한 맛이 매력적인 소스를 밥과 즐겨보세요!");
        recipeService.registerRecipe(createRecipeDto1ByA, userA.getId());
        recipeService.registerRecipe(createRecipeDto2ByA, userA.getId());

        CreateRecipeDto createRecipeDto1ByB = new CreateRecipeDto(
                "차돌 된장찌개",
                "https://blog.naver.com/cow",
                "https://www.koreanfood.com",
                "된장찌개 국물이 예술이에요!");
        CreateRecipeDto createRecipeDto2ByB = new CreateRecipeDto(
                "돼지고기 김치볶음밥",
                "https://blog.naver.com/kimchi",
                "https://www.awesomekfood.com",
                "해장 필수 메뉴로 추천드려요!");
        recipeService.registerRecipe(createRecipeDto1ByB, userB.getId());
        recipeService.registerRecipe(createRecipeDto2ByB, userB.getId());

        // when
        PagedResponseDto<RecipeResponseDto> allRecipesRegisteredByUserA = recipeQueryService.findAllRecipesByUserId(
                userA.getId(),
                null,
                null);

        // then
        Assertions.assertThat(allRecipesRegisteredByUserA).isNotNull();
        Assertions.assertThat(allRecipesRegisteredByUserA.getItems()).hasSize(2);
        Assertions.assertThat(allRecipesRegisteredByUserA.getItems().get(0).getTitle())
                .isEqualTo("Kimchi pasta");
        Assertions.assertThat(allRecipesRegisteredByUserA.getItems().get(1).getTitle())
                .isEqualTo("치즈 불닭 덮밥");
    }

    @Test
    @DisplayName("레시피를 정상적으로 수정할 수 있는지 테스트합니다.")
    void updateRecipeTest() {
        // given
        EatzUser user = EatzUser.create(
                "heextoryA",
                "heextoryA@gmail.com",
                "1q2w3e4r!",
                Role.MEMBER
        );
        userRepository.save(user);

        CreateRecipeDto createRecipeDto = new CreateRecipeDto(
                "차돌 된장찌개",
                "https://blog.naver.com/cow",
                "https://www.koreanfood.com",
                "된장찌개 국물이 예술이에요!");
        RecipeResponseDto registeredRecipe = recipeService.registerRecipe(createRecipeDto, user.getId());

        UpdateRecipeDto updateRecipeDto = new UpdateRecipeDto("불닭 차돌 된장찌개", "https://blog.daum.net/cow", "https://www.kfoodworld.com", "된장찌개 국물이 화끈하게 매워요!");

        // when
        RecipeResponseDto updatedRecipe = recipeService.updateRecipe(registeredRecipe.getId(), updateRecipeDto, user.getId());

        // then
        Assertions.assertThat(updatedRecipe).isNotNull();
        Assertions.assertThat(updatedRecipe.getTitle()).isEqualTo(updateRecipeDto.getTitle());
        Assertions.assertThat(updatedRecipe.getUrl()).isEqualTo(updateRecipeDto.getUrl());
        Assertions.assertThat(updatedRecipe.getImageUrl()).isEqualTo(updateRecipeDto.getImageUrl());
        Assertions.assertThat(updatedRecipe.getUpdatedAt()).isNotNull();
    }

    @Test
    @DisplayName("권한 없는 사용자가 레시피를 수정하려고 할 때, 권한 예외가 발생하는지 테스트합니다.")
    void unauthorizedUserUpdateRecipeTest() {
        // given
        EatzUser user = EatzUser.create(
                "heextoryA",
                "heextoryA@gmail.com",
                "1q2w3e4r!",
                Role.MEMBER
        );
        userRepository.save(user);

        CreateRecipeDto createRecipeDto = new CreateRecipeDto(
                "차돌 된장찌개",
                "https://blog.naver.com/cow",
                "https://www.koreanfood.com",
                "된장찌개 국물이 예술이에요!");
        RecipeResponseDto registeredRecipe = recipeService.registerRecipe(createRecipeDto, user.getId());

        UpdateRecipeDto updateRecipeDto = new UpdateRecipeDto("불닭 차돌 된장찌개", "https://blog.daum.net/cow", "https://www.kfoodworld.com", "된장찌개 국물이 화끈하게 매워요!");

        // then
        Assertions.assertThatThrownBy(() ->
                recipeService.updateRecipe(registeredRecipe.getId(), updateRecipeDto, 99999L))
                .isInstanceOf(UnauthorizedAccessException.class);
    }

    @Test
    @DisplayName("레시피가 정상적으로 삭제되는지 테스트합니다.")
    void deleteRecipeTest() {
        // given
        EatzUser user = EatzUser.create(
                "heextoryA",
                "heextoryA@gmail.com",
                "1q2w3e4r!",
                Role.MEMBER
        );
        userRepository.save(user);

        CreateRecipeDto createRecipeDto = new CreateRecipeDto(
                "차돌 된장찌개",
                "https://blog.naver.com/cow",
                "https://www.koreanfood.com",
                "된장찌개 국물이 예술이에요!");
        RecipeResponseDto registeredRecipe = recipeService.registerRecipe(createRecipeDto, user.getId());

        // when
        recipeService.deleteRecipe(registeredRecipe.getId(), user.getId());
        Recipe deletedRecipe = recipeRepository.findById(registeredRecipe.getId()).orElse(null);

        // then
        if (deletedRecipe != null) {
            Assertions.assertThat(deletedRecipe.getDeletedAt()).isNotNull();
        }
    }

    @Test
    @DisplayName("권한 없는 사용자가 레시피를 삭제하려고 할 때, 예외가 발생하는지 테스트합니다.")
    void unauthorizedUserDeleteRecipeTest() {
        // given
        EatzUser user = EatzUser.create(
                "heextoryA",
                "heextoryA@gmail.com",
                "1q2w3e4r!",
                Role.MEMBER
        );
        userRepository.save(user);

        CreateRecipeDto createRecipeDto = new CreateRecipeDto(
                "차돌 된장찌개",
                "https://blog.naver.com/cow",
                "https://www.koreanfood.com",
                "된장찌개 국물이 예술이에요!");
        RecipeResponseDto registeredRecipe = recipeService.registerRecipe(createRecipeDto, user.getId());

        // then
        Assertions.assertThatThrownBy(() -> recipeService.deleteRecipe(registeredRecipe.getId(), 9999L))
                .isInstanceOf(UnauthorizedAccessException.class);

    }

    @PersistenceContext
    private EntityManager em;

}