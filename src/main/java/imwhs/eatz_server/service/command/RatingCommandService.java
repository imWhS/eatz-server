package imwhs.eatz_server.service.command;

import imwhs.eatz_server.domain.EatzUser;
import imwhs.eatz_server.domain.Rating;
import imwhs.eatz_server.domain.Recipe;
import imwhs.eatz_server.exception.EatzUserNotFoundException;
import imwhs.eatz_server.exception.RatingNotFoundException;
import imwhs.eatz_server.exception.RecipeNotFoundException;
import imwhs.eatz_server.exception.UnauthorizedEatzUserException;
import imwhs.eatz_server.repository.eatzuser.EatzUserRepository;
import imwhs.eatz_server.repository.RatingRepository;
import imwhs.eatz_server.repository.RecipeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RatingCommandService {

    private final RatingRepository ratingRepository;

    private final RecipeRepository recipeRepository;

    private final EatzUserRepository userRepository;

    /**
     * 새 평가를 등록합니다.
     * @param recipeId 평가를 달 레시피의 ID
     * @param userId 평가 등록을 요청한 사용자의 ID
     * @param score 등록할 평가의 점수
     * @param content 등록할 평가의 내용
     * @return 등록 완료된 평가의 ID
     */
    @Transactional
    public Long registerRating(Long recipeId, Long userId, int score, String content) {
        validateRatingScore(score);

        // 이미 해당 레시피에 해당 사용자가 평점을 등록했는지 확인합니다.
        boolean isDuplicatedRating = ratingRepository.existsByRecipeIdAndUserId(recipeId, userId);
        if (isDuplicatedRating) {
            throw new IllegalArgumentException("이미 사용자가 레시피에 평가를 남겼습니다.");
        }

        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new RecipeNotFoundException("id가 " + recipeId + "인 레시피가 존재하지 않습니다."));
        EatzUser user = userRepository.findById(userId)
                .orElseThrow(() -> new EatzUserNotFoundException("id가 " + userId + "인 사용자가 존재하지 않습니다."));

        Rating rating = new Rating(user, recipe, score);
        ratingRepository.save(rating);

        return rating.getId();
    }

    /**
     * 평가를 수정합니다.
     * @param ratingId 수정할 평가의 ID
     * @param userId 평가 수정을 요청한 사용자의 ID
     * @param score 수정할 평가의 점수. null일 경우 수정하지 않습니다.
     * @param content 수정할 평가의 내용. null일 경우 수정하지 않습니다.
     */
    @Transactional
    public void updateRating(Long ratingId, Long userId, Integer score, String content) {
        Rating rating = getRating(ratingId, userId);

        // 파라미터를 통해 수정할 평가의 점수를 전달받은 경우, 해당 점수로 수정합니다.
        if (score != null) {
            validateRatingScore(score);
            rating.updateScore(score);
        }

        // 파라미터를 통해 수정할 평가의 내용을 전달받은 경우, 해당 내용으로 변경합니다.
        if (content != null) {
            rating.updateContent(content);
        }
    }

    /**
     * 평가를 삭제 처리합니다.
     * @param ratingId 삭제 처리할 평가의 ID
     * @param userId 평가 삭제 처리를 요청한 사용자의 ID
     */
    @Transactional
    public void deleteRating(Long ratingId, Long userId) {
        Rating rating = getRating(ratingId, userId);
        rating.markAsDeleted();
    }

    /**
     * Rating 엔티티를 가져옵니다.
     * @param ratingId 레시피 ID
     * @param userId 엔티티를 요청한 사용자 ID
     * @throws RatingNotFoundException ratingId에 해당하는 Rating 엔티티가 존재하지 않을 경우
     * @throws UnauthorizedEatzUserException 평가를 등록한 사용자의 id가 userId와 일치하지 않을 경우(접근 권한이 없는 사용자의 요청인 경우)
     * @throws IllegalArgumentException 가져오려는 평가가 삭제 처리된 경우
     * @return Rating 엔티티
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
     * 평가 점수가 올바른 값인지 확인합니다.
     * @param score 평가 점수
     */
    private static void validateRatingScore(Integer score) {
        if (score == null || score < 1 || 5 < score) {
            throw new IllegalArgumentException("평가 점수는 1에서 5 사이의 자연수로만 설정할 수 있습니다.");
        }
    }

}
