package imwhs.eatz_server.controller;

import imwhs.eatz_server.auth.EatzUserAuthUtil;
import imwhs.eatz_server.dto.ApiResponse;
import imwhs.eatz_server.dto.Paged;
import imwhs.eatz_server.dto.auth.SignUpRequestDto;
import imwhs.eatz_server.dto.eatzuser.*;
import imwhs.eatz_server.dto.ingredient.IngredientDto;
import imwhs.eatz_server.dto.plan.ChecklistItemResponseDto;
import imwhs.eatz_server.dto.plan.PlanCreateDto;
import imwhs.eatz_server.dto.plan.PlanUpdateDto;
import imwhs.eatz_server.dto.recipe.IngredientAddDto;
import imwhs.eatz_server.dto.recipe.PlanDto;
import imwhs.eatz_server.dto.recipe.RecipeDto;
import imwhs.eatz_server.dto.recipe.RecipeBasicDto;
import imwhs.eatz_server.dto.recipe.nsavedrecipe.NSavedRecipeCreateDto;
import imwhs.eatz_server.service.AuthService;
import imwhs.eatz_server.service.EatzUserService;
import imwhs.eatz_server.service.PlanService;
import imwhs.eatz_server.service.ingredient.IngredientUserService;
import imwhs.eatz_server.service.query.EatzUserQueryService;
import imwhs.eatz_server.service.NSavedRecipeService;
import imwhs.eatz_server.service.query.RecipeQueryService;
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
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v0/users")
@RequiredArgsConstructor
public class EatzUserController {

    private final EatzUserService userService;

    private final EatzUserQueryService userQueryService;
    
    private final AuthService authService;

    private final RecipeQueryService recipeQueryService;

    private final NSavedRecipeService savedRecipeService;

    private final PlanService planService;

    private final IngredientUserService ingredientUserService;

    @PostMapping
    public ResponseEntity<ApiResponse<Long>> registerUser(@RequestBody @Valid SignUpRequestDto dto) {
        Long userId = authService.signUp(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(userId));
    }

    // TODO: PATCH, 요청 파라미터를 통해 비밀 번호 등에 대한 부분 업데이트 API 추가
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<?> updateUser(
            @PathVariable Long id,
            @RequestBody @Valid EatzUserUpdateDto dto) {
        userService.updateUser(id, dto);
        return ResponseEntity.ok().body(ApiResponse.success("사용자의 주요 정보를 업데이트했어요."));
    }

    @PostMapping("/image")
    public ResponseEntity<ApiResponse<String>> updateImage(@RequestParam("file") MultipartFile file) {
        userService.updateImage(file);
        return ResponseEntity.ok(ApiResponse.success("사용자 대표 이미지를 업데이트했어요."));
    }

    // TODO: 재료 추가 POST /users/ingredients/

    @DeleteMapping("/image")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteImage() {
        userService.deleteImage();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(
            @PathVariable Long id,
            @RequestBody @Valid EatzUserDeleteDto dto) {
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
    public ResponseEntity<ApiResponse<Paged<RecipeDto>>> getRecipeByUser(
            @PathVariable Long id,
            @PageableDefault(page = 0, size = 10) Pageable pageable) {
        Page<RecipeDto> recipes = recipeQueryService.findAllRecipesByUserId(id, pageable);
        return ResponseEntity.ok(ApiResponse.success(recipes));
    }

    @PostMapping("/saveds")
    public ResponseEntity<ApiResponse<Long>> save(NSavedRecipeCreateDto dto) {
        Long savedRecipeId = savedRecipeService.saveRecipe(dto.getRecipeId(), EatzUserAuthUtil.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(savedRecipeId));
    }

    @DeleteMapping("/saveds/{id}")
    public ResponseEntity<ApiResponse<Long>> unsaveRecipeById(@PathVariable Long id) {
        savedRecipeService.unsaveRecipeById(id, EatzUserAuthUtil.getUsername());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(ApiResponse.success(id));
    }

    @GetMapping("/saveds")
    public ResponseEntity<ApiResponse<List<RecipeBasicDto>>> getSavedRecipes() {
        List<RecipeBasicDto> savedRecipes = savedRecipeService.getSavedRecipesByUser(EatzUserAuthUtil.getUsername());
        return ResponseEntity.ok(ApiResponse.success(savedRecipes));
    }

    @PostMapping("/plans")
    public ResponseEntity<ApiResponse<Long>> registerPlan(@RequestBody PlanCreateDto dto) {
        Long planId = planService.registerPlan(dto.getRecipeId(), EatzUserAuthUtil.getUsername(), dto.getDate(), dto.getPriority());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(planId));
    }

    @PutMapping("/plans/{id}")
    public ResponseEntity<ApiResponse<?>> updatePlan(@PathVariable Long id, @RequestBody PlanUpdateDto dto) {
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
            startDate = LocalDate.of(1900, 1, 1); // 과거의 기준 날짜
        }
        if (endDate == null) {
            endDate = LocalDate.of(2100, 12, 31); // 미래의 기준 날짜
        }

        List<PlanDto> plans = planService.findAllByUserAndDateRange(EatzUserAuthUtil.getUsername(), startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success(plans));
    }

    @GetMapping("/plans/checklist")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getChecklist(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        Map<String, Object> checklist = planService.getChecklist(EatzUserAuthUtil.getUsername(), startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success(checklist));
    }

    // TODO: 재료 등록, 삭제
    /*
    재료 등록: POST users/ingredients
    재료 삭제: DELETE users/ingredients
     */
    @PostMapping("/ingredients")
    public ResponseEntity<ApiResponse<List<Long>>> addIngredients(@RequestBody IngredientAddDto dto) {
        List<Long> addedIngredients = ingredientUserService.addIngredientsToUser(EatzUserAuthUtil.getUsername(), dto.getIngredientIds());

        if (addedIngredients.size() != dto.getIngredientIds().size()) {
            return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT).body(ApiResponse.success(addedIngredients));
        } else if (addedIngredients.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error("모든 재료를 사용자에 추가하지 못했어요. " +
                    "추가하려는 재료를 다시 확인해보세요."));
        }

        return ResponseEntity.ok().body(ApiResponse.success(addedIngredients));
    }

    @GetMapping("/ingredients")
    public ResponseEntity<ApiResponse<List<IngredientDto>>> getIngredients() {
        List<IngredientDto> ingredients = ingredientUserService.getIngredients(EatzUserAuthUtil.getUsername());
        return ResponseEntity.ok(ApiResponse.success(ingredients));
    }



    /*
    TODO: 특정 사용자, 날짜 별 플랜 목록 조회 (우선 순위 순)
    - Plan에 날짜, 사용자 ID, 레시피 ID 정보가 포함됨.
    - PlanRepository: 사용자, 날짜로 Plan 필터링 후 레시피 목록 조회


    TODO: 특정 사용자, 레시피 별 플랜 조회
    TODO: 특정 사용자, 날짜 범위에 속하는 플랜의 레시피들의 재료 조회 (+ 사용자가 추가한 재료와 비교)
     */



}
