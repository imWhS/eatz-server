package imwhs.eatz_server.service.admin;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import imwhs.eatz_server.domain.eatzuser.EatzUser;
import imwhs.eatz_server.domain.recipe.Recipe;
import imwhs.eatz_server.dto.recipe.RecipeInitDto;
import imwhs.eatz_server.repository.EatzUserRepository;
import imwhs.eatz_server.repository.ingredient.IngredientRepository;
import imwhs.eatz_server.repository.kitchenware.KitchenwareRepository;
import imwhs.eatz_server.repository.recipe.RecipeRepository;
import imwhs.eatz_server.service.tag.TagService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class AdminBulkService {

    private final TagService tagService;

    private final EatzUserRepository userRepository;
    private final RecipeRepository recipeRepository;
    private final IngredientRepository ingredientRepository;
    private final KitchenwareRepository kitchenwareRepository;

    private final ObjectMapper objectMapper;

    // TODO: 재료, 도구 In-memory mapping 방식 추가
    @Transactional(rollbackFor = Exception.class)
    public void saveBulkRecipes(MultipartFile json, Long authorId) {
        EatzUser author = userRepository.get(authorId);

        try {
            List<RecipeInitDto> recipeInitDtos = objectMapper.readValue(
                    json.getInputStream(), new TypeReference<>() {});

            List<Recipe> recipesToSave = new ArrayList<>();

            for (RecipeInitDto dto : recipeInitDtos) {
                Recipe recipe = dto.toRecipe(author);

                // 재료 매핑
                if (dto.getIngredientNames() != null) {
                    for (String ingredientName : dto.getIngredientNames()) {
                        ingredientRepository.findFirstByNameAndDeletedAtIsNull(ingredientName)
                                .ifPresent(recipe::addIngredient);
                    }
                }

                // 도구 매핑
                if (dto.getKitchenwareNames() != null) {
                    for (String kitchenwareName : dto.getKitchenwareNames()) {
                        kitchenwareRepository.findFirstByNameAndDeletedAtIsNull(kitchenwareName)
                                .ifPresent(recipe::addKitchenware);
                    }
                }

                // 태그 매핑
                if (dto.getTagNames() != null) {
                    tagService.addAllToRecipe(dto.getTagNames(), recipe);
                }

                recipesToSave.add(recipe);
            }

            recipeRepository.saveAll(recipesToSave);
            log.info("EATZ 레시피 {}개 벌크 추가를 완료했어요!", recipesToSave.size());
        } catch (IOException e) {
            log.error("EATZ 레시피 벌크 추가 중 오류가 발생했어요. | {}", e.getMessage(), e);
            throw new RuntimeException("레시피 벌크 추가 데이터를 파싱하는 과정 중 오류가 발생했어요.", e);
        }
    }

}
