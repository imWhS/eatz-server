package imwhs.eatz_server.service.recipe;

import imwhs.eatz_server.ImageCategory;
import imwhs.eatz_server.domain.Tag;
import imwhs.eatz_server.domain.Ingredient;
import imwhs.eatz_server.domain.eatzuser.EatzUser;
import imwhs.eatz_server.domain.Kitchenware;
import imwhs.eatz_server.domain.recipe.Recipe;
import imwhs.eatz_server.dto.RecipeOutboundResponse;
import imwhs.eatz_server.dto.recipe.RecipeCreateDto;
import imwhs.eatz_server.dto.recipe.RecipeCreationInfoDto;
import imwhs.eatz_server.dto.recipe.RecipeUpdateDto;
import imwhs.eatz_server.repository.EatzUserRepository;
import imwhs.eatz_server.repository.ingredient.IngredientRepository;
import imwhs.eatz_server.repository.kitchenware.KitchenwareRepository;
import imwhs.eatz_server.repository.tag.TagRepository;
import imwhs.eatz_server.repository.recipe.RecipeRepository;
import imwhs.eatz_server.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 레시피(Recipe) 상태를 변경할 수 있는 서비스를 제공합니다.
 * <ul>
 *     <li> Recipe 생성 뿐 아니라, 수정, 삭제 등 엔티티 데이터를 변경하는 쓰기 전용 비즈니스 로직을 담당합니다. </li>
 *     <li> 읽기 전용 비즈니스 로직은 RecipeQueryService에서 처리합니다. </li>
 * </ul>
 */
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class RecipeService {

    private final RecipeRepository recipeRepository;
    private final EatzUserRepository userRepository;
    private final IngredientRepository ingredientRepository;
    private final KitchenwareRepository kitchenwareRepository;
    private final TagRepository tagRepository;
    private final ImageService imageService;

    /**
     * 레시피를 생성하고, 레시피와 연관된 정보(재료, 도구, 상위 재료)를 생성 또는 매핑한 후 저장합니다.
     * @param dto 레시피를 생성하기 위해 필요한 정보
     * @param authorId 레시피 등록을 요청한 사용자의 ID (레시피 작성자의 ID)
     * @return 레시피의 생성 정보
     */
    @Transactional(rollbackFor = Exception.class)
    public RecipeCreationInfoDto register(RecipeCreateDto dto, Long authorId) {
        EatzUser author = userRepository.getReference(authorId);
        Recipe recipe = dto.toEntity(author);

        // 레시피에 재료를 추가합니다.
        List<Long> ingredientIds = dto.getIngredientIds();
        addIngredients(ingredientIds, recipe);

        // 레시피에 도구를 추가합니다.
        List<Long> kitchenwareIds = dto.getKitchenwareIds();
        addKitchenwares(kitchenwareIds, recipe);

        // 레시피에 태그를 추가합니다.
        List<String> tagNames = dto.getTagNames();
        addTags(tagNames, recipe);

        // 레시피를 저장합니다.
        recipeRepository.save(recipe);

        return new RecipeCreationInfoDto(recipe);
    }

    /**
     * 레시피를 업데이트하고, 레시피와 연관된 정보(재료, 도구, 상위 재료)까지 생성 또는 새로 매핑합니다.
     * @param id 업데이트할 레시피의 ID
     * @param dto 레시피를 업데이트하기 위해 필요한 정보
     * @param authorId 레시피 업데이트를 요청한 사용자의 ID (레시피 작성자의 ID)
     */
    @Transactional(rollbackFor = Exception.class)
    public void update(Long id, RecipeUpdateDto dto, Long authorId) {
        userRepository.validateExists(authorId);
        Recipe recipe = recipeRepository.get(id);
        recipe.update(
                authorId,
                dto.getTitle(),
                dto.getUrl(),
                dto.getImageUrl(),
                dto.getCookingTime(),
                dto.getServings(),
                dto.getIsCommentEnabled(),
                dto.getDescription(),
                dto.getPrepTime(),
                dto.getCreatorName(),
                dto.getCreatorUrl());

        // TODO: 삭제 쿼리 N+1 방지를 위한 벌크 연산 최적화
        // 기존 레시피에 추가했던 재료를 모두 삭제하고 새 재료를 추가합니다.
        recipe.clearAllIngredientRecipes();
        recipeRepository.flush();
        List<Long> ingredientIds = dto.getIngredientIds();
        addIngredients(ingredientIds, recipe);

        // 기존 레시피에 추가했던 도구를 모두 삭제하고 새 도구를 추가합니다.
        recipe.clearAllKitchenwareRecipes();
        recipeRepository.flush();
        List<Long> kitchenwareIds = dto.getKitchenwareIds();
        addKitchenwares(kitchenwareIds, recipe);

        // 기존 레시피에 추가했던 태그를 모두 삭제하고 새 태그를 추가합니다.
        recipe.clearAllRecipeTags();
        recipeRepository.flush();
        List<String> tagNames = dto.getTagNames();
        addTags(tagNames, recipe);
    }

    /**
     * 레시피를 삭제 처리합니다.
     * <p>
     *     레시피의 작성자 뿐 아니라, 권리자 역할이 있는 사용자도 삭제 처리할 수 있습니다.
     * </p>
     * @param id 삭제 처리할 레시피의 ID
     * @param requesterId 레시피 삭제 처리를 요청한 사용자의 ID (작성자의 ID)
     */
    @Transactional(rollbackFor = Exception.class)
    public void markAsDeleted(Long id, Long requesterId) {
        EatzUser user = userRepository.get(requesterId);
        Recipe recipe = recipeRepository.get(id);
        recipe.markAsDeleted(user);
    }

    @Transactional(rollbackFor = Exception.class)
    public String uploadNewImage(MultipartFile image, Long userId) {
        userRepository.validateExists(userId);
        return imageService.upload(image, ImageCategory.RECIPE);
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteExistingImage(String imageUrl, Long userId) {
        userRepository.validateExists(userId);
        imageService.delete(imageUrl);
    }

    /**
     * ID에 해당하는 여러 재료들을 레시피에 추가합니다.
     * @param ids 재료의 ID 목록
     * @param recipe 레시피의 Recipe 엔티티
     */
    private void addIngredients(List<Long> ids, Recipe recipe) {
        if (ids == null || ids.isEmpty()) { return; }
        for (Long id : ids) {
            System.out.println("id = " + id);
        }
        Set<Long> idSet = new HashSet<>(ids);
        List<Ingredient> existingIngredients = ingredientRepository.findAllById(ids);
        if (idSet.size() != existingIngredients.size()) {
            throw new IllegalArgumentException("레시피에 추가하려는 재료 중 일부가 유효하지 않아요.");
        }

        for (Ingredient existingIngredient : existingIngredients) {
            recipe.addIngredient(existingIngredient);
        }
    }

    /**
     * ID에 해당하는 여러 도구들을 레시피에 추가합니다.
     */
    private void addKitchenwares(List<Long> ids, Recipe recipe) {
        if (ids == null || ids.isEmpty()) { return; }
        Set<Long> idSet = new HashSet<>(ids);
        List<Kitchenware> existingKitchenwares = kitchenwareRepository.findAllById(ids);
        if (idSet.size() != existingKitchenwares.size()) {
            throw new IllegalArgumentException("레시피에 추가하려는 도구 중 일부가 유효하지 않아요.");
        }

        for (Kitchenware existingKitchenware : existingKitchenwares) {
            recipe.addKitchenware(existingKitchenware);
        }
    }

    /**
     * 이름에 해당하는 여러 태그들을 레시피에 추가합니다.
     * <p>
     *     이름에 해당하는 태그가 존재하지 않으면, 해당 태그의 Tag 엔티티를 생성한 후 레시피에 추가합니다.
     * </p>
     * @param names 레시피에 추가하려는 태그 이름 목록
     * @param recipe 레시피의 Recipe 엔티티
     */
    private void addTags(List<String> names, Recipe recipe) {
        if (names == null || names.isEmpty()) { return; }

        List<Tag> existingTags = tagRepository.findAllByNameIn(names);

        Map<String, Tag> existingTagMap = existingTags.stream()
                .collect(Collectors.toMap(Tag::getName, tag -> tag));

        // 사용자가 태그를 추가한 순서를 보장하기 위해, names 목록을 순회하며 레시피에 태그를 추가합니다.
        for (String name : names) {
            Tag tag = existingTagMap.get(name);

            // 해당 이름의 Tag가 없을 경우, 해당 이름으로 새 Tag 엔티티를 생성한 후 저장합니다.
            // 이미 해당 이름의 Tag가 존재한다면 바로 RecipeTag 엔티티를 생성해서, Recipe와 연관 관계를 설정합니다.
            if (tag == null) {
                tag = Tag.create(name);
                tagRepository.save(tag);
            }

            // Recipe를 저장할 때, RecipeTag도 함께 저장되도록 Recipe와 연관 관계를 설정합니다.
            recipe.addTag(tag);
        }
    }

    public RecipeOutboundResponse getUrl(Long recipeId) {
        Recipe recipe = recipeRepository.get(recipeId);
        return new RecipeOutboundResponse(recipe.getUrl());
    }
}
