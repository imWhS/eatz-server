package imwhs.eatz_server.service.query;

import imwhs.eatz_server.domain.Rating;
import imwhs.eatz_server.dto.rating.RatingResponseDto;
import imwhs.eatz_server.exception.EatzUserNotFoundException;
import imwhs.eatz_server.exception.RatingNotFoundException;
import imwhs.eatz_server.exception.RecipeNotFoundException;
import imwhs.eatz_server.repository.eatzuser.EatzUserRepository;
import imwhs.eatz_server.repository.RatingRepository;
import imwhs.eatz_server.repository.recipe.RecipeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class RatingQueryService {

    private final RatingRepository ratingRepository;

    private final EatzUserRepository userRepository;

    private final RecipeRepository recipeRepository;

    /**
     * 페이지 번호 및 크기 기본 값.
     * 응답 메시지에 포함시킬 시용자에 대해 페이징 처리를 하기 위해 정의합니다.
     */
    private static final int DEFAULT_CURRENT_PAGE = 0;
    private static final int DEFAULT_PAGING_SIZE = 10;

    /**
     * ID로 특정 평가 조회.
     */
    public RatingResponseDto findRating(Long id) {
        Rating rating = ratingRepository.findJoinUserRecipeById(id)
                .orElseThrow(() -> new RatingNotFoundException("id " + id + "에 해당하는 평가가 존재하지 않습니다."));
        return new RatingResponseDto(rating);
    }

    /**
     * 시용자 ID, 레시피 ID로 특정 평가 조회.
     */
    public RatingResponseDto findRating(Long userId, Long recipeId) {
        validateUser(userId);

        validateRecipe(recipeId);

        Rating rating = ratingRepository.findJoinUserRecipeByUserIdAndRecipeId(userId, recipeId)
                .orElseThrow(() -> new RatingNotFoundException("평가가 존재하지 않습니다."));

        return new RatingResponseDto(rating);
    }

    /**
     * 특정 레시피에 달린 모든 평가 조회.
     */
    public Page<RatingResponseDto> findRatings(Long recipeId, Integer currentPage, Integer pagingSize) {
        validateRecipe(recipeId);

        int page = (currentPage == null ? DEFAULT_CURRENT_PAGE : currentPage);
        int size = (pagingSize == null ? DEFAULT_PAGING_SIZE : pagingSize);

        PageRequest pageRequest = PageRequest.of(page, size);
        Page<Rating> ratings = ratingRepository.findJoinUserRecipeByRecipeId(recipeId, pageRequest);

        return ratings.map(RatingResponseDto::new);
    }

    /**
     * 특정 사용자가 등록한 모든 평가 조회.
     */
    public Page<RatingResponseDto> findRatingsByUser(Long userId, Integer currentPage, Integer pagingSize) {
        validateUser(userId);

        int page = (currentPage == null ? DEFAULT_CURRENT_PAGE : currentPage);
        int size = (pagingSize == null ? DEFAULT_PAGING_SIZE : pagingSize);

        PageRequest pageRequest = PageRequest.of(page, size);
        Page<Rating> ratings = ratingRepository.findJoinUserRecipeByUserId(userId, pageRequest);

        return ratings.map(RatingResponseDto::new);
    }

    private void validateUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new EatzUserNotFoundException("id가 " + userId + "인 사용자가 존재하지 않습니다.");
        }
    }

    private void validateRecipe(Long recipeId) {
        if (!recipeRepository.existsById(recipeId)) {
            throw new RecipeNotFoundException("id가 " + recipeId + "인 레시피가 존재하지 않습니다.");
        }
    }

}
