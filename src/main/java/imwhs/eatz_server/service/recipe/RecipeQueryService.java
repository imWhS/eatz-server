package imwhs.eatz_server.service.recipe;

import imwhs.eatz_server.domain.eatzuser.EatzUser;
import imwhs.eatz_server.domain.recipe.Recipe;
import imwhs.eatz_server.dto.ingredient.IngredientEssentialDto;
import imwhs.eatz_server.dto.kitchenware.KitchenwareEssentialDto;
import imwhs.eatz_server.dto.recipe.*;
import imwhs.eatz_server.dto.recipe.cookable.CookableRecipeDto;
import imwhs.eatz_server.dto.recipe.cookable.CookableRecipesRequest;
import imwhs.eatz_server.dto.recipe.explore.ExploreRecipeDto;
import imwhs.eatz_server.dto.recipe.explore.ExploreRecipesRequest;
import imwhs.eatz_server.exception.RecipeNotFoundException;
import imwhs.eatz_server.repository.EatzUserRepository;
import imwhs.eatz_server.repository.blocked.BlockedRepository;
import imwhs.eatz_server.repository.recipe.ingredient.RecipeIngredientRepository;
import imwhs.eatz_server.repository.pantry.PantryIngredientRepository;
import imwhs.eatz_server.repository.recipe.kitchenware.RecipeKitchenwareRepository;
import imwhs.eatz_server.repository.pantry.PantryKitchenwareRepository;
import imwhs.eatz_server.repository.tag.RecipeTagRepository;
import imwhs.eatz_server.repository.recipe.RecipeRepository;
import imwhs.eatz_server.service.ingredient.IngredientQueryService;
import imwhs.eatz_server.service.kitchenware.KitchenwareQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * 레시피(Recipe) 관련 정보를 조회하기 위한 서비스입니다.
 * Recipe에 대한 읽기 전용 쿼리 메서드를 제공합니다.
 */
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class RecipeQueryService {

    private final RecipeRepository recipeRepository;
    private final RecipeIngredientRepository recipeIngredientRepository;
    private final RecipeKitchenwareRepository recipeKitchenwareRepository;
    private final RecipeTagRepository recipeTagRepository;
    private final PantryIngredientRepository pantryIngredientRepository;
    private final PantryKitchenwareRepository pantryKitchenwareRepository;
    private final EatzUserRepository userRepository;
    private final IngredientQueryService ingredientQueryService;
    private final KitchenwareQueryService kitchenwareQueryService;
    private final BlockedRepository blockedRepository;

    /**
     * 레시피의 ID로 레시피의 상세한 정보와 연관 관계 정보를 가져옵니다.
     * <ul>
     *     <li> 사용자의 ID를 전달받으면, 조회하려는 레시피에 대한 해당 사용자의 context를 포함합니다. </li>
     *     <li> 차단한 사용자의 레시피는 조회 대상에서 제외합니다. </li>
     * </ul>
     * @param id 레시피의 ID
     * @param userId 사용자의 ID
     * @return 조회된 레시피의 상세한 정보와 연관 관계 정보
     * @throws RecipeNotFoundException ID에 해당하는 레시피가 존재하지 않는 경우
     */
    public RecipeDetailDto getDetail(Long id, Long userId) {
        // DTO 프로젝션을 통해 레시피의 상세한 정보 및 연관 관계 데이터를 가져옵니다.
        // 단, 레시피가 속한 태그(RecipeTag) 등 1:N 연관 관계의 컬렉션 필드는 카테시안 곱을 방지하고,
        // 쿼리 성능을 최적화하기 위해 별도로 조회합니다.
        RecipeDetailDto recipeDetailDto = recipeRepository.findDetail(id, userId).orElseThrow(
                () -> new RecipeNotFoundException(id));
        recipeTagRepository.findAllEssentialsByRecipeId(id).forEach(recipeTag -> {
            recipeDetailDto.getTags().add(recipeTag);
        });

        return recipeDetailDto;
    }

    /**
     * 레시피의 ID로 작성자가 레시피를 업데이트하기 위한 정보(draft)를 가져옵니다.
     * @param id 레시피의 ID
     * @param authorId 작성자의 ID
     * @return 조회된 레시피를 업데이트하기 위한 정보
     */
    public RecipeEditableDto getEditable(Long id, Long authorId) {
        Recipe recipe = recipeRepository.get(id);
        recipe.verifyAuthor(authorId);
        RecipeEditableDto draftDto = RecipeEditableDto.from(recipe);

        recipeTagRepository.findAllEssentialsByRecipeId(id).forEach(recipeTag -> {
            draftDto.getTagNames().add(recipeTag.getName());
        });

        List<IngredientEssentialDto> ingredients = ingredientQueryService
                .getIngredientRequirementsByRecipeId(id, authorId).stream()
                .map(dto ->
                        new IngredientEssentialDto(dto.getId(), dto.getName())).toList();
        draftDto.setIngredients(ingredients);

        List<KitchenwareEssentialDto> kitchenwares = kitchenwareQueryService
                .getKitchenwareRequirementsByRecipeId(id, authorId).stream()
                .map(dto ->
                        new KitchenwareEssentialDto(dto.getId(), dto.getName(), dto.getImageUrl())).toList();
        draftDto.setKitchenwares(kitchenwares);

        return draftDto;
    }

    /**
     * 레시피 ID로 레시피의 핵심 정보와 작성자 정보를 함께 가져옵니다.
     * @param id 레시피의 ID
     * @return 조회된 레시피의 상세한 정보와 연관 관계 데이터
     * @throws RecipeNotFoundException ID에 해당하는 레시피가 존재하지 않는 경우
     */
    public RecipeEssentialWithAuthorDto getEssentialWithAuthor(Long id) {
        return recipeRepository.findEssentialByIdWithAuthor(id).orElseThrow(
                () -> new RecipeNotFoundException(id));
    }

    /**
     * 레시피의 기본 정보 목록을 가져옵니다.
     * <ul>
     *     <li> 키워드(제목, 설명 검색어)를 전달받으면, 키워드로 검색된 레시피 기본 정보만 컬렉션에 포함합니다. </li>
     *     <li> 키워드를 전달받지 않으면, 전체 레시피 기본 정보 목록을 가져옵니다. </li>
     *     <li> 사용자의 ID를 전달받으면, 조회하려는 레시피에 대한 해당 사용자의 context를 포함합니다. </li>
     * </ul>
     * @param keyword 키워드
     * @param userId 사용자의 ID를
     * @param pageable 페이징 정보
     * @return 레시피의 기본 정보 목록
     */
    public Page<RecipeBasicDto> getAllBasics(String keyword, Long userId, Pageable pageable) {
        return recipeRepository.findAllBasics(keyword, userId, pageable);
    }

    /**
     * 특정 사용자가 작성한 모든 레시피의 기본 정보 목록을 가져옵니다.
     * <ul>
     *     <li> 조회하려는 레시피에 대해 ID에 해당하는 사용자의 context를 포함합니다. </li>
     * </ul>
     * @param id 작성자의 ID
     * @param pageable 페이징 정보
     * @return 조회된 레시피의 기본 정보 목록과 페이징 정보
     */
    public Page<RecipeBasicDto> getAllBasicsByAuthorId(Long id, Pageable pageable) {
        userRepository.validateExists(id);
        return recipeRepository.findAllBasicsByAuthorId(id, pageable);
    }

    /**
     * 특정 사용자가 좋아하는 모든 레시피의 기본 정보 목록을 가져옵니다.
     * <ul>
     *     <li> 조회하려는 레시피에 대해 ID에 해당하는 사용자의 context를 포함합니다. </li>
     *     <li> 차단한 사용자의 레시피는 조회 대상에서 제외합니다. </li>
     * </ul>
     * @param id 사용자의 ID
     * @param pageable 페이징 정보
     * @return 조회된 레시피의 기본 정보 목록과 페이징 정보
     */
    public Page<RecipeBasicDto> getAllLikedBasicsByUserId(Long id, Pageable pageable) {
        userRepository.validateExists(id);
        return recipeRepository.findAllLikedBasicsByUserId(id, pageable);
    }

    /**
     * '둘러보기(Explore)'에서 사용할 레시피 목록을 조건에 맞춰 가져옵니다.
     * <ul>
     *     <li> 사용자의 ID를 전달받으면, 조회하려는 레시피에 대한 해당 사용자의 context를 포함합니다. </li>
     *     <li> 차단한 사용자의 레시피는 조회 대상에서 제외합니다. </li>
     * </ul>
     * @param request 검색 키워드, 필터링 등의 선택 조건, 정렬 조건을 포함하는 요청 DTO
     * @param userId 사용자의 ID. 게스트 사용자일 경우 null을 전달합니다.
     * @param pageable 페이징 정보
     * @return '둘러보기(Explore)'에서 사용할 레시피 기본 정보 목록과 페이징 정보
     */
    public Page<ExploreRecipeDto> getExploreRecipes(
            ExploreRecipesRequest request,
            Long userId,
            Pageable pageable) {
        return recipeRepository.findAllExploreRecipes(request, userId, pageable);
    }

    /**
     * '지금 요리(Cookable)'에서 사용할 레시피 목록을 조건에 맞춰 가져옵니다.
     * <ul>
     *     <li> 사용자의 ID를 전달받으면, 조회하려는 레시피에 대한 해당 사용자의 context를 포함합니다. </li>
     *     <li> 차단한 사용자의 레시피는 조회 대상에서 제외합니다. </li>
     * </ul>
     * @param request 검색 키워드, 필터링, 지금 요리 가능한 레시피만 포함 여부 등의 선택 조건, 정렬 조건을 포함하는 요청 DTO
     * @param userId 사용자의 ID. 게스트 사용자일 경우 null을 전달합니다.
     * @param pageable 페이징 정보
     * @return '지금 요리(Cookable)'에서 사용할 레시피 기본 정보 목록과 페이징 정보
     */
    public Page<CookableRecipeDto> getCookableRecipes(
            CookableRecipesRequest request,
            Long userId,
            Pageable pageable) {
        List<Long> ingredientIdsByUser = Collections.emptyList();
        List<Long> kitchenwareIdsByUser = Collections.emptyList();

        // 사용자 ID로 요청한 사용자 정보를 조회합니다.
        if (userId != null) {
            EatzUser user = userRepository.get(userId);

            // 사용자가 보관함에 추가한 모든 재료, 도구 별 ID 목록을 조회해 컬렉션으로 생성합니다.
            if (user != null) {
                ingredientIdsByUser = pantryIngredientRepository.findAllIngredientIdsByUser(user);
                kitchenwareIdsByUser = pantryKitchenwareRepository.findAllKitchenwareIdsByUser(user);
            }
        }

        // 검색 조건, 정렬, 페이징 등이 모두 적용된 레시피 목록을 가져옵니다.
        // 레시피 별로 요리하기 위해 필요한 준비물에 해당하는 IngredientRecipe, KitchenwareRecipe 관련 필드는 별도의 쿼리로 가져옵니다.
        Page<CookableRecipeDto> pagedDtos = recipeRepository.findAllCookableRecipes(
                request,
                userId,
                ingredientIdsByUser,
                kitchenwareIdsByUser,
                pageable);

        List<CookableRecipeDto> dtos = pagedDtos.getContent();

        // 필요한 준비물이 1개 이상 있어, 바로 요리할 수 없는 레시피 ID만 필터링해서 컬렉션으로 매핑합니다.
        List<Long> notCookableRecipeIds = dtos
                .stream()
                .filter(dto ->
                        (0 < dto.getMissingIngredientCount() + dto.getMissingKitchenwareCount()))
                .map(CookableRecipeDto::getId)
                .toList();

        // 바로 요리할 수 없는 레시피가 없는 경우, 바로 레시피 목록을 반환합니다.
        if (notCookableRecipeIds.isEmpty()) { return pagedDtos; }

        // 레시피 별 부족한 재료의 핵심 정보 컬렉션을 가져옵니다.
        Map<Long, List<IngredientEssentialDto>> missingIngredientsByRecipeId =
                recipeIngredientRepository.findAllMissingEssentials(
                        notCookableRecipeIds,
                        ingredientIdsByUser);

        // 레시피 별 부족한 도구의 핵심 정보 컬렉션을 가져옵니다.
        Map<Long, List<KitchenwareEssentialDto>> missingKitchenwaresByRecipeId =
                recipeKitchenwareRepository.findAllMissingEssentials(
                    notCookableRecipeIds,
                    kitchenwareIdsByUser);

        // 메서드에서 처음 가져왔던 레시피 목록의 IngredientRecipe, KitchenwareRecipe 관련 필드에 초기화합니다.
        // 특정 레시피를 요리하기 위해 부족한 재료 또는 도구가 없을 경우, 빈 컬렉션을 생성한 후 할당합니다.
        for (CookableRecipeDto dto : dtos) {
            dto.setMissingIngredients(missingIngredientsByRecipeId.getOrDefault(dto.getId(), Collections.emptyList()));
            dto.setMissingKitchenwares(missingKitchenwaresByRecipeId.getOrDefault(dto.getId(), Collections.emptyList()));
        }

        return pagedDtos;
    }

}
