package imwhs.eatz_server.controller;

import imwhs.eatz_server.auth.EatzUserAuthUtil;
import imwhs.eatz_server.domain.recipe.SavedRecipe;
import imwhs.eatz_server.dto.ApiResponse;
import imwhs.eatz_server.dto.recipe.nsavedrecipe.NSavedRecipeCreateDto;
import imwhs.eatz_server.dto.recipe.savedrecipe.SavedRecipeCreateDto;
import imwhs.eatz_server.service.query.NSavedRecipeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v0/saveds/recipes")
public class SavedRecipeController {

    private final NSavedRecipeService savedRecipeService;

    @PostMapping
    public @ResponseBody ResponseEntity<?> save(@RequestBody NSavedRecipeCreateDto dto) {
        Long savedId = savedRecipeService.save(dto.getRecipeId(), EatzUserAuthUtil.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(savedId));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void unsave(@PathVariable Long id) {
        savedRecipeService.unsave(id, EatzUserAuthUtil.getUsername());
    }

}
