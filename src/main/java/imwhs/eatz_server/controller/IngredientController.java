package imwhs.eatz_server.controller;

import imwhs.eatz_server.dto.ApiResponse;
import imwhs.eatz_server.dto.ingredient.IngredientCreateDto;
import imwhs.eatz_server.dto.ingredient.IngredientWithCategoryChildDto;
import imwhs.eatz_server.dto.ingredient.IngredientTreeDto;
import imwhs.eatz_server.service.ingredient.IngredientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/v0/ingredients")
@RequiredArgsConstructor
@RestController
public class IngredientController {

    private final IngredientService ingredientService;

    @PostMapping
    public @ResponseBody ResponseEntity<ApiResponse<Long>> registerIngredient(@RequestBody IngredientCreateDto dto) {
        Long ingredientId = ingredientService.registerIngredient(dto.getName(), dto.getCategoryId(), dto.getChildIds());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(ingredientId));
    }

    @GetMapping("/{id}")
    public @ResponseBody ResponseEntity<ApiResponse<IngredientWithCategoryChildDto>> findIngredient(@PathVariable Long id) {
        IngredientWithCategoryChildDto dto = ingredientService.findIngredient(id);
        return ResponseEntity.ok(ApiResponse.success(dto));
    }

    @GetMapping("/{id}/tree")
    public @ResponseBody ResponseEntity<ApiResponse<IngredientTreeDto>> findIngredientTree(@PathVariable Long id) {
        IngredientTreeDto dto = ingredientService.findIngredientTree(id);
        return ResponseEntity.ok(ApiResponse.success(dto));
    }

}
