package imwhs.eatz_server.controller.recipe;

import imwhs.eatz_server.auth.EatzUserAuthUtil;
import imwhs.eatz_server.dto.ApiResponse;
import imwhs.eatz_server.dto.Paged;
import imwhs.eatz_server.dto.comment.CommentCreateDto;
import imwhs.eatz_server.dto.eatzuser.EatzUserBasicDto;
import imwhs.eatz_server.dto.rating.RatingCreateDto;
import imwhs.eatz_server.dto.recipe.*;
import imwhs.eatz_server.service.CommentService;
import imwhs.eatz_server.service.RatingService;
import imwhs.eatz_server.service.ingredient.IngredientRecipeService;
import imwhs.eatz_server.service.query.NSavedRecipeService;
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

    private final CommentService commentService;

    private final IngredientRecipeService ingredientRecipeService;

    private final NSavedRecipeService savedRecipeService;
    private final RatingService ratingService;

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
    public ResponseEntity<ApiResponse<Paged<NRecipeItemDto>>> getAllRecipeList(
            @PageableDefault(page = 0, size = 10) Pageable pageable
    ) {
        Page<NRecipeItemDto> allRecipes = recipeQueryService.findAllRecipeItems(EatzUserAuthUtil.getUsername(), pageable);
        return ResponseEntity.ok(ApiResponse.success(allRecipes));
    }


//    @GetMapping("/{id}/details")
//    public @ResponseBody ResponseEntity<ApiResponse<RecipeDetailDto>> getRecipeDetails(@PathVariable Long id) {
//        RecipeDetailDto dto = recipeQueryService.findRecipeDetailsById(id);
//        return ResponseEntity.ok(ApiResponse.success(dto));
//    }

    @PostMapping("/{id}/comments")
    public ResponseEntity<ApiResponse<Long>> addComment(@PathVariable Long id, @RequestBody CommentCreateDto dto) {
        Long commentId = commentService.registerComment(id, dto.getContent());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(commentId));
    }

    @PostMapping("/{id}/ingredients")
    public ResponseEntity<?> addIngredient(@PathVariable Long id, @RequestBody RecipeAddIngredientDto dto) {
        List<Long> addedIngredients = ingredientRecipeService.addAllIngredientsToRecipe(dto.getIngredientIds(), id);
        if (addedIngredients.size() != dto.getIngredientIds().size()) {
            return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT).body(ApiResponse.success(addedIngredients));
        } else if (addedIngredients.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error("레시피에 모든 재료를 추가하지 못했어요. 레시피에 추가하려는 재료를 다시 확인해보세요."));
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

    @PostMapping("/{id}/ratings")
    public ResponseEntity<ApiResponse<Long>> addRating(@PathVariable Long id, @RequestBody RatingCreateDto dto) {
        Long ratingId = ratingService.registerRating(id, EatzUserAuthUtil.getUsername(), dto.getScore(), dto.getContent());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(ratingId));
    }

}
