package imwhs.eatz_server.controller.recipe;

import imwhs.eatz_server.auth.EatzUserAuthUtil;
import imwhs.eatz_server.dto.ApiResponse;
import imwhs.eatz_server.dto.Paged;
import imwhs.eatz_server.dto.rating.CreateRatingDto;
import imwhs.eatz_server.dto.rating.RatingItemDto;
import imwhs.eatz_server.dto.rating.RatingsDetailDto;
import imwhs.eatz_server.dto.rating.UpdateRatingDto;
import imwhs.eatz_server.service.recipe.RatingService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 레시피의 평가를 관리하기 위한 API를 제공하는 컨트롤러입니다.
 */
@RequestMapping("/api/v0/recipes/{recipeId}/ratings")
@RequiredArgsConstructor
@RestController
public class RatingController {

    private final RatingService ratingService;

    /**
     * 레시피에 새 평가를 등록합니다.
     * @param recipeId 레시피 ID.
     * @param dto 등록하려는 평가 관련 정보.
     * @return 생성된 평가의 ID.
     */
    @PostMapping
    public ResponseEntity<ApiResponse<Long>> addRating(@PathVariable Long recipeId, @RequestBody CreateRatingDto dto) {
        Long ratingId = ratingService.register(recipeId, EatzUserAuthUtil.getId(), dto.getScore(), dto.getContent());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(ratingId));
    }

    /**
     * 특정 레시피의 평가를 업데이트합니다.
     * @param id 평가 ID.
     * @param dto 평가 업데이트 정보.
     */
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateRating(@PathVariable Long id, @RequestBody UpdateRatingDto dto) {
        ratingService.update(id, EatzUserAuthUtil.getId(), dto.getScore(), dto.getContent());
    }

    /**
     * 특정 레시피의 평가를 삭제 처리합니다.
     * @param id 평가 ID.
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteRating(@PathVariable Long id) {
        ratingService.delete(id, EatzUserAuthUtil.getId());
    }

    /**
     * 레시피에 추가된 모든 평가 목록을 조회합니다.
     * @param recipeId 레시피 ID.
     * @param pageable 페이징 정보.
     * @return 평가 목록.
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Paged<RatingItemDto>>> findRatings(
            @PathVariable Long recipeId,
            @PageableDefault(page = 0, size = 10) Pageable pageable) {
        Page<RatingItemDto> ratings = ratingService.findByRecipe(recipeId, pageable);
        return ResponseEntity.ok((ApiResponse.success(ratings)));
    }

    /**
     * 레시피에 추가된 모든 평가 목록과 레시피의 평가 통계를 포함하는 레시피 상세 평가 정보를 조회합니다.
     * @param recipeId 레시피 ID.
     * @param pageable 페이징 정보.
     * @return 상세 평가 정보.
     */
    @GetMapping("/detail")
    public ResponseEntity<ApiResponse<RatingsDetailDto>> findRatingsWithDetail(
            @PathVariable Long recipeId,
            @PageableDefault(page = 0, size = 10) Pageable pageable) {
        RatingsDetailDto dto = ratingService.getWithDetail(recipeId, pageable);
        return ResponseEntity.ok((ApiResponse.success(dto)));
    }

}
