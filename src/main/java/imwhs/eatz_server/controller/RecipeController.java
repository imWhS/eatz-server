package imwhs.eatz_server.controller;

import imwhs.eatz_server.domain.Recipe;
import imwhs.eatz_server.dto.ApiResponse;
import imwhs.eatz_server.dto.Paged;
import imwhs.eatz_server.dto.recipe.RecipeCreateDto;
import imwhs.eatz_server.dto.recipe.RecipeDetailDto;
import imwhs.eatz_server.dto.recipe.RecipeDto;
import imwhs.eatz_server.service.RecipeService;
import imwhs.eatz_server.service.query.RecipeQueryService;
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
    public @ResponseBody ResponseEntity<ApiResponse<RecipeDto>> getRecipe(@PathVariable Long id) {
        RecipeDto dto = recipeQueryService.findRecipeById(id);
        return ResponseEntity.ok(ApiResponse.success(dto));
    }

    @GetMapping("/{id}/details")
    public @ResponseBody ResponseEntity<ApiResponse<RecipeDetailDto>> getRecipeDetails(@PathVariable Long id) {
        RecipeDetailDto dto = recipeQueryService.findRecipeDetailsById(id);
        return ResponseEntity.ok(ApiResponse.success(dto));
    }

}
