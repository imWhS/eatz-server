package imwhs.eatz_server.controller.api;

import imwhs.eatz_server.auth.EatzUserAuthUtil;
import imwhs.eatz_server.dto.ApiResponse;
import imwhs.eatz_server.dto.Paged;
import imwhs.eatz_server.dto.comment.CommentWithRecipeDto;
import imwhs.eatz_server.dto.eatzuser.*;
import imwhs.eatz_server.dto.ingredient.IngredientDto;
import imwhs.eatz_server.dto.plan.*;
import imwhs.eatz_server.dto.rating.RatingWithRecipeDto;
import imwhs.eatz_server.dto.recipe.*;
import imwhs.eatz_server.dto.recipe.ingredient.AddIngredientDto;
import imwhs.eatz_server.dto.recipe.ingredient.RemoveIngredientDto;
import imwhs.eatz_server.dto.recipe.savedrecipe.CreateSavedRecipeDto;
import imwhs.eatz_server.service.*;
import imwhs.eatz_server.service.ingredient.IngredientUserService;
import imwhs.eatz_server.service.query.EatzUserQueryService;
import imwhs.eatz_server.service.query.RecipeQueryService;
import imwhs.eatz_server.service.recipe.PlanService;
import imwhs.eatz_server.service.recipe.RatingService;
import imwhs.eatz_server.service.recipe.SavedRecipeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v0/users")
@RequiredArgsConstructor
public class EatzUserController {

    private final EatzUserService userService;

    private final EatzUserQueryService userQueryService;

    private final RecipeQueryService recipeQueryService;

    private final SavedRecipeService savedRecipeService;

    private final PlanService planService;

    private final IngredientUserService ingredientUserService;

    private final CommentService commentService;

    private final RatingService ratingService;

    // TODO: PATCH, 요청 파라미터를 통해 비밀 번호 등에 대한 부분 업데이트 API 추가
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<?> updateUser(
            @PathVariable Long id,
            @RequestBody @Valid UpdateEatzUserDto dto) {
        userService.updateUser(EatzUserAuthUtil.getUsername(), id, dto);
        return ResponseEntity.ok().body(ApiResponse.success("사용자의 주요 정보를 업데이트했어요."));
    }

    @PutMapping("/image")
    public ResponseEntity<ApiResponse<String>> updateImage(@RequestParam("file") MultipartFile file) {
        userService.updateImage(EatzUserAuthUtil.getUsername(), file);
        return ResponseEntity.ok(ApiResponse.success("사용자 대표 이미지를 업데이트했어요."));
    }

    @DeleteMapping("/image")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeImage() {
        userService.removeImage();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(
            @PathVariable Long id,
            @RequestBody @Valid DeleteEatzUserDto dto) {
        userService.deleteUser(id, dto);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Paged<EatzUserDto>>> getAllUsers(
            @PageableDefault(page = 0, size = 10) Pageable pageable
    ) {
        Page<EatzUserDto> allUsers = userQueryService.findAllUsers(pageable);
        return ResponseEntity.ok(ApiResponse.success(allUsers));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<EatzUserDto>> getUserById(
            @PathVariable Long id
    ) {
        EatzUserDto user = userQueryService.findUserById(id);
        return ResponseEntity.ok(ApiResponse.success(user));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<EatzUserDto>> getUserByEmail(
            @RequestParam String email
    ) {
        EatzUserDto user = userQueryService.findUserByEmail(email);
        return ResponseEntity.ok(ApiResponse.success(user));
    }

    @GetMapping("/{id}/recipes")
    public ResponseEntity<ApiResponse<Paged<RecipeByUserDto>>> getRecipesByUser(
            @PathVariable Long id,
            @PageableDefault(page = 0, size = 10) Pageable pageable) {
        Page<RecipeByUserDto> recipes = recipeQueryService.findByUserId(id, pageable);
        return ResponseEntity.ok(ApiResponse.success(recipes));
    }

    @PostMapping("/saveds")
    public ResponseEntity<ApiResponse<Long>> save(@RequestBody CreateSavedRecipeDto dto) {
        Long savedRecipeId = savedRecipeService.saveRecipe(dto.getRecipeId(), EatzUserAuthUtil.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(savedRecipeId));
    }

    @DeleteMapping("/saveds/recipes/{id}")
    public ResponseEntity<ApiResponse<Long>> unsaveRecipeById(@PathVariable Long id) {
        savedRecipeService.unsaveRecipeById(id, EatzUserAuthUtil.getId());
        return ResponseEntity.ok(ApiResponse.success(id));
    }

    @GetMapping("/saveds")
    public ResponseEntity<ApiResponse<List<RecipeBasicDto>>> getSavedRecipes() {
        List<RecipeBasicDto> savedRecipes = savedRecipeService.getSavedRecipesByUser(EatzUserAuthUtil.getUsername());
        return ResponseEntity.ok(ApiResponse.success(savedRecipes));
    }

    @PostMapping("/plans")
    public ResponseEntity<ApiResponse<Long>> registerPlan(@RequestBody CreatePlanDto dto) {
        Long planId = planService.registerPlan(dto.getRecipeId(), EatzUserAuthUtil.getId(), dto.getDate(), dto.getPriority());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(planId));
    }

    @PutMapping("/plans/{id}")
    public ResponseEntity<ApiResponse<?>> updatePlan(@PathVariable Long id, @RequestBody UpdatePlanDto dto) {
        planService.updatePlan(id, EatzUserAuthUtil.getUsername(), dto.getDate(), dto.getPriority());
        return ResponseEntity.ok(ApiResponse.success("플랜을 성공적으로 업데이트했어요."));
    }

    @DeleteMapping("/plans/{id}")
    public ResponseEntity<ApiResponse<?>> deletePlan(@PathVariable Long id) {
        planService.deletePlan(id, EatzUserAuthUtil.getUsername());
        return ResponseEntity.ok(ApiResponse.success("플랜을 성공적으로 삭제했어요."));
    }

    @GetMapping("/plans")
    public ResponseEntity<ApiResponse<List<PlanDto>>> getPlans(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        if (startDate == null) {
            startDate = LocalDate.of(1900, 1, 1);
        }
        if (endDate == null) {
            endDate = LocalDate.of(2100, 12, 31);
        }

        List<PlanDto> plans = planService.findByUserAndDateRange(EatzUserAuthUtil.getId(), startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success(plans));
    }

    @GetMapping("/plans/checklist")
    public ResponseEntity<ApiResponse<ChecklistDto>> getChecklist(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        ChecklistDto checklist = planService.getChecklist(EatzUserAuthUtil.getId(), startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success(checklist));
    }

    @GetMapping("/plans/checklistn")
    public ResponseEntity<ApiResponse<NChecklistDto>> getChecklistn(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate
    ) {
        NChecklistDto checklist = planService.getChecklistn(EatzUserAuthUtil.getId(), startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success(checklist));
    }

    @PostMapping("/plans/checklist/complete")
    public ResponseEntity<ApiResponse<?>> completeChecklist(
            @RequestBody CompleteChecklistDto dto
    ) {
        ingredientUserService.addIngredientsToUser(EatzUserAuthUtil.getId(), dto.getIngredientIds());
        return ResponseEntity.ok(ApiResponse.success("체크리스트의 필요한 재료를 모두 추가했어요."));
    }

    @PostMapping("/ingredients")
    public ResponseEntity<ApiResponse<List<Long>>> addIngredients(@RequestBody AddIngredientDto dto) {
        List<Long> addedIngredients = ingredientUserService.addIngredientsToUser(EatzUserAuthUtil.getId(), dto.getIngredientIds());

        if (addedIngredients.size() != dto.getIngredientIds().size()) {
            return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT).body(ApiResponse.success(addedIngredients));
        } else if (addedIngredients.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error("모든 재료를 추가하지 못했어요. " +
                    "추가하려는 재료를 다시 확인해보세요."));
        }

        return ResponseEntity.ok(ApiResponse.success(addedIngredients));
    }

    @DeleteMapping("/ingredients")
    public ResponseEntity<ApiResponse<?>> removeIngredients(@RequestBody RemoveIngredientDto dto) {
        ingredientUserService.removeIngredientsFromUser(EatzUserAuthUtil.getUsername(), dto.getIngredientIds());
        return ResponseEntity.ok(ApiResponse.success("재료 제거를 완료했어요."));
    }

    @GetMapping("/ingredients")
    public ResponseEntity<ApiResponse<List<IngredientDto>>> getIngredients() {
        List<IngredientDto> ingredients = ingredientUserService.getIngredients(EatzUserAuthUtil.getUsername());
        return ResponseEntity.ok(ApiResponse.success(ingredients));
    }

    @GetMapping("/comments")
    public ResponseEntity<ApiResponse<Paged<CommentWithRecipeDto>>> getComments(
            @PageableDefault(page = 0, size = 10) Pageable pageable) {
        Page<CommentWithRecipeDto> comments = commentService.findByUser(EatzUserAuthUtil.getUsername(), pageable);
        return ResponseEntity.ok(ApiResponse.success(comments));
    }

    @GetMapping("/ratings")
    public ResponseEntity<ApiResponse<Paged<RatingWithRecipeDto>>> getRatings(
            @PageableDefault(page = 0, size = 10) Pageable pageable) {
        Page<RatingWithRecipeDto> ratings = ratingService.findByUser(EatzUserAuthUtil.getId(), pageable);
        return ResponseEntity.ok(ApiResponse.success(ratings));
    }

}
