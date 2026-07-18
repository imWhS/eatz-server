package imwhs.eatz_server.controller.api.eatzuser;

import imwhs.eatz_server.dto.CountResponse;
import imwhs.eatz_server.dto.eatzuser.pantry.AddAllRecipeRequirementsToPantryRequest;
import imwhs.eatz_server.dto.ingredient.IngredientBasicDto;
import imwhs.eatz_server.dto.kitchenware.KitchenwareBasicDto;
import imwhs.eatz_server.dto.eatzuser.pantry.AddIngredientsToPantryRequest;
import imwhs.eatz_server.dto.eatzuser.pantry.PantryAdditionInfoResponse;
import imwhs.eatz_server.dto.eatzuser.pantry.AddKitchenwaresToPantryRequest;
import imwhs.eatz_server.dto.plan.ChecklistRequirementsDto;
import imwhs.eatz_server.resolver.AuthenticatedEatzUserId;
import imwhs.eatz_server.service.PantryService;
import imwhs.eatz_server.service.ingredient.IngredientQueryService;
import imwhs.eatz_server.service.kitchenware.KitchenwareQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/api/v0/users/me/pantry")
@RestController
public class EatzUserPantryController {

    private final PantryService pantryService;
    private final IngredientQueryService ingredientQueryService;
    private final KitchenwareQueryService kitchenwareQueryService;

    @PostMapping("/add-all-recipe-requirements")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void addAllRecipeRequirementsToPantry(
            @RequestBody AddAllRecipeRequirementsToPantryRequest request,
            @AuthenticatedEatzUserId Long userId) {
        pantryService.addAllRecipeRequirementsToPantry(request.getRecipeId(), userId);
    }

    @PostMapping("/add-all-checklist-requirements")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void addAllChecklistRequirementsToPantry(
            @RequestBody ChecklistRequirementsDto dto,
            @AuthenticatedEatzUserId Long userId) {
        pantryService.addAllChecklistRequirementsToPantry(dto, userId);
    }

    @PostMapping("/ingredients")
    public ResponseEntity<PantryAdditionInfoResponse> addIngredientsToPantry(
            @RequestBody AddIngredientsToPantryRequest request,
            @AuthenticatedEatzUserId Long userId) {
        List<Long> ingredientIds = request.getIngredientIds();
        List<Long> addedIngredients = pantryService.addIngredients(ingredientIds, userId);
        return ResponseEntity.ok(new PantryAdditionInfoResponse(addedIngredients));
    }

    /**
     * 사용자의 보관함에서 재료를 제거합니다.
     * <p>
     *     HTTP spec에 맞게, DELETE 요청 시 삭제할 재료의 ID 목록을 query parameter로 전달 받습니다.
     * </p>
     * @param ingredientIds 제거하려는 재료의 ID 목록
     */
    @DeleteMapping("/ingredients")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeIngredientsFromPantry(
            @RequestParam("ingredientIds") List<Long> ingredientIds,
            @AuthenticatedEatzUserId Long userId) {
        pantryService.removeIngredients(ingredientIds, userId);
    }

    @DeleteMapping("/ingredients/all")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeAllIngredientsFromPantry(@AuthenticatedEatzUserId Long userId) {
        pantryService.clearIngredients(userId);
    }

    @GetMapping("/ingredients")
    @ResponseStatus(HttpStatus.OK)
    public Page<IngredientBasicDto> getIngredientsFromPantry(
            @AuthenticatedEatzUserId Long userId,
            @PageableDefault(page = 0, size = 10) Pageable pageable) {
        return ingredientQueryService.getAllIngredientBasicsByUserId(userId, pageable);
    }

    @GetMapping("/ingredients/count")
    @ResponseStatus(HttpStatus.OK)
    public CountResponse getIngredientCountFromPantry(@AuthenticatedEatzUserId Long userId) {
        return ingredientQueryService.getAllIngredientCountByUserId(userId);
    }

    @PostMapping("/kitchenwares")
    public ResponseEntity<PantryAdditionInfoResponse> addKitchenwaresToPantry(
            @RequestBody AddKitchenwaresToPantryRequest request,
            @AuthenticatedEatzUserId Long userId) {
        List<Long> kitchenwareIds = request.getKitchenwareIds();
        List<Long> addedKitchenwares = pantryService.addKitchenwares(kitchenwareIds, userId);
        return ResponseEntity.ok(new PantryAdditionInfoResponse(addedKitchenwares));
    }

    /**
     * 사용자의 보관함에서 도구를 제거합니다.
     * <p>
     *     HTTP spec에 맞게, DELETE 요청 시 삭제할 재료의 ID 목록을 Query parameter로 전달 받습니다.
     * </p>
     * @param kitchenwareIds 제거하려는 재료의 ID 목록
     */
    @DeleteMapping("/kitchenwares")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeKitchenwaresFromPantry(
            @RequestParam("kitchenwareIds") List<Long> kitchenwareIds,
            @AuthenticatedEatzUserId Long userId) {
        pantryService.removeKitchenwares(kitchenwareIds, userId);
    }

    @DeleteMapping("/kitchenwares/all")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeAllKitchenwaresFromPantry(@AuthenticatedEatzUserId Long userId) {
        pantryService.clearKitchenwares(userId);
    }

    @GetMapping("/kitchenwares")
    @ResponseStatus(HttpStatus.OK)
    public Page<KitchenwareBasicDto> getKitchenwaresFromPantry(
            @AuthenticatedEatzUserId Long userId,
            @PageableDefault(page = 0, size = 10) Pageable pageable) {
        return kitchenwareQueryService.getAllKitchenwares(userId, pageable);
    }

    @GetMapping("/kitchenwares/count")
    @ResponseStatus(HttpStatus.OK)
    public CountResponse getKitchenwareCountFromPantry(@AuthenticatedEatzUserId Long userId) {
        return kitchenwareQueryService.getAllKitchenwareCount(userId);
    }

}
