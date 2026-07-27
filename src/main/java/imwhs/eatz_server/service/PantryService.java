package imwhs.eatz_server.service;

import imwhs.eatz_server.domain.Ingredient;
import imwhs.eatz_server.domain.eatzuser.EatzUser;
import imwhs.eatz_server.domain.pantry.PantryIngredient;
import imwhs.eatz_server.domain.pantry.PantryKitchenware;
import imwhs.eatz_server.domain.Kitchenware;
import imwhs.eatz_server.dto.plan.ChecklistRequirementsDto;
import imwhs.eatz_server.repository.EatzUserRepository;
import imwhs.eatz_server.repository.pantry.PantryIngredientRepository;
import imwhs.eatz_server.repository.ingredient.IngredientRepository;
import imwhs.eatz_server.repository.kitchenware.KitchenwareRepository;
import imwhs.eatz_server.repository.pantry.PantryKitchenwareRepository;
import imwhs.eatz_server.repository.recipe.ingredient.RecipeIngredientRepository;
import imwhs.eatz_server.repository.recipe.kitchenware.RecipeKitchenwareRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class PantryService {

    private final PantryIngredientRepository pantryIngredientRepository;
    private final PantryKitchenwareRepository pantryKitchenwareRepository;
    private final IngredientRepository ingredientRepository;
    private final KitchenwareRepository kitchenwareRepository;
    private final EatzUserRepository userRepository;
    private final RecipeIngredientRepository recipeIngredientRepository;
    private final RecipeKitchenwareRepository recipeKitchenwareRepository;

    /**
     * 사용자의 보관함에 재료를 추가합니다.
     * @param id 재료의 ID
     * @param userId 사용자의 ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void addIngredient(Long id, Long userId) {
        addIngredients(List.of(id), userId);
    }

    /**
     * 사용자의 보관함에 도구를 추가합니다.
     * @param id 도구의 ID
     * @param userId 사용자의 ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void addKitchenware(Long id, Long userId) {
        addKitchenwares(List.of(id), userId);
    }

    /**
     * 사용자의 보관함에 1개 이상의 재료를 추가합니다.
     * @param ids 재료의 ID 목록
     * @param userId 사용자의 ID
     * @return 추가 완료한 재료의 ID 목록
     * @throws IllegalArgumentException 보관함에 추가하려는 재료의 ID 중 일부가 유효하지 않은 경우
     */
    @Transactional(rollbackFor = Exception.class)
    public List<Long> addIngredients(List<Long> ids, Long userId) {
        if (ids == null || ids.isEmpty()) return new ArrayList<>();

        EatzUser user = userRepository.getReference(userId);
        Set<Long> idSet = new HashSet<>(ids);

        List<Ingredient> ingredients = ingredientRepository.findAllById(idSet);
        if (ingredients.size() != idSet.size()) {
            throw new IllegalArgumentException("보관함에 추가하려는 재료 중 일부가 유효하지 않아요.");
        }

        List<PantryIngredient> pantryIngredients = ingredients.stream()
                .map(ingredient -> PantryIngredient.create(ingredient, user)).toList();

        pantryIngredientRepository.saveAll(pantryIngredients);
        return new ArrayList<>(idSet);
    }

    /**
     * 사용자의 보관함에 1개 이상의 도구를 추가합니다.
     * @param ids 도구의 ID 목록
     * @param userId 사용자의 ID
     * @return 추가 완료한 도구의 ID 목록
     * @throws IllegalArgumentException 보관함에 추가하려는 도구의 ID 중 일부가 유효하지 않은 경우
     */
    @Transactional(rollbackFor = Exception.class)
    public List<Long> addKitchenwares(List<Long> ids, Long userId) {
        if (ids == null || ids.isEmpty()) return new ArrayList<>();

        EatzUser user = userRepository.getReference(userId);
        Set<Long> idSet = new HashSet<>(ids);

        List<Kitchenware> kitchenwares = kitchenwareRepository.findAllById(idSet);
        if (kitchenwares.size() != idSet.size()) {
            throw new IllegalArgumentException("보관함에 추가하려는 도구 중 일부가 유효하지 않아요.");
        }

        List<PantryKitchenware> pantryKitchenwares = kitchenwares.stream()
                .map(kitchenware -> PantryKitchenware.create(kitchenware, user)).toList();

        pantryKitchenwareRepository.saveAll(pantryKitchenwares);
        return new ArrayList<>(idSet);
    }

    /**
     * 사용자의 보관함에서 재료를 제거합니다.
     * @param ids 재료의 ID 목록
     * @param userId 사용자의 ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void removeIngredients(List<Long> ids, Long userId) {
        if (ids == null || ids.isEmpty()) return;
        EatzUser user = userRepository.getReference(userId);
        pantryIngredientRepository.deleteByUserAndIngredientIds(user, ids);
    }

    /**
     * 사용자의 보관함에서 도구를 제거합니다.
     * @param ids 도구의 ID 목록
     * @param userId 사용자의 ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void removeKitchenwares(List<Long> ids, Long userId) {
        if (ids == null || ids.isEmpty()) return;
        EatzUser user = userRepository.getReference(userId);
        pantryKitchenwareRepository.deleteByUserAndKitchenwareIds(user, ids);
    }

    /**
     * 사용자의 보관함 속 모든 재료를 제거합니다.
     * @param userId 사용자의 ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void clearIngredients(Long userId) {
        EatzUser user = userRepository.getReference(userId);
        pantryIngredientRepository.deleteAllByUser(user);
    }

    /**
     * 사용자의 보관함 속 모든 도구를 제거합니다.
     * @param userId 사용자의 ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void clearKitchenwares(Long userId) {
        EatzUser user = userRepository.getReference(userId);
        pantryKitchenwareRepository.deleteAllByUser(user);
    }

    /**
     * 체크리스트가 요구하는 모둔 준비물을 사용자의 보관함에 추가합니다.
     */
    @Transactional(rollbackFor = Exception.class)
    public void addAllChecklistRequirementsToPantry(ChecklistRequirementsDto dto, Long userId) {
        EatzUser user = userRepository.getReference(userId);

        // 체크리스트가 요구하는 재료, 도구 ID 목록: 필터링 후 추가할 대상
        List<Long> requiredIngredientIds = dto.getIngredients().stream().map(
                ingredient -> ingredient.getId()).toList();
        List<Long> requiredKitchenwareIds = dto.getKitchenwares().stream().map(
                kitenware -> kitenware.getId()).toList();

        addAllRequirementsToPantry(user, requiredIngredientIds, requiredKitchenwareIds);
    }

    /**
     * 특정 레시피의 모든 준비물을 사용자의 보관함에 추가합니다.
     * @param recipeId 레시피의 ID
     * @param userId 사용자의 ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void addAllRecipeRequirementsToPantry(Long recipeId, Long userId) {
        EatzUser user = userRepository.getReference(userId);

        // 레시피가 요구하는 재료, 도구 ID 목록: 필터링 후 추가할 대상
        List<Long> requiredIngredientIds = recipeIngredientRepository.findAllIngredientIdsByRecipeId(recipeId);
        List<Long> requiredKitchenwareIds = recipeKitchenwareRepository.findAllKitchenwareIdsByRecipeId(recipeId);

        addAllRequirementsToPantry(user, requiredIngredientIds, requiredKitchenwareIds);
    }

    private void addAllRequirementsToPantry(EatzUser user, List<Long> ingredientIds, List<Long> kitchenwareIds) {
        // 사용자가 보관함에 추가한 재료, 도구 ID 목록 -> 모두 불러오지 말고, 레시피가 요구하는 재료, 도구에 대해서만 가져오기
        Set<Long> ingredientIdsInPantry = ingredientIds.isEmpty()
                ? Collections.emptySet()
                : pantryIngredientRepository.findExistingIngredientIdsByUserId(user.getId(), ingredientIds);
        Set<Long> kitchenwareIdsInPantry = kitchenwareIds.isEmpty()
                ? Collections.emptySet()
                : pantryKitchenwareRepository.findExistingKitchenwareIdsByUserId(user.getId(), kitchenwareIds);

        List<PantryIngredient> ingredients = ingredientIds.stream()
                .filter(ingredientId -> !ingredientIdsInPantry.contains(ingredientId))
                .map(id -> PantryIngredient.create(ingredientRepository.getReferenceById(id), user))
                .toList();

        List<PantryKitchenware> kitchenwares = kitchenwareIds.stream()
                .filter(kitchenareId -> !kitchenwareIdsInPantry.contains(kitchenareId))
                .map(id -> PantryKitchenware.create(kitchenwareRepository.getReferenceById(id), user))
                .toList();

        pantryIngredientRepository.saveAll(ingredients);
        pantryKitchenwareRepository.saveAll(kitchenwares);
    }

}
