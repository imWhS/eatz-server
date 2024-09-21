package imwhs.eatz_server.service;

import imwhs.eatz_server.domain.EatzUser;
import imwhs.eatz_server.domain.Rating;
import imwhs.eatz_server.domain.Recipe;
import imwhs.eatz_server.exception.EatzUserNotFoundException;
import imwhs.eatz_server.exception.RatingNotFoundException;
import imwhs.eatz_server.exception.RecipeNotFoundException;
import imwhs.eatz_server.exception.UnauthorizedEatzUserException;
import imwhs.eatz_server.repository.CommentRepository;
import imwhs.eatz_server.repository.EatzUserRepository;
import imwhs.eatz_server.repository.RatingRepository;
import imwhs.eatz_server.repository.RecipeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RatingService {

    private final RatingRepository ratingRepository;

    private final RecipeRepository recipeRepository;

    private final EatzUserRepository userRepository;

    @Transactional
    public Long registerRating(Long recipeId, Long userId, int score) {
        validateRatingScore(score);

        // 이미 해당 레시피에 해당 사용자가 평점을 등록했는지 확인합니다.
        boolean isDuplicatedRating = ratingRepository.existsByRecipeIdAndUserId(recipeId, userId);
        if (isDuplicatedRating) {
            throw new IllegalArgumentException("이미 해당 사용자가 레시피에 평가를 등록했기 때문에, 평가를 한 번 더 등록할 수 없습니다.");
        }

        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new RecipeNotFoundException("id가 " + recipeId + "인 레시피가 존재하지 않습니다."));
        EatzUser user = userRepository.findById(userId)
                .orElseThrow(() -> new EatzUserNotFoundException("id가 " + userId + "인 사용자가 존재하지 않습니다."));

        Rating rating = new Rating(user, recipe, score);
        ratingRepository.save(rating);

        return rating.getId();
    }

    @Transactional
    public void updateRating(Long ratingId, Long userId, int score) {
        validateRatingScore(score);
        Rating rating = findRating(ratingId, userId);
        rating.updateScore(score);
    }


    // TODO: 삭제 처리를 0점으로 할지, 엔티티 자체를 삭제할지 결정
    @Transactional
    public void deleteRating(Long ratingId, Long userId) {
        Rating rating = findRating(ratingId, userId);
        ratingRepository.delete(rating);
    }

    /**
     * Rating 엔티티를 가져옵니다.
     * @param ratingId 레시피 ID
     * @param userId 서비스를 요청한 사용자 ID
     * @throws RatingNotFoundException ratingId에 해당하는 Rating 엔티티가 존재하지 않을 경우
     * @throws UnauthorizedEatzUserException 평가를 등록한 사용자의 id가 userId와 일치하지 않을 경우(접근 권한이 없는 사용자의 요청인 경우)
     * @return Rating 엔티티
     */
    private Rating findRating(Long ratingId, Long userId) {
        Rating rating = ratingRepository.findById(ratingId)
                .orElseThrow(() -> new RatingNotFoundException("id가 " + ratingId + "인 평가가 존재하지 않습니다."));

        if (!Objects.equals(rating.getUser().getId(), userId)) {
            throw new UnauthorizedEatzUserException("평가를 등록한 사용자가 아니어서, 수정을 진행할 수 없습니다.");
        }
        return rating;
    }

    /**
     * 평가 점수가 올바른 값인지 확인합니다.
     * @param score 평가 점수
     */
    private static void validateRatingScore(int score) {
        if (score < 1 || 5 < score) {
            throw new IllegalArgumentException("평가 점수는 1에서 5 사이의 자연수로만 설정할 수 있습니다.");
        }
    }

}
