package imwhs.eatz_server.controller;

import imwhs.eatz_server.auth.EatzUserAuthUtil;
import imwhs.eatz_server.dto.ApiResponse;
import imwhs.eatz_server.dto.eatzuser.EatzUserBasicDto;
import imwhs.eatz_server.dto.recipe.RecipeDto;
import imwhs.eatz_server.dto.recipe.RecipeSaveDto;
import imwhs.eatz_server.dto.recipe.RecipeUnsaveDto;
import imwhs.eatz_server.service.query.NSavedRecipeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/v0/me/saved")
@RequiredArgsConstructor
@RestController
public class SavedController {

    private final NSavedRecipeService savedRecipeService;

    @PostMapping
    public ResponseEntity<ApiResponse<Long>> saveRecipe(@RequestBody RecipeSaveDto dto) {
        Long savedRecipeId = savedRecipeService.save(dto.getRecipeId(), EatzUserAuthUtil.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(savedRecipeId));
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse<Long>> unsaveRecipe(@RequestBody RecipeUnsaveDto dto) {
        savedRecipeService.unsave(dto.getRecipeId(), EatzUserAuthUtil.getUsername());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/recipes")
    public ResponseEntity<ApiResponse<List<RecipeDto>>> getRecipes() {
        List<RecipeDto> recipes = savedRecipeService.getSavedRecipesByUser(EatzUserAuthUtil.getUsername());
        return ResponseEntity.ok(ApiResponse.success(recipes));
    }

    // TODO: 디버깅 API
    @GetMapping("/recipes/tmp/count/{id}")
    public ResponseEntity<ApiResponse<Long>> getSavedsCountOfRecipe(@PathVariable Long id) {
        Long count = savedRecipeService.countSaveds(id);
        return ResponseEntity.ok(ApiResponse.success(count));
    }

    // TODO: 디버깅 API
    @GetMapping("/recipes/tmp")
    public ResponseEntity<ApiResponse<Boolean>> isRecipeSaved(@RequestParam Long id, @RequestParam String username) {
        boolean isRecipeSaved = savedRecipeService.isRecipeSaved(id, username);
        return ResponseEntity.ok(ApiResponse.success(isRecipeSaved));
    }

    // TODO: 관리자 API
    @GetMapping("/recipes/tmp/{username}")
    public ResponseEntity<ApiResponse<List<RecipeDto>>> getRecipesByUsername(@PathVariable String username) {
        List<RecipeDto> recipes = savedRecipeService.getSavedRecipesByUser(username);
        return ResponseEntity.ok(ApiResponse.success(recipes));
    }


    @GetMapping("/recipes/tmp/saved-users/{id}")
    public ResponseEntity<ApiResponse<List<EatzUserBasicDto>>> getSavedUsersOfRecipe(@PathVariable Long id) {
        List<EatzUserBasicDto> savedUsers = savedRecipeService.getSavedUsersByRecipeId(id);
        return ResponseEntity.ok(ApiResponse.success(savedUsers));

    }


}
