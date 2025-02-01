package imwhs.eatz_server.controller.recipe;

import imwhs.eatz_server.domain.recipe.Category;
import imwhs.eatz_server.dto.ApiResponse;
import imwhs.eatz_server.dto.recipe.CategoryCreateDto;
import imwhs.eatz_server.dto.recipe.CategoryDetailDto;
import imwhs.eatz_server.service.recipe.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v0/categories")
@RestController
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<ApiResponse<CategoryDetailDto>> registerCategory(@RequestBody CategoryCreateDto dto) {
        Long recipeId = dto.getRecipeId();
        Category category;

        if (recipeId == null) {
            category = categoryService.registerCategory(dto.getName(), dto.getDescription());
        } else {
            category = categoryService.registerCategory(dto.getName(), dto.getDescription(), recipeId);
        }

        return ResponseEntity.ok(ApiResponse.success(new CategoryDetailDto(category)));
    }

}
