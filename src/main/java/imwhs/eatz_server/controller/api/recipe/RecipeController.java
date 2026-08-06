package imwhs.eatz_server.controller.api.recipe;

import imwhs.eatz_server.dto.RecipeOutboundResponse;
import imwhs.eatz_server.dto.UploadedImageInfoResponse;
import imwhs.eatz_server.dto.liked.LikedRecipeBasicDto;
import imwhs.eatz_server.dto.recipe.cookable.CookableRecipeDto;
import imwhs.eatz_server.dto.recipe.cookable.CookableRecipesRequest;
import imwhs.eatz_server.dto.recipe.explore.ExploreRecipeDto;
import imwhs.eatz_server.dto.recipe.explore.ExploreRecipesRequest;
import imwhs.eatz_server.dto.recipe.kitchenware.KitchenwareRequirementDto;
import imwhs.eatz_server.dto.recipe.*;
import imwhs.eatz_server.dto.recipe.ingredient.IngredientRequirementDto;
import imwhs.eatz_server.resolver.AuthenticatedEatzUserId;
import imwhs.eatz_server.service.ingredient.IngredientQueryService;
import imwhs.eatz_server.service.kitchenware.KitchenwareQueryService;
import imwhs.eatz_server.service.liked.LikedRecipeService;
import imwhs.eatz_server.service.recipe.RecipeOutboundCountService;
import imwhs.eatz_server.service.recipe.RecipeService;
import imwhs.eatz_server.service.recipe.RecipeQueryService;
import imwhs.eatz_server.service.recipe.RecipeDetailViewCountService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 레시피를 관리하기 위한 API를 제공하는 컨트롤러입니다.
 */
@RequestMapping("/api/v0/recipes")
@RequiredArgsConstructor
@RestController
public class RecipeController {

    private final RecipeService recipeService;
    private final RecipeQueryService recipeQueryService;
    private final IngredientQueryService ingredientQueryService;
    private final KitchenwareQueryService kitchenwareQueryService;
    private final RecipeDetailViewCountService recipeDetailViewCountService;
    private final LikedRecipeService likedRecipeService;
    private final RecipeOutboundCountService recipeOutboundCountService;

    /**
     * 새 레시피를 등록합니다.
     * @param dto 레시피 생성 및 등록 요청 정보
     * @return 등록 완료된 레시피의 생성 정보
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RecipeCreationInfoDto registerRecipe(
            @RequestBody RecipeCreateDto dto,
            @AuthenticatedEatzUserId Long userId) {
        return recipeService.register(dto, userId);
    }

    /**
     * 새 '레시피 보기' 이벤트를 생성합니다.
     * '레시피 보기' 이벤트 생성 수를 증기시키고, 레시피의 URL을 응답합니다.
     * @param id
     * @param userId
     */
    @PostMapping("/urls/{id}")
    public RecipeOutboundResponse getRecipeOutbound(
            @PathVariable Long id,
            @AuthenticatedEatzUserId(required = false) Long userId,
            @RequestHeader(value = "X-Device-ID", required = false) String deviceId,
            @RequestHeader(value = "Time-Zone", defaultValue = "Asia/Seoul") String timeZone) {
        if (userId == null) { recipeOutboundCountService.markAsOutboundTodayByGuest(id, deviceId, timeZone); }
        else { recipeOutboundCountService.markAsOutboundTodayByAuthenticated(id, userId, timeZone); }

        return recipeService.getUrl(id);
    }

    @PostMapping("/images")
    public UploadedImageInfoResponse uploadRecipeImage(
            @RequestParam("image") MultipartFile image,
            @AuthenticatedEatzUserId Long userId) {
        String imageUrl = recipeService.uploadNewImage(image, userId);
        return new UploadedImageInfoResponse(imageUrl);
    }

    @DeleteMapping("/images")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteRecipeImage(
            @RequestParam String imageUrl,
            @AuthenticatedEatzUserId Long userId) {
        recipeService.deleteExistingImage(imageUrl, userId);
    }

    /**
     * 레시피를 업데이트합니다.
     * @param id 업데이트하려는 레시피의 ID
     * @param dto 레시피 업데이트 요청 정보
     */
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateRecipe(
            @PathVariable Long id,
            @AuthenticatedEatzUserId Long userId,
            @RequestBody RecipeUpdateDto dto) {
        recipeService.update(id, dto, userId);
    }

    /**
     * 레시피를 삭제 처리합니다.
     * <p>레시피를 등록한 사용자만 접근 가능합니다.</p>
     * @param id 레시피의 ID
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void markRecipeAsDeleted(@PathVariable Long id, @AuthenticatedEatzUserId Long userId) {
        recipeService.markAsDeleted(id, userId);
    }

    /**
     * 사용자가 레시피에 좋아요를 표시합니다.
     * @param id 레시피의 ID
     * @return 좋아요 표시한 레시피의 정보
     */
    @PostMapping("/{id}/likeds")
    public LikedRecipeBasicDto likeRecipe(@PathVariable Long id, @AuthenticatedEatzUserId Long userId) {
        return likedRecipeService.like(id, userId);
    }

    /**
     * 사용자가 레시피의 좋아요 표시를 취소합니다.
     * @param id 레시피의 ID
     * @return 좋아요 표시 취소한 레시피의 정보
     */
    @DeleteMapping("/{id}/likeds")
    public LikedRecipeBasicDto unlikeRecipe(@PathVariable Long id, @AuthenticatedEatzUserId Long userId) {
        return likedRecipeService.unlike(id, userId);
    }

    /**
     * 레시피의 상세 정보를 가져오고, 해당 레시피의 조회 수를 증가시킵니다.
     * @param id 레시피의 ID
     * @return 레시피의 상세 정보
     */
    @GetMapping("/{id}")
    public RecipeDetailDto getRecipeDetailAndIncreaseViewCount(
            @PathVariable Long id,
            @AuthenticatedEatzUserId(required = false) Long userId,
            @RequestHeader(value = "X-Device-ID", required = false) String deviceId,
            @RequestHeader(value = "Time-Zone", defaultValue = "Asia/Seoul") String timeZone) {
        RecipeDetailDto dto = recipeQueryService.getDetail(id, userId);
        if (userId == null) { recipeDetailViewCountService.markAsViewedTodayByGuest(id, deviceId, timeZone); }
        else { recipeDetailViewCountService.markAsViewedTodayByAuthenticated(id, userId, timeZone); }

        return dto;
    }

    @GetMapping("/{id}/editable")
    public RecipeEditableDto getRecipeEditable(@PathVariable Long id, @AuthenticatedEatzUserId Long userId) {
        return recipeQueryService.getEditable(id, userId);
    }

    @GetMapping
    public Page<RecipeBasicDto> getAllRecipeBasicsByAuthorId(
            @RequestParam Long authorId,
            @PageableDefault(page = 0, size = 10) Pageable pageable) {
        return recipeQueryService.getAllBasicsByAuthorId(authorId, pageable);
    }

    @GetMapping("/{id}/essential")
    public RecipeEssentialWithAuthorDto getRecipeEssential(@PathVariable Long id) {
        return recipeQueryService.getEssentialWithAuthor(id);
    }

    @GetMapping("/{id}/ingredients")
    public List<IngredientRequirementDto> getIngredientRequirements(
            @PathVariable Long id,
            @AuthenticatedEatzUserId(required = false) Long userId) {
        return ingredientQueryService.getIngredientRequirementsByRecipeId(id, userId);
    }

    @GetMapping("/{id}/kitchenwares")
    public List<KitchenwareRequirementDto> getKitchenwareRequirements(
            @PathVariable Long id, @AuthenticatedEatzUserId(required = false) Long userId) {
        List<KitchenwareRequirementDto> kitchenwares = kitchenwareQueryService.getKitchenwareRequirementsByRecipeId(
                id, userId);
        return kitchenwares;
    }

    @GetMapping("/search")
    public Page<RecipeBasicDto> searchRecipes(
            @RequestParam(name = "keyword", required = false) String keyword,
            @AuthenticatedEatzUserId(required = false) Long userId,
            @PageableDefault(page = 0, size = 10) Pageable pageable) {
        return recipeQueryService.getAllBasics(keyword, userId, pageable);
    }

    @GetMapping("/explore")
    public Page<ExploreRecipeDto> getExploreRecipes(
            ExploreRecipesRequest request,
            @AuthenticatedEatzUserId(required = false) Long userId,
            @PageableDefault(page = 0, size = 10) Pageable pageable) {
        return recipeQueryService.getExploreRecipes(request, userId, pageable);
    }

    @GetMapping("/cookable")
    public Page<CookableRecipeDto> getCookableRecipes(
            CookableRecipesRequest request,
            @AuthenticatedEatzUserId(required = false) Long userId,
            @PageableDefault(page = 0, size = 10) Pageable pageable) {
        return recipeQueryService.getCookableRecipes(request, userId, pageable);
    }

}
