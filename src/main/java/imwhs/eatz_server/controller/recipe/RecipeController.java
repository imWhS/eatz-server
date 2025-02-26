package imwhs.eatz_server.controller.recipe;

import imwhs.eatz_server.auth.EatzUserAuthUtil;
import imwhs.eatz_server.domain.recipe.RecipeItemSortType;
import imwhs.eatz_server.dto.ApiResponse;
import imwhs.eatz_server.dto.Paged;
import imwhs.eatz_server.dto.eatzuser.EatzUserBasicDto;
import imwhs.eatz_server.dto.recipe.*;
import imwhs.eatz_server.service.ingredient.IngredientRecipeService;
import imwhs.eatz_server.service.SavedRecipeService;
import imwhs.eatz_server.service.recipe.RecipeService;
import imwhs.eatz_server.service.query.RecipeQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * RecipeController 클래스입니다.
 */
@RequestMapping("/api/v0/recipes")
@RequiredArgsConstructor
@RestController
public class RecipeController {

    private final RecipeService recipeService;

    private final RecipeQueryService recipeQueryService;

    private final IngredientRecipeService ingredientRecipeService;

    private final SavedRecipeService savedRecipeService;

    @PostMapping
    public ResponseEntity<ApiResponse<Long>> registerRecipe(@RequestBody RecipeCreateDto dto) {
        Long recipeId = recipeService.registerRecipe(dto, EatzUserAuthUtil.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(recipeId));
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<?> updateRecipe(@PathVariable Long id, @RequestBody RecipeUpdateDto dto) {
        recipeService.updateRecipe(id, dto, EatzUserAuthUtil.getUsername());
        return ResponseEntity.ok().body(ApiResponse.success("레시피를 성공적으로 업데이트했어요."));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<?> deleteRecipe(@PathVariable Long id) {
        recipeService.markRecipeAsDeleted(id, EatzUserAuthUtil.getUsername());
        return ResponseEntity.ok().body(ApiResponse.success("레시피를 성공적으로 삭제했어요."));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<NRecipeDto>> getRecipe(@PathVariable Long id) {
        NRecipeDto dto = recipeQueryService.findRecipeById(id);
        return ResponseEntity.ok(ApiResponse.success(dto));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Paged<RecipeItemDto>>> getRecipeList(
            @PageableDefault(page = 0, size = 10) Pageable pageable,
            @RequestParam String title) {
        Page<RecipeItemDto> allRecipes = recipeQueryService.findRecipeItems(EatzUserAuthUtil.getId(), title, pageable);
        return ResponseEntity.ok(ApiResponse.success(allRecipes));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Paged<RecipeItemDto>>> searchRecipeList(
            @RequestParam(defaultValue = "LATEST") RecipeItemSortType sortType,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) List<Long> ingredientIds,
            @RequestParam(required = false) List<Long> exactIngredientIds,
            @PageableDefault(page = 0, size = 10) Pageable pageable) {
        Page<RecipeItemDto> allRecipes = recipeQueryService.searchRecipeItems(sortType, EatzUserAuthUtil.getId(), keyword, ingredientIds, exactIngredientIds, pageable);
        return ResponseEntity.ok(ApiResponse.success(allRecipes));
    }

    @PostMapping("/{id}/ingredients")
    public ResponseEntity<?> addIngredients(@PathVariable Long id, @RequestBody IngredientAddDto dto) {
        List<Long> addedIngredients = ingredientRecipeService.addAllIngredientsToRecipe(dto.getIngredientIds(), id);

        if (addedIngredients.size() != dto.getIngredientIds().size()) {
            return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT).body(ApiResponse.success(addedIngredients));
        } else if (addedIngredients.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error("레시피에 모든 재료를 추가하지 못했어요. " +
                    "레시피에 추가하려는 재료를 다시 확인해보세요."));
        }

        return ResponseEntity.ok().body(ApiResponse.success(addedIngredients));
    }

    @GetMapping("/{id}/saveds")
    public ResponseEntity<ApiResponse<List<EatzUserBasicDto>>> getSavedUsers(@PathVariable Long id) {
        return ResponseEntity.ok().body(ApiResponse.success(savedRecipeService.getSavedUsersByRecipe(id)));
    }

    @GetMapping("/{id}/saveds/count")
    public ResponseEntity<ApiResponse<Long>> getSavedUserCount(@PathVariable Long id) {
        Long savedUserCount = savedRecipeService.countSaveds(id);
        return ResponseEntity.ok(ApiResponse.success(savedUserCount));
    }

}
