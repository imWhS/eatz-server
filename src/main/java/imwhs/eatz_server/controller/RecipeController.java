package imwhs.eatz_server.controller;

import imwhs.eatz_server.domain.Recipe;
import imwhs.eatz_server.dto.ApiResponse;
import imwhs.eatz_server.dto.recipe.RecipeCreateDto;
import imwhs.eatz_server.service.RecipeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@RequestMapping("/api/v0/recipes")
@RequiredArgsConstructor
@Controller
public class RecipeController {

    private final RecipeService recipeService;

    @PostMapping
    public @ResponseBody ResponseEntity<ApiResponse<Long>> registerRecipe(@RequestBody RecipeCreateDto dto) {
        System.out.println("RecipeController.registerRecipe");
        Long recipeId = recipeService.registerRecipe(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(recipeId));
    }

}
