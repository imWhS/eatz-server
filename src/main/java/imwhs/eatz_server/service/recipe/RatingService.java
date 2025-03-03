package imwhs.eatz_server.service.recipe;

import imwhs.eatz_server.domain.eatzuser.EatzUser;
import imwhs.eatz_server.domain.recipe.Rating;
import imwhs.eatz_server.domain.recipe.Recipe;
import imwhs.eatz_server.dto.Paged;
import imwhs.eatz_server.dto.rating.*;
import imwhs.eatz_server.exception.EatzUserNotFoundException;
import imwhs.eatz_server.exception.RatingNotFoundException;
import imwhs.eatz_server.exception.RecipeNotFoundException;
import imwhs.eatz_server.exception.UnauthorizedEatzUserException;
import imwhs.eatz_server.repository.eatzuser.EatzUserRepository;
import imwhs.eatz_server.repository.rating.RatingRepository;
import imwhs.eatz_server.repository.recipe.RecipeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RatingService {

    private final RatingRepository ratingRepository;

    private final RecipeRepository recipeRepository;

    private final EatzUserRepository userRepository;

    /**
     * 새 평가를 등록합니다.
     * @param recipeId 평가를 추가할 레시피의 ID.
     * @param username 평가 등록을 요청한 사용자의 사용자 이름.
     * @param score 평가 점수.
     * @param content 평가 내용.
     * @return 등록 완료된 평가의 ID.
     */
    @Transactional
    public Long register(Long recipeId, String username, int score, String content) {
        validateDuplicates(recipeId, username);

        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new RecipeNotFoundException(recipeId));

        EatzUser user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EatzUserNotFoundException(username));

        Rating rating = Rating.create(user, recipe, score, content);
        ratingRepository.save(rating);
        return rating.getId();
    }

    /**
     * 평가를 업데이트합니다.
     * @param ratingId 업데이트할 평가의 식별자.
     * @param username 평가 업데이트를 요청한 사용자의 식별자.
     * @param score 업데이트할 평가의 점수. null일 경우 업데이트하지 않습니다.
     * @param content 업데이트할 평가의 내용. null일 경우 업데이트하지 않습니다.
     */
    @Transactional
    public void updateRating(Long ratingId, String username, Integer score, String content) {
        Rating rating = ratingRepository.findById(ratingId)
                .orElseThrow(() -> new RatingNotFoundException("id가 " + ratingId + "인 평가가 존재하지 않아요."));

        if (!Objects.equals(rating.getUser().getUsername(), username)) {
            throw new UnauthorizedEatzUserException("평가를 수정할 권한이 없어요.");
        }

        if (rating.isMarkedAsDeleted()) {
            throw new IllegalStateException("삭제 처리된 평가예요.");
        }

        rating.update(score, content);
    }

    /**
     * 평가를 삭제 처리합니다.
     * @param ratingId 삭제 처리할 평가의 식별자.
     * @param userId 평가 삭제 처리를 요청한 사용자의 식별자.
     */
    @Transactional
    public void deleteRating(Long ratingId, Long userId) {
//        Rating rating = getRating(ratingId, userId);
//        rating.markAsDeleted();
    }

    public Page<RatingItemDto> findAllByRecipe(Long id, Pageable pageable) {
        validateRecipe(id);
        return ratingRepository.findWithUserByRecipeId(id, pageable);
    }

    public Page<RatingWithRecipeResponseDto> findRatingsByUser(Long id, Pageable pageable) {
        validateUser(id);
        return ratingRepository.findWithRecipeByUserId(id, pageable);
    }


    public RatingsDetailDto getWithDetail(Long recipeId, Pageable pageable) {
        validateRecipe(recipeId);

        RatingsSummaryDistributionDto summary = ratingRepository.findAverageScoreByRecipeId(recipeId);
        Paged<RatingItemDto> ratings = new Paged<>(ratingRepository.findWithUserByRecipeId(recipeId, pageable));
        return new RatingsDetailDto(summary, ratings);
    }

    /**
     * 사용자가 레시피에 평점을 등록했는지 확인합니다.
     * @param recipeId 레시피 ID.
     * @param username 사용자 이름.
     */
    private void validateDuplicates(Long recipeId, String username) {
        if (ratingRepository.existsByRecipeIdAndUserUsername(recipeId, username)) {
            throw new IllegalArgumentException("이미 평가를 남긴 레시피입니다.");
        }
    }

    private void validateUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new EatzUserNotFoundException(id);
        }
    }

    private void validateRecipe(Long recipeId) {
        if (!recipeRepository.existsById(recipeId)) {
            throw new RecipeNotFoundException(recipeId);
        }
    }

}
