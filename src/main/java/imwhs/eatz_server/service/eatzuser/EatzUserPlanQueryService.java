package imwhs.eatz_server.service.eatzuser;

import imwhs.eatz_server.domain.eatzuser.EatzUser;
import imwhs.eatz_server.domain.Ingredient;
import imwhs.eatz_server.domain.recipe.RecipeIngredient;
import imwhs.eatz_server.domain.Kitchenware;
import imwhs.eatz_server.domain.recipe.RecipeKitchenware;
import imwhs.eatz_server.dto.ingredient.IngredientEssentialDto;
import imwhs.eatz_server.dto.kitchenware.KitchenwareEssentialDto;
import imwhs.eatz_server.dto.plan.*;
import imwhs.eatz_server.repository.EatzUserRepository;
import imwhs.eatz_server.repository.pantry.PantryIngredientRepository;
import imwhs.eatz_server.repository.recipe.ingredient.RecipeIngredientRepository;
import imwhs.eatz_server.repository.pantry.PantryKitchenwareRepository;
import imwhs.eatz_server.repository.recipe.kitchenware.RecipeKitchenwareRepository;
import imwhs.eatz_server.repository.liked.LikedIngredientRepository;
import imwhs.eatz_server.repository.liked.LikedRecipeRepository;
import imwhs.eatz_server.repository.plan.PlanRepository;
import imwhs.eatz_server.repository.recipe.RecipeRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 사용자의 플랜(Plan) 관련 정보를 조회하기 위한 서비스입니다.
 * Plan에 대한 읽기 전용 쿼리 메서드를 제공합니다.
 */
@Transactional(readOnly = true)
@AllArgsConstructor
@Service
public class EatzUserPlanQueryService {

    private final PlanRepository planRepository;
    private final RecipeRepository recipeRepository;
    private final EatzUserRepository userRepository;
    private final RecipeIngredientRepository recipeIngredientRepository;
    private final RecipeKitchenwareRepository recipeKitchenwareRepository;
    private final PantryIngredientRepository pantryIngredientRepository;
    private final PantryKitchenwareRepository pantryKitchenwareRepository;
    private final LikedRecipeRepository likedRecipeRepository;
    private final LikedIngredientRepository likedIngredientRepository;

    /**
     * 사용자가 등록한 모든 플랜의 상세한 정보 목록을 가져옵니다.
     * <ul>
     *     <li> 차단한 사용자의 레시피가 추가되어 있는 플랜은 조회 대상에서 제외합니다. </li>
     * </ul>
     * @param id 사용자의 ID
     * @param startDate 기간의 첫 날짜
     * @param endDate 기간의 마지막 날짜
     * @return 모든 플랜의 상세한 정보 목록
     */
    public List<PlanDetailDto> getAllDetailsByUserId(
            Long id,
            LocalDateTime startDate,
            LocalDateTime endDate) {
        return planRepository.findAllDetailsByUserId(id, startDate, endDate);
    }

    /**
     * 사용자가 등록한 모든 플랜의 날짜 목록을 가져옵니다.
     * <ul>
     *     <li> 차단한 사용자의 레시피가 추가되어 있는 플랜은 조회 대상에서 제외합니다. </li>
     * </ul>
     * @param userId 사용자의 ID
     * @param recipeId 레시피의 ID
     * @param startDate 기간의 첫 날짜
     * @param endDate 기간의 마지막 날짜
     * @return 모든 플랜의 날짜 목록
     */
    public PlannedDatesDto getPlannedDatesForRecipe(
            Long userId,
            Long recipeId,
            LocalDateTime startDate,
            LocalDateTime endDate) {
        userRepository.validateExists(userId);
        recipeRepository.validateExists(recipeId);
        List<LocalDateTime> plannedDates = planRepository.findScheduledDatesByUserIdAndRecipeId(
                userId, recipeId, startDate, endDate);
        return new PlannedDatesDto(plannedDates);
    }

    /**
     * 사용자가 특정 기간 내 추가한 플랜에 대한 체크리스트를 처리한 후 가져옵니다.
     * <ul>
     *     <li> 체크리스트는 사용자가 플랜으로 추가한 레시피와 사용자가 보관함에 추가한 재료, 도구를 분석해
     *          플랜 별 요리 가능 여부와 요리 불가능한 플랜에 대해 필요한 재료 목록 등을 제공합니다. </li>
     *     <li> 사용자가 기간 내에 플랜으로 추가한 레시피 목록과, 현재 보관함에 추가한 재료/도구 데이터를 비교, 분석합니다.
     *          이를 통해 지금 요리 가능한 플랜과 불가능한 플랜을 분류하고,
     *          요리를 위해 추가로 구비해야 할 준비물 목록과 부족한 수량을 집계 후 제공합니다. </li>
     *     <li> 차단한 사용자의 레시피가 추가되어 있는 플랜은 체크리스트 처리 대상에서 제외합니다. </li>
     * </ul>
     * @param userId 사용자의 ID
     * @param startDate 기간의 첫 날짜
     * @param endDate 기간의 마지막 날짜
     * @return 체크리스트
     */
    public ChecklistDto getChecklist(
            Long userId,
            LocalDateTime startDate,
            LocalDateTime endDate) {
        // ID에 해당하는 사용자가 유효한지 확인합니다.
        userRepository.validateExists(userId);

        // 사용자의 ID와 기간에 해당하는 모든 플랜 기본 정보 목록을 조회해 체크리스트 조회 대상 플랜 목록으로 사용합니다.
        List<PlanBasicDto> plans = planRepository.findAllBasicsByUserId(userId, startDate, endDate);

        // 체크리스트 조회 대상 플랜 목록에서 레시피의 ID만 추출합니다.
        List<Long> recipeIds = plans.stream().map(PlanBasicDto::getRecipeId).toList();

        // 레시피 ID 별 요구 재료, 도구 목록을 조회합니다.
        Map<Long, List<IngredientEssentialDto>> ingredientsByRecipeId =
                recipeIngredientRepository.findAllEssentialsByRecipeIds(recipeIds);
        Map<Long, List<KitchenwareEssentialDto>> kitchenwaresByRecipeId =
                recipeKitchenwareRepository.findAllEssentialsByRecipeIds(recipeIds);

        // 사용자가 보관함에 추가한 재료, 도구 ID 목록과 사용자가 좋아하는 재료 ID 목록을 조회합니다.
        Set<Long> ingredientIdsByUserId = pantryIngredientRepository.findAllIngredientIdsByUserId(userId);
        Set<Long> kitchenwareIdsByUserId = pantryKitchenwareRepository.findAllKitchenwareIdsByUserId(userId);
        Set<Long> likedIngredientIdsByUserId = likedIngredientRepository.findAllIngredientIdsByUserIdAndIsLikedIsTrue(userId);

        List<PlanBasicDto> cookablePlans = new ArrayList<>();
        List<PlanBasicDto> uncookablePlans = new ArrayList<>();

        // 플랜으로 추가한 레시피 별 요구 재료, 도구와 사용자가 추가한 재료, 도구를 비교해 요리 가능, 불가능 플랜을 분류합니다.
        for (PlanBasicDto plan : plans) {
            long recipeId = plan.getRecipeId();
            boolean isCookable = isCookableRecipe(
                    recipeId,
                    ingredientsByRecipeId,
                    kitchenwaresByRecipeId,
                    ingredientIdsByUserId,
                    kitchenwareIdsByUserId);
            if (isCookable) { cookablePlans.add(plan); }
            else { uncookablePlans.add(plan); }
        }

        Set<ChecklistIngredientDto> uncookableRequirementsIngredients = new HashSet<>();
        Set<ChecklistIngredientDto> cookableRequirementsIngredients = new HashSet<>();
        long missingIngredientCount = 0;

        Set<ChecklistKitchenwareDto> uncookableRequirementsKitchenwares = new HashSet<>();
        Set<ChecklistKitchenwareDto> cookableRequirementsKitchenwares = new HashSet<>();
        long missingKitchenwareCount = 0;

        // 요리 불가능한 플랜 별 요구 재료, 도구 목록을 순회하며 사용자의 보유 여부에 따라 누락 여부(isMissing)를 판별합니다.
        // 여러 레시피에서 공통으로 요구하는 항목이 체크리스트에 중복 표시되지 않도록 하기 위해, 중복이 제거된 준비물 집합으로 구성합니다.
        // 향후 재료 별 요구량 기능이 도입되면, 순회 시 중복를 제거하는 것 대신 요구량을 합산하는 흐름으로 확장할 수 있습니다.
        for (PlanBasicDto uncookablePlan : uncookablePlans) {
            Long uncookableRecipeId = uncookablePlan.getRecipeId();

            List<IngredientEssentialDto> uncookableRecipeIngredients =
                    ingredientsByRecipeId.getOrDefault(uncookableRecipeId, Collections.emptyList());
            for (IngredientEssentialDto ingredient : uncookableRecipeIngredients) {
                boolean isMissing = !(ingredientIdsByUserId.contains(ingredient.getId()));
                ChecklistIngredientDto checklistIngredient = new ChecklistIngredientDto(
                        ingredient.getId(),
                        ingredient.getName(),
                        isMissing,
                        likedIngredientIdsByUserId.contains(ingredient.getId())
                );
                uncookableRequirementsIngredients.add(checklistIngredient);
            }

            List<KitchenwareEssentialDto> uncookableRecipeKitchenwares =
                    kitchenwaresByRecipeId.getOrDefault(uncookableRecipeId, Collections.emptyList());
            for (KitchenwareEssentialDto kitchenware : uncookableRecipeKitchenwares) {
                boolean isMissing = !(kitchenwareIdsByUserId.contains(kitchenware.getId()));
                ChecklistKitchenwareDto checklistKitchenware = new ChecklistKitchenwareDto(
                        kitchenware.getId(),
                        kitchenware.getName(),
                        kitchenware.getImageUrl(),
                        isMissing
                );
                uncookableRequirementsKitchenwares.add(checklistKitchenware);
            }
        }

        // 요리 가능한 플랜 별 요구 재료, 도구 목록을 순회하며 사용자의 보유 여부에 따라 누락 여부(isMissing)를 판별합니다.
        // 여러 레시피에서 공통으로 요구하는 항목이 체크리스트에 중복 표시되지 않도록 하기 위해, 중복이 제거된 준비물 집합(Set) 컬렉션으로 구성합니다.
        for (PlanBasicDto cookablePlan : cookablePlans) {
            Long cookableRecipeId = cookablePlan.getRecipeId();

            List<IngredientEssentialDto> cookableRecipeIngredients =
                    ingredientsByRecipeId.getOrDefault(cookableRecipeId, Collections.emptyList());
            for (IngredientEssentialDto ingredient : cookableRecipeIngredients) {
                ChecklistIngredientDto checklistIngredient = new ChecklistIngredientDto(
                        ingredient.getId(),
                        ingredient.getName(),
                        false,
                        likedIngredientIdsByUserId.contains(ingredient.getId())
                );
                cookableRequirementsIngredients.add(checklistIngredient);
            }

            List<KitchenwareEssentialDto> cookableRecipeKitchenwares =
                    kitchenwaresByRecipeId.getOrDefault(cookableRecipeId, Collections.emptyList());
            for (KitchenwareEssentialDto kitchenware : cookableRecipeKitchenwares) {
                ChecklistKitchenwareDto checklistKitchenware = new ChecklistKitchenwareDto(
                        kitchenware.getId(),
                        kitchenware.getName(),
                        kitchenware.getImageUrl(),
                        false
                );
                cookableRequirementsKitchenwares.add(checklistKitchenware);
            }
        }

        // 중복이 제거된 요리 불가능 준비물 재료 목록에서 실제로 누락된(사용자가 보유하지 않은) 재료의 개수를 각각 집계합니다.
        missingIngredientCount = uncookableRequirementsIngredients
                .stream()
                .filter(ChecklistIngredientDto::isMissing)
                .count();

        // 중복이 제거된 요리 불가능 준비물 도구 목록에서 실제로 누락된(사용자가 보유하지 않은) 도구의 개수를 각각 집계합니다.
        missingKitchenwareCount = uncookableRequirementsKitchenwares
                .stream()
                .filter(ChecklistKitchenwareDto::isMissing)
                .count();

        // 분류된 요리 불가능 플랜 목록과 각각의 준비물 목록을 취합하여 그룹 별 DTO를 생성합니다.
        ChecklistCookabilityDto uncookable = new ChecklistCookabilityDto(
                uncookablePlans,
                new ChecklistRequirementsDto(
                        uncookableRequirementsIngredients,
                        uncookableRequirementsKitchenwares));

        // 분류된 요리 가능 플랜 목록과 각각의 준비물 목록을 취합하여 그룹 별 DTO를 생성합니다.
        ChecklistCookabilityDto cookable = new ChecklistCookabilityDto(
                cookablePlans,
                new ChecklistRequirementsDto(
                        cookableRequirementsIngredients,
                        cookableRequirementsKitchenwares));

        return new ChecklistDto(uncookable, cookable, missingIngredientCount, missingKitchenwareCount);
    }

    /**
     * 특정 기간에 설정된 레시피들에 대한 요리 가능, 불가능 여부와 요리 불가능한 레시피들에 대해 필요한 재료 목록을 가져옵니다.
     * @param userId 사용자의 ID
     * @param startDate 기간의 첫 날짜
     * @param endDate 기간의 마지막 날짜
     * @return ChecklistDto.
     */
    public ChecklistDto getChecklistOld(
            Long userId,
            LocalDateTime startDate,
            LocalDateTime endDate) {
        EatzUser user = userRepository.get(userId);

        // 사용자의 ID, 플래너의 기간에 해당하는 모든 Plan × Recipe를 조회합니다.
        List<PlanBasicDto> requiredRecipes = planRepository.findAllBasicsByUserId(userId, startDate, endDate);

        if (requiredRecipes.isEmpty()) {
            throw new IllegalArgumentException("올바르지 않은 요청이에요.");
        }

        // Plan × Recipe에서 모든 레시피의 ID들을 목록으로 추출합니다. 이후 레시피 별 연관 정보를 일괄 조회할 때 사용합니다.
        List<Long> recipeIds = requiredRecipes.stream().map(PlanBasicDto::getRecipeId)
                .toList();

        // recipeIds로 플래너 기간 내 레시피 별 연관 재료 목록, 도구 목록을 조회합니다.
        List<RecipeIngredient> recipeIngredients = recipeIngredientRepository.findAllByRecipeIdsOld(recipeIds);
        List<RecipeKitchenware> recipeKitchenwares = recipeKitchenwareRepository.findAllWithKitchenwareByRecipeIdsOld(recipeIds);

        Set<Long> likedIngredientIdsByUser = likedRecipeRepository.findAllRecipeIdsByUserIdAndIsLikedIsTrue(userId);

        // 레시피 ID 별 연관 재료와 도구를 빠르게 참조할 수 있도록, 레시피 ID로 그룹핑합니다.
        Map<Long, List<Ingredient>> ingredientsByRecipeId = recipeIngredients.stream()
                .collect(Collectors.groupingBy(
                        ingredientRecipe -> ingredientRecipe.getRecipe().getId(),
                        Collectors.mapping(RecipeIngredient::getIngredient, Collectors.toList())));
        Map<Long, List<Kitchenware>> kitchenwaresByRecipeId = recipeKitchenwares.stream()
                .collect(Collectors.groupingBy(
                        kitchenwareRecipe -> kitchenwareRecipe.getRecipe().getId(),
                        Collectors.mapping(RecipeKitchenware::getKitchenware, Collectors.toList())));


        // 사용자의 모든 연관 재료 목록, 모든 연관 도구 ID 목록을 조회합니다. 이후, 레시피 별 연관 재료 ID, 도구 ID를 이용해 사용자의 추가 여부를 확인할 때 사용합니다.
        Set<Long> ingredientIdsByUser = new HashSet<>(pantryIngredientRepository.findAllIngredientIdsByUser(user));
        HashSet<Long> kitchenwareIdsByUser = new HashSet<>(pantryKitchenwareRepository.findAllKitchenwareIdsByUser(user));

        // 모든 레시피를 순회하면서, 요리 가능 및 불가능 여부를 기준으로 분류합니다.
        List<PlanBasicDto> cookableRecipes = new ArrayList<>();
        List<PlanBasicDto> uncookableRecipes = new ArrayList<>();

        for (PlanBasicDto requiredRecipe : requiredRecipes) {
            boolean isCookable = true;
            Long recipeId = requiredRecipe.getRecipeId();

            for (Ingredient ingredient : ingredientsByRecipeId.getOrDefault(recipeId, Collections.emptyList())) {
                if (!ingredientIdsByUser.contains(ingredient.getId())) {
                    isCookable = false;
                    break;
                }
            }

            for (Kitchenware kitchenware : kitchenwaresByRecipeId.getOrDefault(recipeId, Collections.emptyList())) {
                if (!kitchenwareIdsByUser.contains(kitchenware.getId())) {
                    isCookable = false;
                    break;
                }
            }

            if (isCookable) {
                cookableRecipes.add(requiredRecipe);
            } else {
                uncookableRecipes.add(requiredRecipe);
            }
        }

        // 요리 가능 레시피의 모든 연관 재료, 도구 집합을 만듭니다.
        Set<Ingredient> requiredIngredientsOfCookableRecipes = new HashSet<>();
        Set<Kitchenware> requiredKitchenwaresOfCookableRecipes = new HashSet<>();

        for (PlanBasicDto recipe : cookableRecipes) {
            requiredIngredientsOfCookableRecipes.addAll(
                    ingredientsByRecipeId.getOrDefault(recipe.getRecipeId(), Collections.emptyList()));
            requiredKitchenwaresOfCookableRecipes.addAll(
                    kitchenwaresByRecipeId.getOrDefault(recipe.getRecipeId(), Collections.emptyList()));
        }

        // 사용자 보유 여부와 함께 ChecklistIngredientDto, ChecklistKitchenwareDto를 생성합니다.
        List<ChecklistIngredientDto> ingredientsOfCookableRecipes = requiredIngredientsOfCookableRecipes.stream()
                .map(ingredient -> new ChecklistIngredientDto(
                        ingredient,
                        !ingredientIdsByUser.contains(ingredient.getId()),
                        likedIngredientIdsByUser.contains(ingredient.getId())))
                .toList();
        List<ChecklistKitchenwareDto> kitchenwaresOfCookableRecipes = requiredKitchenwaresOfCookableRecipes.stream()
                .map(kitchenware -> new ChecklistKitchenwareDto(
                        kitchenware,
                        !kitchenwareIdsByUser.contains(kitchenware.getId())))
                .toList();

        ChecklistRequirementsDto requiredItemsOfCookableRecipes = new ChecklistRequirementsDto(
                new HashSet<>(ingredientsOfCookableRecipes), new HashSet<>(kitchenwaresOfCookableRecipes));
        ChecklistCookabilityDto cookable = new ChecklistCookabilityDto(cookableRecipes, requiredItemsOfCookableRecipes);

        // 요리 불가능 레시피의 모든 연관 재료, 도구 집합을 만듭니다.
        Set<Ingredient> requiredIngredientsOfUncookableRecipes = new HashSet<>();
        Set<Kitchenware> requiredKitchenwaresOfUncookableRecipes = new HashSet<>();

        for (PlanBasicDto recipe : uncookableRecipes) {
            requiredIngredientsOfUncookableRecipes.addAll(
                    ingredientsByRecipeId.getOrDefault(recipe.getRecipeId(), Collections.emptyList()));
            requiredKitchenwaresOfUncookableRecipes.addAll(
                    kitchenwaresByRecipeId.getOrDefault(recipe.getRecipeId(), Collections.emptyList()));
        }

        // 사용자 보유 여부와 함께 ChecklistIngredientDto를 생성합니다.
        List<ChecklistIngredientDto> ingredientsOfUncookableRecipes = requiredIngredientsOfUncookableRecipes.stream()
                .map(ingredient -> new ChecklistIngredientDto(
                        ingredient,
                        !ingredientIdsByUser.contains(ingredient.getId()),
                        likedIngredientIdsByUser.contains(ingredient.getId())))
                .toList();

        // 요리하기 위해 필요한 재료의 수를 추출합니다.
        long missingIngredientCount = ingredientsOfUncookableRecipes.stream()
                .filter(ChecklistIngredientDto::isMissing)
                .count();

        // 사용자 보유 여부와 함께 ChecklistKitchenwareDto를 생성합니다.
        List<ChecklistKitchenwareDto> kitchenwaresOfUncookableRecipes = requiredKitchenwaresOfUncookableRecipes.stream()
                .map(kitchenware -> new ChecklistKitchenwareDto(
                        kitchenware,
                        !kitchenwareIdsByUser.contains(kitchenware.getId())))
                .toList();

        // 요리하기 위해 필요한 도구의 수를 추출합니다.
        long missingKitchenwareCount = kitchenwaresOfUncookableRecipes.stream()
                .filter(ChecklistKitchenwareDto::isMissing)
                .count();

        ChecklistRequirementsDto requiredItemsOfUncookableRecipes = new ChecklistRequirementsDto(
                new HashSet<>(ingredientsOfUncookableRecipes), new HashSet<>(kitchenwaresOfUncookableRecipes));
        ChecklistCookabilityDto uncookable = new ChecklistCookabilityDto(
                uncookableRecipes,
                requiredItemsOfUncookableRecipes);

        return new ChecklistDto(uncookable, cookable, missingIngredientCount, missingKitchenwareCount);
    }

    /**
     * 레시피의 요구 재료, 도구와 사용자가 추가한 재료, 도구를 비교해 해당 레시피의 요리 가능 여부를 확인합니다.
     * @param recipeId
     * @param ingredientsByRecipeId
     * @param kitchenwaresByRecipeId
     * @param ingredientIdsByUserId
     * @param kitchenwareIdsByUserId
     * @return
     */
    private boolean isCookableRecipe(
            long recipeId,
            Map<Long, List<IngredientEssentialDto>> ingredientsByRecipeId,
            Map<Long, List<KitchenwareEssentialDto>> kitchenwaresByRecipeId,
            Set<Long> ingredientIdsByUserId,
            Set<Long> kitchenwareIdsByUserId
    ) {
        // 플랜으로 추가한 레시피 별 요구 재료, 도구와 사용자가 추가한 재료, 도구를 비교해 요리 가능, 불가능 플랜을 분류합니다.
        boolean isCookable;

        List<IngredientEssentialDto> requiredIngredients =
                ingredientsByRecipeId.getOrDefault(recipeId, Collections.emptyList());
        List<KitchenwareEssentialDto> requiredKitchenwares =
                kitchenwaresByRecipeId.getOrDefault(recipeId, Collections.emptyList());

        boolean isMatchedAllIngredients = requiredIngredients.stream().allMatch(
                requiredIngredient -> ingredientIdsByUserId.contains(requiredIngredient.getId()));
        boolean isMatchedAllKitchenwares = requiredKitchenwares.stream().allMatch(
                requiredKitchenware -> kitchenwareIdsByUserId.contains(requiredKitchenware.getId()));

        return isMatchedAllIngredients && isMatchedAllKitchenwares;
    }

}
