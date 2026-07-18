package imwhs.eatz_server.service.rating;

import imwhs.eatz_server.domain.eatzuser.EatzUser;
import imwhs.eatz_server.domain.Rating;
import imwhs.eatz_server.domain.recipe.Recipe;
import imwhs.eatz_server.dto.rating.*;
import imwhs.eatz_server.exception.*;
import imwhs.eatz_server.repository.EatzUserRepository;
import imwhs.eatz_server.repository.rating.RatingRepository;
import imwhs.eatz_server.repository.recipe.RecipeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 평가(Rating) 상태를 변경할 수 있는 서비스입니다.
 *
 * <ul>
 *     <li> Rating 생성 뿐 아니라 수정, 삭제 등 엔티티 데이터를 변경하는 쓰기 전용 비즈니스 로직을 담당합니다. </li>
 *     <li> 읽기 전용 비즈니스 로직은 RatingQueryService에서 처리합니다. </li>
 * </ul>
 */
@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RatingService {

    private final RatingRepository ratingRepository;
    private final RecipeRepository recipeRepository;
    private final EatzUserRepository userRepository;

    /**
     * 평가를 생성하고, 레시피 등 평가와 연관된 정보를 매핑한 후 저장합니다.
     * @param recipeId 평가를 달 레시피의 ID
     * @param authorId 평가 등록을 요청한 사용자의 ID (작성자의 ID)
     * @param score 평가의 점수
     * @param content 평가의 내용
     * @return 평가의 생성 정보를 담은 응답 DTO
     */
    @Transactional(rollbackFor = Exception.class)
    public RatingCreationInfoResponse register(Long recipeId, Long authorId, int score, String content) {
        Recipe recipe = recipeRepository.getReference(recipeId);
        EatzUser author = userRepository.getReference(authorId);
        ratingRepository.validateDuplicates(recipeId, authorId);

        Rating rating = Rating.create(author, recipe, score, content);
        ratingRepository.save(rating);

        return new RatingCreationInfoResponse(rating);
    }

    /**
     * 평가를 업데이트합니다.
     * @param id 업데이트할 평가의 ID
     * @param authorId 평가 업데이트를 요청한 작성자의 ID
     * @param score 업데이트할 평가의 점수. null일 경우 업데이트하지 않습니다.
     * @param content 업데이트할 평가의 내용. null일 경우 업데이트하지 않습니다.
     */
    @Transactional(rollbackFor = Exception.class)
    public void update(Long id, Long authorId, Integer score, String content) {
        userRepository.validateExists(authorId);
        Rating rating = ratingRepository.get(id);
        rating.update(authorId, score, content);
    }

    /**
     * 평가를 업데이트합니다.
     * @param recipeId 업데이트할 평가가 달린 레시피의 ID
     * @param authorId 평가 업데이트를 요청한 작성자의 ID
     * @param score 업데이트할 평가의 점수. null일 경우 업데이트하지 않습니다.
     * @param content 업데이트할 평가의 내용. null일 경우 업데이트하지 않습니다.
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateByRecipeId(Long recipeId, Long authorId, Integer score, String content) {
        recipeRepository.validateExists(recipeId);
        Rating rating = ratingRepository.findByRecipeIdAndAuthorIdAndDeletedAtIsNull(recipeId, authorId)
                .orElseThrow(RatingNotFoundException::new);
        rating.update(authorId, score, content);
    }

    /**
     * 평가를 삭제 처리합니다.
     * <p>
     *     평가의 작성자 뿐 아니라, 평가가 달린 레시피의 작성자 또는, 권리자 역할이 있는 사용자도 삭제 처리 할 수 있습니다.
     * </p>
     * @param id 삭제 처리할 평가의 ID
     * @param userId 평가 삭제 처리를 요청한 사용자의 ID (작성자의 ID)
     */
    @Transactional(rollbackFor = Exception.class)
    public void markAsDeleted(Long id, Long userId) {
        EatzUser user = userRepository.get(userId);
        Rating rating = ratingRepository.getWithRecipe(id);
        rating.markAsDeleted(user);
    }

    /**
     * 특정 사용자가 특정 레시피에 작성한 평가를 삭제 처리합니다.
     * <ul>
     *     <li> 삭제 처리를 요청한 사용자가 평가의 작성자이면서, ID를 알 수 없는 평가를 삭제할 때 사용할 수 있습니다. </li>
     * </ul>
     * @param recipeId 삭제 처리할 평가가 달린 레시피의 ID
     * @param authorId 평가 삭제 처리를 요청한, 해당 평가 작성자의 ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void markAsDeletedByRecipeIdAndUserId(Long recipeId, Long authorId) {
        EatzUser user = userRepository.get(authorId);
        Rating rating = ratingRepository.findByRecipeIdAndAuthorIdAndDeletedAtIsNull(recipeId, authorId)
                .orElseThrow(() -> new RatingNotFoundException());
        rating.markAsDeleted(user);
    }

}
