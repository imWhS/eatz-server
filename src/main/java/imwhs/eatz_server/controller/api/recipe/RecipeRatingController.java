package imwhs.eatz_server.controller.api.recipe;

import imwhs.eatz_server.dto.rating.*;
import imwhs.eatz_server.resolver.AuthenticatedEatzUserId;
import imwhs.eatz_server.service.rating.RatingQueryService;
import imwhs.eatz_server.service.rating.RatingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * 레시피의 평가를 관리하기 위한 API를 제공하는 컨트롤러입니다.
 */
@RequestMapping("/api/v0/recipes/{recipeId}/ratings")
@RequiredArgsConstructor
@RestController
public class RecipeRatingController {

    private final RatingService ratingService;
    private final RatingQueryService ratingQueryService;

    /**
     * 특정 레시피에 새 평가를 등록합니다.
     * @param recipeId 레시피의 ID
     * @param request 새 평가 등록 요청에 필요한 정보를 담은 DTO
     * @return 생성된 평가 정보를 담은 DTO
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RatingCreationInfoResponse registerRating(
            @PathVariable Long recipeId,
            @AuthenticatedEatzUserId Long userId,
            @Valid @RequestBody RatingCreateRequest request) {
        return ratingService.register(
                recipeId,
                userId,
                request.getScore(),
                request.getContent());
    }

    /**
     * 특정 레시피의 평가를 업데이트합니다.
     * @param recipeId 업데이트할 평가가 달린 레시피의 ID
     * @param request 평가 업데이트 요청에 필요한 정보를 담은 DTO
     */
    @PutMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateRatingByRecipeId(
            @PathVariable Long recipeId,
            @AuthenticatedEatzUserId Long userId,
            @Valid @RequestBody RatingUpdateRequest request) {
        ratingService.updateByRecipeId(recipeId, userId, request.getScore(), request.getContent());
    }

    /**
     * 특정 레시피의 평가를 삭제 처리합니다.
     * @param id 평가의 ID
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void markRatingAsDeleted(@PathVariable Long id, @AuthenticatedEatzUserId Long userId) {
        ratingService.markAsDeleted(id, userId);
    }

    /**
     * 요청한 사용자가 특정 레시피에 등록한 평가를 삭제합니다.
     * @param recipeId 평가의 ID
     */
    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMyRatingByRecipeId(@PathVariable Long recipeId, @AuthenticatedEatzUserId Long userId) {
        ratingService.markAsDeletedByRecipeIdAndUserId(recipeId, userId);
    }

    /**
     * 특정 레시피에 달린 모든 평가의 기본 정보 목록을 가져옵니다.
     * <ul>
     *     <li> 사용자가 차단한 사용자가 작성한 평가는 조회 대상에서 제외합니다. </li>
     * </ul>
     * @param recipeId 레시피의 ID
     * @param userId 요청한 사용자의 ID
     * @param pageable 페이징 정보
     * @return 평가의 기본 정보 목록과 페이징 정보를 담고 있는 DTO
     */
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Page<RatingBasicDto> getRatingsByRecipeId(
            @PathVariable Long recipeId,
            @AuthenticatedEatzUserId(required = false) Long userId,
            @PageableDefault(page = 0, size = 10) Pageable pageable) {
        return ratingQueryService.getAllBasicsByRecipeId(recipeId, userId, pageable);
    }

    @GetMapping("/me")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<RatingBasicDto> getRating(
            @PathVariable Long recipeId,
            @AuthenticatedEatzUserId Long authorId) {
        Optional<RatingBasicDto> rating = ratingQueryService.getBasicByRecipeIdAndAuthorId(recipeId, authorId);
        if (rating.isEmpty()) { return ResponseEntity.noContent().build(); } 
        else { return ResponseEntity.ok(rating.get());}
    }

    @GetMapping("/indicator")
    @ResponseStatus(HttpStatus.OK)
    public RatingIndicatorDto getIndicator(@PathVariable Long recipeId) {
       return ratingQueryService.getIndicatorByRecipeId(recipeId);
    }

}
