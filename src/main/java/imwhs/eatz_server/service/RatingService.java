package imwhs.eatz_server.service;

import imwhs.eatz_server.domain.EatzUser;
import imwhs.eatz_server.domain.Rating;
import imwhs.eatz_server.domain.Recipe;
import imwhs.eatz_server.exception.EatzUserNotFoundException;
import imwhs.eatz_server.exception.RecipeNotFoundException;
import imwhs.eatz_server.repository.CommentRepository;
import imwhs.eatz_server.repository.EatzUserRepository;
import imwhs.eatz_server.repository.RatingRepository;
import imwhs.eatz_server.repository.RecipeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RatingService {

    private final RatingRepository ratingRepository;

    private final RecipeRepository recipeRepository;

    private final EatzUserRepository userRepository;

    @Transactional
    public Long registerRating(Long recipeId, Long userId, int score) {
        // 이미 해당 레시피에 해당 사용자가 평점을 등록했는지 확인합니다.
        boolean isDuplicatedRating = ratingRepository.existsByRecipeIdAndUserId(recipeId, userId);
        if (isDuplicatedRating) {
            throw new IllegalArgumentException("이미 해당 사용자가 레시피에 평가를 등록했기 때문에, 평가를 한 번 더 등록할 수 없습니다.");
        }

        // 평가 점수가 올바른 값인지 확인합니다.
        if (score < 1 || 5 < score) {
            throw new IllegalArgumentException("평가 점수는 1에서 5 사이의 자연수로만 설정할 수 있습니다.");
        }

        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new RecipeNotFoundException("id가 " + recipeId + "인 레시피가 존재하지 않습니다."));
        EatzUser user = userRepository.findById(userId)
                .orElseThrow(() -> new EatzUserNotFoundException("id가 " + userId + "인 사용자가 존재하지 않습니다."));


        Rating rating = new Rating(user, recipe, score);
        ratingRepository.save(rating);

        return rating.getId();
    }


}
