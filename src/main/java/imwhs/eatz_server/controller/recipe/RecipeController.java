package imwhs.eatz_server.controller.recipe;

import imwhs.eatz_server.domain.likes.LikesType;
import imwhs.eatz_server.dto.ApiResponse;
import imwhs.eatz_server.dto.Paged;
import imwhs.eatz_server.dto.comment.CommentCreateDto;
import imwhs.eatz_server.dto.likes.LikesDto;
import imwhs.eatz_server.dto.recipe.NRecipeDto;
import imwhs.eatz_server.dto.recipe.RecipeCreateDto;
import imwhs.eatz_server.dto.recipe.RecipeDetailDto;
import imwhs.eatz_server.dto.recipe.RecipeDto;
import imwhs.eatz_server.dto.recipe.savedrecipe.SavedRecipeCreateDto;
import imwhs.eatz_server.service.CommentService;
import imwhs.eatz_server.service.LikeService;
import imwhs.eatz_server.service.recipe.RecipeService;
import imwhs.eatz_server.service.query.RecipeQueryService;
import imwhs.eatz_server.service.recipe.SavedRecipeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/v0/recipes")
@RequiredArgsConstructor
@Controller
public class RecipeController {

    private final RecipeService recipeService;

    private final RecipeQueryService recipeQueryService;
    private final CommentService commentService;
    private final SavedRecipeService savedRecipeService;

    @PostMapping
    public @ResponseBody ResponseEntity<ApiResponse<Long>> registerRecipe(@RequestBody RecipeCreateDto dto) {
        Long recipeId = recipeService.registerRecipe(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(recipeId));
    }

    @GetMapping
    public @ResponseBody ResponseEntity<ApiResponse<Paged<RecipeDto>>> getAllRecipes(
            @PageableDefault(page = 0, size = 10) Pageable pageable
    ) {
        Page<RecipeDto> allRecipes = recipeQueryService.findAllRecipes(pageable);
        return ResponseEntity.ok(ApiResponse.success(allRecipes));
    }

    @GetMapping("/{id}")
    public @ResponseBody ResponseEntity<ApiResponse<NRecipeDto>> getRecipe(@PathVariable Long id) {
        NRecipeDto dto = recipeQueryService.findRecipeById(id);
        return ResponseEntity.ok(ApiResponse.success(dto));
    }

    @GetMapping("/{id}/details")
    public @ResponseBody ResponseEntity<ApiResponse<RecipeDetailDto>> getRecipeDetails(@PathVariable Long id) {
        RecipeDetailDto dto = recipeQueryService.findRecipeDetailsById(id);
        return ResponseEntity.ok(ApiResponse.success(dto));
    }

    @PostMapping("/{id}/comments")
    public @ResponseBody ResponseEntity<ApiResponse<Long>> registerComment(@PathVariable Long id, @RequestBody CommentCreateDto dto) {
        Long commentId = commentService.registerComment(id, dto.getContent());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(commentId));
    }

}
