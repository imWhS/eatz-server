package imwhs.eatz_server.controller;

import imwhs.eatz_server.auth.EatzUserAuthUtil;
import imwhs.eatz_server.domain.Category;
import imwhs.eatz_server.dto.ApiResponse;
import imwhs.eatz_server.dto.recipe.category.CreateCategoryDto;
import imwhs.eatz_server.dto.recipe.category.CategoryDto;
import imwhs.eatz_server.dto.recipe.category.UpdateCategoryDto;
import imwhs.eatz_server.service.recipe.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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
     * @param dto 등록하려는 카테고리 정보.
     * @return 생성된 카테고리 관련 정보.
     */
    @PostMapping
    public ResponseEntity<ApiResponse<CategoryDto>> registerCategory(@RequestBody CreateCategoryDto dto) {
        Long recipeId = dto.getRecipeId();
        Category category;

        if (recipeId == null) {
            category = categoryService.register(dto.getName(), dto.getDescription());
        } else {
            category = categoryService.register(dto.getName(), dto.getDescription(), recipeId);
        }

        return ResponseEntity.ok(ApiResponse.success(new CategoryDto(category)));
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateCategory(@PathVariable Long id, @RequestBody UpdateCategoryDto dto) {
        categoryService.update(id, EatzUserAuthUtil.getId(), dto.getName(), dto.getDescription());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable Long id) {
        categoryService.delete(id, EatzUserAuthUtil.getId());
    }

}
