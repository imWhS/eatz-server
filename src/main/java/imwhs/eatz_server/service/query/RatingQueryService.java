package imwhs.eatz_server.service.query;

import imwhs.eatz_server.domain.Rating;
import imwhs.eatz_server.dto.RatingResponseDto;
import imwhs.eatz_server.exception.RatingNotFoundException;
import imwhs.eatz_server.repository.EatzUserRepository;
import imwhs.eatz_server.repository.RatingRepository;
import imwhs.eatz_server.repository.RecipeRepository;
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
    private static final int DEFAULT_PAGE_NUMBER = 0;
    private static final int DEFAULT_PAGE_SIZE = 10;

    /**
     * ID로 특정 평가 조회.
     */
    public RatingResponseDto findRating(Long id) {
        Rating rating = ratingRepository.findRatingJoinUserRecipeById(id)
                .orElseThrow(() -> new RatingNotFoundException("id " + id + "에 해당하는 평가가 존재하지 않습니다."));
        return new RatingResponseDto(rating);
    }

    /**
     * 시용자 ID, 레시피 ID로 특정 평가 조회.
     */
    public RatingResponseDto findRating(Long userId, Long recipeId) {
        Rating rating = ratingRepository.findRatingJoinUserRecipeByUserIdAndRecipeId(userId, recipeId)
                .orElseThrow(() -> new RatingNotFoundException("평가가 존재하지 않습니다."));
        return new RatingResponseDto(rating);
    }

    /**
     * 특정 레시피에 달린 모든 평가 조회.
     */
    public Page<RatingResponseDto> findAllRatings(Long recipeId, Integer pageNumber, Integer pageSize) {
        int number = (pageNumber == null ? DEFAULT_PAGE_NUMBER : pageNumber);
        int size = (pageSize == null ? DEFAULT_PAGE_SIZE : pageSize);

        PageRequest pageRequest = PageRequest.of(number, size);
        Page<Rating> ratings = ratingRepository.findAllRatingsJoinUserRecipeByRecipeId(recipeId, pageRequest);

        return ratings.map(RatingResponseDto::new);
    }

}
