package imwhs.eatz_server.controller;

import imwhs.eatz_server.domain.recipe.Category;
import imwhs.eatz_server.dto.ApiResponse;
import imwhs.eatz_server.dto.recipe.CategoryCreateDto;
import imwhs.eatz_server.dto.recipe.CategoryDetailDto;
import imwhs.eatz_server.service.recipe.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 카테고리를 관리하기 위한 API를 제공하는 컨트롤러입니다.
 */
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v0/categories")
@RestController
public class CategoryController {

    private final CategoryService categoryService;

    /**
     * 새 카테고리를 등록합니다.
     * @param dto
     * @return
     */
    @PostMapping
    public ResponseEntity<ApiResponse<CategoryDetailDto>> registerCategory(@RequestBody CategoryCreateDto dto) {
        Long recipeId = dto.getRecipeId();
        Category category;

        if (recipeId == null) {
            category = categoryService.register(dto.getName(), dto.getDescription());
        } else {
            category = categoryService.register(dto.getName(), dto.getDescription(), recipeId);
        }

        return ResponseEntity.ok(ApiResponse.success(new CategoryDetailDto(category)));
    }

}
