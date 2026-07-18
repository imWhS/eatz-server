package imwhs.eatz_server.controller.api.eatzuser;

import imwhs.eatz_server.dto.CountResponse;
import imwhs.eatz_server.dto.UploadedImageInfoResponse;
import imwhs.eatz_server.dto.ingredient.IngredientBasicDto;
import imwhs.eatz_server.dto.comment.CommentEssentialWithRecipeDto;
import imwhs.eatz_server.dto.eatzuser.*;
import imwhs.eatz_server.dto.rating.RatingEssentialWithRecipeDto;
import imwhs.eatz_server.dto.recipe.*;
import imwhs.eatz_server.dto.recipe.SavedRecipeCreateRequest;
import imwhs.eatz_server.resolver.AuthenticatedEatzUserId;
import imwhs.eatz_server.service.ingredient.IngredientQueryService;
import imwhs.eatz_server.service.rating.RatingQueryService;
import imwhs.eatz_server.service.comment.CommentQueryService;
import imwhs.eatz_server.service.eatzuser.EatzUserQueryService;
import imwhs.eatz_server.service.eatzuser.EatzUserService;
import imwhs.eatz_server.service.liked.LikedRecipeService;
import imwhs.eatz_server.service.recipe.RecipeQueryService;
import imwhs.eatz_server.service.recipe.SavedRecipeQueryService;
import imwhs.eatz_server.service.recipe.SavedRecipeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v0/users")
@RestController
public class EatzUserController {

    private final EatzUserService userService;
    private final EatzUserQueryService userQueryService;
    private final RecipeQueryService recipeQueryService;
    private final SavedRecipeService savedRecipeService;
    private final SavedRecipeQueryService savedRecipeQueryService;
    private final CommentQueryService commentQueryService;
    private final LikedRecipeService likedRecipeService;
    private final RatingQueryService ratingQueryService;
    private final IngredientQueryService ingredientQueryService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Page<EatzUserDetailDto> getAllUsers(@PageableDefault(page = 0, size = 10) Pageable pageable) {
        return userQueryService.getAllDetails(pageable);
    }

    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public EatzUserDetailDto getUserByEmail(@RequestParam String email) {
        return userQueryService.getDetailByEmail(email);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public EatzUserDetailDto getUserById(@PathVariable Long id) {
        return userQueryService.getDetail(id);
    }

    /**
     * 사용자의 암호를 업데이트합니다.
     * @param id 사용자의 ID
     * @param request 사용자 암호 업데이트를 요청하기 위해 필요한 정보
     */
    @PutMapping("/{id}/password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateUserPassword(
            @PathVariable Long id,
            @AuthenticatedEatzUserId Long userId,
            @RequestBody @Valid UpdateEatzUserPasswordRequest request) {
        userService.updatePassword(id, userId, request.getExistingPassword(), request.getNewPassword());
    }

    @PutMapping("/{id}/username")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateUserUsername(
            @PathVariable Long id,
            @AuthenticatedEatzUserId Long userId,
            @RequestBody @Valid UpdateEatzUserUsernameRequest request) {
        userService.updateUsername(id, userId, request.getUsername());
    }

    /**
     * 사용자를 삭제 처리합니다.
     * @param id 사용자의 ID
     * @param request 사용자 삭제 처리를 요청하기 위해 필요한 정보
     */
    @DeleteMapping("/{id}/deactive")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void markUserAsDeleted(
            @PathVariable Long id,
            @AuthenticatedEatzUserId Long userId,
            @RequestBody @Valid EatzUserMarkAsDeletedRequest request) {
        userService.markAsDeleted(id, userId, request.getExistingPassword());
    }

    /**
     * 사용자를 삭제합니다.
     * @param id 사용자의 ID
     * @param request 사용자 삭제를 요청하기 위해 필요한 정보
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(
            @PathVariable Long id,
            @AuthenticatedEatzUserId Long userId,
            @RequestBody @Valid EatzUserMarkAsDeletedRequest request) {
        userService.delete(id, userId, request.getExistingPassword());
    }

    @DeleteMapping("/{id}/image")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeImage(@PathVariable Long id, @AuthenticatedEatzUserId Long userId) {
        userService.removeImage(id, userId);
    }

    @GetMapping("/{id}/bio")
    public EatzUserBioDto getBio(@PathVariable Long id) {
        return userQueryService.getBio(id);
    }

    @PutMapping("/{id}/bio")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateBio(
            @PathVariable Long id,
            @AuthenticatedEatzUserId Long userId,
            @Valid @RequestBody UpdateEatzUserBioRequest request) {
        userService.updateBio(id, userId, request.getBio());
    }

    @DeleteMapping("/{id}/bio")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeBio(@PathVariable Long id, Long userId) {
        userService.removeBio(id, userId);
    }

    @GetMapping("/me")
    @ResponseStatus(HttpStatus.OK)
    public EatzUserBasicDto getCurrentUser(@AuthenticatedEatzUserId Long userId) {
        return userQueryService.getBasic(userId);
    }

    @GetMapping("/me/bio")
    public EatzUserBioDto getMyBio(@AuthenticatedEatzUserId Long userId) {
        return userQueryService.getBio(userId);
    }

    @PutMapping("/me/bio")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateMyBio(
            @Valid @RequestBody UpdateEatzUserBioRequest request,
            @AuthenticatedEatzUserId Long userId) {
        userService.updateBio(userId, userId, request.getBio());
    }

    @DeleteMapping("/me/bio")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeMyBio(@AuthenticatedEatzUserId Long userId) {
        userService.removeBio(userId, userId);
    }


    @PutMapping("/me/username")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateMyUsername(
            @RequestBody @Valid UpdateEatzUserUsernameRequest request,
            @AuthenticatedEatzUserId Long userId) {
        userService.updateUsername(userId, userId, request.getUsername());
    }

    @PutMapping("/me/image")
    @ResponseStatus(HttpStatus.OK)
    public UploadedImageInfoResponse updateMyImage(
            @RequestParam("image") MultipartFile image,
            @AuthenticatedEatzUserId Long userId) {
        String imageUrl = userService.updateImage(userId, userId, image);
        return new UploadedImageInfoResponse(imageUrl);
    }

    @DeleteMapping("/me/image")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeMyImage(@AuthenticatedEatzUserId Long userId) {
        userService.removeImage(userId, userId);
    }

    @PostMapping("/me/saveds/recipes")
    @ResponseStatus(HttpStatus.CREATED)
    public SavedRecipeCreationInfoResponse saveRecipe(
            @RequestBody SavedRecipeCreateRequest request,
            @AuthenticatedEatzUserId Long userId) {
        return savedRecipeService.save(request.getRecipeId(), userId);
    }

    @DeleteMapping("/me/saveds/recipes/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void unsaveRecipe(@AuthenticatedEatzUserId Long userId, @PathVariable Long id) {
        savedRecipeService.unsave(id, userId);
    }

    @GetMapping("/me/recipes")
    @ResponseStatus(HttpStatus.OK)
    public Page<RecipeBasicDto> getAllMyRecipes(
            @AuthenticatedEatzUserId Long userId,
            @PageableDefault(page = 0, size = 10) Pageable pageable) {
        return recipeQueryService.getAllBasicsByAuthorId(userId, pageable);
    }

    @GetMapping("/me/saveds/recipes")
    @ResponseStatus(HttpStatus.OK)
    public Page<RecipeBasicDto> getAllMySavedRecipes(
            @AuthenticatedEatzUserId Long userId,
            @PageableDefault(page = 0, size = 10) Pageable pageable) {
        return savedRecipeQueryService.getSavedRecipesByUserId(userId, pageable);
    }

    @GetMapping("/me/rateds/recipes")
    @ResponseStatus(HttpStatus.OK)
    public Page<RecipeBasicDto> getAllMyRatedRecipes(
            @AuthenticatedEatzUserId Long userId,
            @PageableDefault(page = 0, size = 10) Pageable pageable) {
        return ratingQueryService.getAllRatedRecipesByAuthorId(userId, pageable);
    }

    /**
     * 사용자가 좋아하는 모든 레시피 목록을 제공합니다.
     * @param pageable 페이징 정보
     * @return 조회된 레시피의 기본 정보 목록과 페이징 정보
     */
    @GetMapping("/me/likeds/recipes")
    @ResponseStatus(HttpStatus.OK)
    public Page<RecipeBasicDto> getAllMyLikedRecipes(
            @AuthenticatedEatzUserId Long userId,
            @PageableDefault(page = 0, size = 10) Pageable pageable) {
        return recipeQueryService.getAllLikedBasicsByUserId(userId, pageable);
    }

    /**
     * 사용자가 좋아하는 모든 재료 목록을 제공합니다.
     * @param pageable 페이징 정보
     * @return 조회된 재료의 기본 정보 목록과 페이징 정보
     */
    @GetMapping("/me/likeds/ingredients")
    @ResponseStatus(HttpStatus.OK)
    public Page<IngredientBasicDto> getAllMyLikedIngredients(
            @AuthenticatedEatzUserId Long userId,
            @PageableDefault(page = 0, size = 10) Pageable pageable) {
        return ingredientQueryService.getAllLikedBasicsByUserId(userId, pageable);
    }

    @GetMapping("/me/comments")
    @ResponseStatus(HttpStatus.OK)
    public Page<CommentEssentialWithRecipeDto> getAllMyComments(
            @AuthenticatedEatzUserId Long userId,
            @PageableDefault(page = 0, size = 10) Pageable pageable) {
        return commentQueryService.getAllEssentialsByAuthorId(userId, pageable);
    }

    @GetMapping("/me/ratings")
    @ResponseStatus(HttpStatus.OK)
    public Page<RatingEssentialWithRecipeDto> getAllMyRatings(
            @AuthenticatedEatzUserId Long userId,
            @PageableDefault(page = 0, size = 10) Pageable pageable) {
        return ratingQueryService.getAllEssentialsWithRecipeByAuthorId(userId, pageable);
    }

    @GetMapping("/me/likeds/recipes/count")
    @ResponseStatus(HttpStatus.OK)
    public CountResponse getMyLikedRecipeCount(@AuthenticatedEatzUserId Long userId) {
        return likedRecipeService.countByUserId(userId);
    }

    @GetMapping("/me/rateds/count")
    @ResponseStatus(HttpStatus.OK)
    public CountResponse getRatedRecipeCount(@AuthenticatedEatzUserId Long userId) {
        return ratingQueryService.countAllRatedRecipesByAuthorId(userId);
    }

}
