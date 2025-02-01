package imwhs.eatz_server.service;

import imwhs.eatz_server.domain.eatzuser.EatzUser;
import imwhs.eatz_server.domain.recipe.Rating;
import imwhs.eatz_server.domain.recipe.Recipe;
import imwhs.eatz_server.dto.rating.RatingDtoOld;
import imwhs.eatz_server.exception.EatzUserNotFoundException;
import imwhs.eatz_server.exception.RatingNotFoundException;
import imwhs.eatz_server.exception.RecipeNotFoundException;
import imwhs.eatz_server.exception.UnauthorizedEatzUserException;
import imwhs.eatz_server.repository.eatzuser.EatzUserRepository;
import imwhs.eatz_server.repository.rating.RatingRepository;
import imwhs.eatz_server.repository.recipe.RecipeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RatingService {

    /**
     * 페이지 번호 및 크기 기본 값.
     * <p>
     *     응답 메시지에 포함시킬 시용자에 대해 페이징 처리를 하기 위해 정의합니다.
     * </p>
     */
    private static final int DEFAULT_CURRENT_PAGE = 0;
    private static final int DEFAULT_PAGING_SIZE = 10;

    private final RatingRepository ratingRepository;

    private final RecipeRepository recipeRepository;

    private final EatzUserRepository userRepository;

    /**
     * 새 평가를 등록합니다.
     * @param recipeId 평가를 달 레시피의 식별자.
     * @param userId 평가 등록을 요청한 사용자의 식별자.
     * @param score 등록할 평가의 점수.
     * @param content 등록할 평가의 내용.
     * @return 등록 완료된 평가의 식별자.
     */
    @Transactional
    public Long registerRating(Long recipeId, Long userId, int score, String content) {
        validateRatingScore(score);
        validateDuplicates(recipeId, userId);

        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new RecipeNotFoundException("id가 " + recipeId + "인 레시피가 존재하지 않습니다."));
        EatzUser user = userRepository.findById(userId)
                .orElseThrow(() -> new EatzUserNotFoundException("id가 " + userId + "인 사용자가 존재하지 않습니다."));

        Rating rating = new Rating(user, recipe, score, content);
        ratingRepository.save(rating);

        return rating.getId();
    }

    /**
     * 평가를 업데이트합니다.
     * @param ratingId 업데이트할 평가의 식별자.
     * @param userId 평가 업데이트를 요청한 사용자의 식별자.
     * @param score 업데이트할 평가의 점수. null일 경우 업데이트하지 않습니다.
     * @param content 업데이트할 평가의 내용. null일 경우 업데이트하지 않습니다.
     */
    @Transactional
    public void updateRating(Long ratingId, Long userId, Integer score, String content) {
        Rating rating = getRating(ratingId, userId);

        // 파라미터를 통해 업데이트할 평가의 점수를 전달받은 경우, 해당 점수로 업데이트합니다.
        if (score != null) {
            validateRatingScore(score);
            rating.updateScore(score);
        }

        // 파라미터를 통해 업데이트할 평가의 내용을 전달받은 경우, 해당 내용으로 변경합니다.
        if (content != null) {
            rating.updateContent(content);
        }
    }

    /**
     * 평가를 삭제 처리합니다.
     * @param ratingId 삭제 처리할 평가의 식별자.
     * @param userId 평가 삭제 처리를 요청한 사용자의 식별자.
     */
    @Transactional
    public void deleteRating(Long ratingId, Long userId) {
        Rating rating = getRating(ratingId, userId);
        rating.markAsDeleted();
    }

    /**
     * 식별자로 평가를 조회합니다.
     */
    public RatingDtoOld findRating(Long id) {
        Rating rating = ratingRepository.findJoinUserRecipeById(id)
                .orElseThrow(() -> new RatingNotFoundException("id " + id + "에 해당하는 평가가 존재하지 않습니다."));
        return new RatingDtoOld(rating);
    }

    /**
     * 시용자 식별자, 레시피 식별자로 평가를 조회합니다.
     */
    public RatingDtoOld findRating(Long userId, Long recipeId) {
        validateUser(userId);
        validateRecipe(recipeId);
        Rating rating = ratingRepository.findJoinUserRecipeByUserIdAndRecipeId(userId, recipeId)
                .orElseThrow(() -> new RatingNotFoundException("평가가 존재하지 않습니다."));
        return new RatingDtoOld(rating);
    }

    /**
     * 특정 레시피에 달린 평가를 모두 조회합니다.
     */
    public Page<RatingDtoOld> findRatings(Long recipeId, Integer currentPage, Integer pagingSize) {
        validateRecipe(recipeId);

        int page = (currentPage == null ? DEFAULT_CURRENT_PAGE : currentPage);
        int size = (pagingSize == null ? DEFAULT_PAGING_SIZE : pagingSize);

        PageRequest pageRequest = PageRequest.of(page, size);
        Page<Rating> ratings = ratingRepository.findJoinUserRecipeByRecipeId(recipeId, pageRequest);

        return ratings.map(RatingDtoOld::new);
    }

    /**
     * 특정 사용자가 등록한 평가를 모두 조회합니다.
     */
    public Page<RatingDtoOld> findRatingsByUser(Long userId, Integer currentPage, Integer pagingSize) {
        validateUser(userId);

        int page = (currentPage == null ? DEFAULT_CURRENT_PAGE : currentPage);
        int size = (pagingSize == null ? DEFAULT_PAGING_SIZE : pagingSize);

        PageRequest pageRequest = PageRequest.of(page, size);
        Page<Rating> ratings = ratingRepository.findJoinUserRecipeByUserId(userId, pageRequest);

        return ratings.map(RatingDtoOld::new);
    }

    /**
     * Rating 엔티티를 가져옵니다.
     * @param ratingId 레시피 식별자.
     * @param userId 엔티티를 요청한 사용자 식별자.
     * @throws RatingNotFoundException ratingId에 해당하는 Rating 엔티티가 존재하지 않을 경우.
     * @throws UnauthorizedEatzUserException 평가를 등록한 사용자의 식별자가 userId와 일치하지 않을 경우(접근 권한이 없는 사용자의 요청인 경우).
     * @throws IllegalArgumentException 가져오려는 평가가 삭제 처리된 경우.
     * @return Rating 엔티티.
     */
    private Rating getRating(Long ratingId, Long userId) {
        Rating rating = ratingRepository.findById(ratingId)
                .orElseThrow(() -> new RatingNotFoundException("id가 " + ratingId + "인 평가가 존재하지 않습니다."));

        if (!Objects.equals(rating.getUser().getId(), userId)) {
            throw new UnauthorizedEatzUserException("평가를 등록한 사용자가 아니어서, 권한이 없습니다.");
        }

        if (rating.isMarkedAsDeleted()) {
            throw new IllegalArgumentException("삭제 처리된 평가입니다.");
        }

        return rating;
    }

    /**
     * 레시피에 사용자가 평점을 등록했는지 확인합니다.
     * @param recipeId 레시피 식별자.
     * @param userId 사용자 식별자.
     */
    private void validateDuplicates(Long recipeId, Long userId) {
        if (ratingRepository.existsByRecipeIdAndUserId(recipeId, userId)) {
            throw new IllegalArgumentException("이미 사용자가 레시피에 평가를 남겼습니다.");
        }
    }

    /**
     * 평가 점수가 올바른 값인지 확인합니다.
     * @param score 평가 점수
     */
    private static void validateRatingScore(Integer score) {
        if (score == null || score < 1 || 5 < score) {
            throw new IllegalArgumentException("평가 점수는 1에서 5 사이의 자연수로만 설정할 수 있습니다.");
        }
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
