package imwhs.eatz_server.controller.api.recipe;

import imwhs.eatz_server.auth.EatzUserAuthUtil;
import imwhs.eatz_server.domain.liked.EntityType;
import imwhs.eatz_server.domain.recipe.RecipeItemSortType;
import imwhs.eatz_server.dto.*;
import imwhs.eatz_server.dto.apiresponse.ApiResponse;
import imwhs.eatz_server.dto.recipe.*;
import imwhs.eatz_server.dto.recipe.ingredient.AddIngredientDto;
import imwhs.eatz_server.service.ReportService;
import imwhs.eatz_server.service.ingredient.IngredientRecipeService;
import imwhs.eatz_server.service.recipe.RecipeService;
import imwhs.eatz_server.service.query.RecipeQueryService;
import imwhs.eatz_server.service.recipe.RecipeViewCountService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 레시피를 관리하기 위한 API를 제공하는 컨트롤러입니다.
 */
@RequestMapping("/api/v0/recipes")
@RequiredArgsConstructor
@RestController
public class RecipeController {

    private final RecipeService recipeService;

    private final RecipeQueryService recipeQueryService;

    private final IngredientRecipeService ingredientRecipeService;

    private final ReportService reportService;

    private final RecipeViewCountService recipeViewCountService;

    /**
     * 새 레시피를 등록합니다.
     * @param dto 등록하려는 레시피 관련 정보.
     * @return 생성된 레시피의 ID.
     */
    @PostMapping
    public Long registerRecipe(@RequestBody CreateRecipeDto dto) {
        Long recipeId = recipeService.register(dto, EatzUserAuthUtil.getUsername());
        return recipeId;
    }

    /**
     * 레시피를 업데이트합니다.
     * @param id 업데이트하려는 레시피의 ID.
     * @param dto 업데이트하려는 레시피 관련 정보.
     */
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ApiResponse<?> updateRecipe(@PathVariable Long id, @RequestBody UpdateRecipeDto dto) {
        recipeService.update(id, dto, EatzUserAuthUtil.getUsername());
        return ApiResponse.success();
    }

    /**
     * 레시피를 삭제 처리합니다.
     * <p>레시피를 등록한 사용자만 접근 가능합니다.</p>
     * @param id 삭제 처리하려는 레시피의 ID.
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ApiResponse<?> deleteRecipe(@PathVariable Long id) {
        recipeService.markAsDeleted(id, EatzUserAuthUtil.getUsername());
        return ApiResponse.success();
    }

    /**
     * 레시피에 재료를 추가합니다.
     * @param id 레시피의 ID.
     * @param dto 추가하려는 재료 정보.
     */
    @PostMapping("/{id}/ingredients")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ApiResponse<?>> addIngredients(@PathVariable Long id, @RequestBody AddIngredientDto dto) {
        List<Long> addedIngredients = ingredientRecipeService.addIngredientsToRecipe(dto.getIngredientIds(), id);

        if (addedIngredients.size() != dto.getIngredientIds().size()) {
            return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(addedIngredients, "일부 재료만 추가됐어요."));
        } else if (addedIngredients.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error("모든 재료를 추가하지 못했어요. 유효한 재료가 존재하지 않아요."));
        }

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(addedIngredients, "모든 재료를 추가했어요."));
    }

    /**
     * 레시피를 신고합니다.
     * @param id 신고하려는 레시피의 ID.
     * @param dto 신고 정보.
     * @return 추가된 신고 ID.
     */
    @PostMapping("/{id}/report")
    @ResponseStatus(HttpStatus.CREATED)
    public Long reportRecipe(@PathVariable Long id, @RequestBody CreateBasicReportDto dto) {
        Long reportId = reportService.register(EatzUserAuthUtil.getId(), id, EntityType.RECIPE, dto.getContent());
        return reportId;
    }

    /**
     * 레시피를 조회합니다.
     * @param id 조회하려는 레시피의 ID.
     * @return 레시피 정보.
     */
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public RecipeDto findRecipe(
            HttpServletRequest request,
            HttpServletResponse response,
            @PathVariable Long id) {
        RecipeDto dto = recipeQueryService.findById(id, EatzUserAuthUtil.getId());
        recipeViewCountService.increaseViewCount(request, response, id);
        return dto;
    }

    /**
     * 필터링을 적용해 레시피를 검색하거나, 모든 레시피 목록을 조회합니다.
     * @param sortType 레시피 정렬 기준.
     * @param keyword 레시피를 필터링 할 제목 및 내용 키워드.
     * @param categoryId 레시피를 필터링 할 카테고리 ID.
     * @param ingredientIds 레시피를 필터링 할 재료 ID 목록.
     * @param requiredIngredientIds 특정 재료 집합과 일치하는 레시피만 필터링하기 위한 재료 ID 목록.
     * @param pageable 페이징 정보.
     * @return 레시피 목록.
     */
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Page<RecipeItemDto> searchRecipes(
            @RequestParam(required = false) RecipeItemSortType sortType,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) List<Long> ingredientIds,
            @RequestParam(required = false) List<Long> requiredIngredientIds,
            @PageableDefault(page = 0, size = 10) Pageable pageable) {
        if (sortType == null) sortType = RecipeItemSortType.LATEST;
        Page<RecipeItemDto> allRecipes = recipeQueryService.search(sortType, EatzUserAuthUtil.getId(), keyword, categoryId, ingredientIds, requiredIngredientIds, pageable);
        return allRecipes;
    }

}
