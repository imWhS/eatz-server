package imwhs.eatz_server.controller;

import imwhs.eatz_server.dto.ApiResponse;
import imwhs.eatz_server.dto.Paged;
import imwhs.eatz_server.dto.auth.SignUpRequestDto;
import imwhs.eatz_server.dto.eatzuser.*;
import imwhs.eatz_server.dto.recipe.RecipeDto;
import imwhs.eatz_server.dto.recipe.RecipeBasicDto;
import imwhs.eatz_server.service.AuthService;
import imwhs.eatz_server.service.EatzUserService;
import imwhs.eatz_server.service.query.EatzUserQueryService;
import imwhs.eatz_server.service.query.NSavedRecipeService;
import imwhs.eatz_server.service.query.RecipeQueryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

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

    @GetMapping("/activity")
    public ResponseEntity<ApiResponse<Paged<EatzUserSummaryDto>>> getAllUsersWithActivity(
            @PageableDefault(page = 0, size = 10) Pageable pageable
    ) {
        Page<EatzUserSummaryDto> allUsers = userQueryService.findAllUsersWithActivity(pageable);
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

    @GetMapping("/{id}/saveds")
    public ResponseEntity<ApiResponse<List<RecipeBasicDto>>> getSavedRecipes(@PathVariable Long id) {
        List<RecipeBasicDto> savedRecipes = savedRecipeService.getSavedRecipesByUser(id);
        return ResponseEntity.ok(ApiResponse.success(savedRecipes));
    }

}
