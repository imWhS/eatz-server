package imwhs.eatz_server.service;

import imwhs.eatz_server.domain.EatzUser;
import imwhs.eatz_server.domain.Recipe;
import imwhs.eatz_server.dto.CreateRecipeDto;
import imwhs.eatz_server.dto.RecipeResponseDto;
import imwhs.eatz_server.dto.UpdateRecipeDto;
import imwhs.eatz_server.exception.EatzUserNotFoundException;
import imwhs.eatz_server.exception.RecipeNotFoundException;
import imwhs.eatz_server.repository.EatzUserRepository;
import imwhs.eatz_server.repository.RecipeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RecipeService {

    private final RecipeRepository recipeRepository;

    private final EatzUserRepository eatzUserRepository;

    /**
     * 새 레시피 등록.
     * @param userId
     * @param dto
     * @return
     */
    @Transactional
    public Recipe registerRecipe(
            Long userId,
            CreateRecipeDto dto
    ) {
        EatzUser user = eatzUserRepository.findById(userId).orElseThrow(() ->
                new EatzUserNotFoundException("id가 " + userId + "인 사용자를 찾지 못했습니다."));

        Recipe recipe = Recipe.create(user, dto.getTitle(), dto.getUrl(), dto.getImageUrl());

        return recipeRepository.save(recipe);
    }

    /**
     * 식별자로 레시피 조회.
     * @param id
     * @return
     */
    public Recipe findRecipeById(Long id) {
        return recipeRepository.findById(id)
                .orElseThrow(() -> new RecipeNotFoundException("id가 " + id + "인 레시피를 찾지 못했습니다."));
    }

    /**
     * 모든 레시피 조회.
     */
    public List<Recipe> findAllRecipes() {
        return recipeRepository.findAll();
    }

    /**
     * 레시피 정보 수정.
     * @param id
     * @param dto
     * @return
     */
    @Transactional
    public RecipeResponseDto updateRecipe(Long id, UpdateRecipeDto dto) {
        Recipe recipe = recipeRepository.findById(id)
                .orElseThrow(() ->
                        new RecipeNotFoundException("id가 " + id + "인 레시피를 찾지 못했습니다."));

        recipe.update(dto);

        return new RecipeResponseDto(recipe);
    }

}
