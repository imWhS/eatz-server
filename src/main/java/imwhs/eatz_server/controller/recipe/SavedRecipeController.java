package imwhs.eatz_server.controller.recipe;

import imwhs.eatz_server.dto.ApiResponse;
import imwhs.eatz_server.dto.recipe.savedrecipe.SavedRecipeCreateDto;
import imwhs.eatz_server.dto.recipe.savedrecipe.SavedRecipeDto;
import imwhs.eatz_server.service.query.SavedRecipeQueryService;
import imwhs.eatz_server.service.recipe.SavedRecipeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/v0/saved")
@RequiredArgsConstructor
@RestController
public class SavedRecipeController {

    private final SavedRecipeService savedRecipeService;

    private final SavedRecipeQueryService savedRecipeQueryService;

    @PostMapping
    public @ResponseBody ResponseEntity<ApiResponse<Long>> saveRecipe(@RequestBody SavedRecipeCreateDto dto) {
        Long savedRecipeId = savedRecipeService.saveRecipe(dto.getRecipeId(), dto.getSchedules());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(savedRecipeId));
    }

    @GetMapping
    public @ResponseBody ResponseEntity<ApiResponse<List<SavedRecipeDto>>> getAllSavedRecipesByUser(
            @RequestParam Long id,
            @PageableDefault(page = 0, size = 10) Pageable pageable) {
        List<SavedRecipeDto> savedRecipesByUser = savedRecipeQueryService.findSavedRecipesByUser(id, pageable);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(savedRecipesByUser));
    }

}
