package imwhs.eatz_server.service.liked;

import imwhs.eatz_server.domain.eatzuser.EatzUser;
import imwhs.eatz_server.domain.liked.LikedRecipe;
import imwhs.eatz_server.domain.recipe.Recipe;
import imwhs.eatz_server.dto.CountResponse;
import imwhs.eatz_server.dto.liked.LikedRecipeBasicDto;
import imwhs.eatz_server.repository.EatzUserRepository;
import imwhs.eatz_server.repository.liked.LikedRecipeRepository;
import imwhs.eatz_server.repository.recipe.RecipeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * 레시피의 좋아요(LikedRecipe) 상태를 변경할 수 있는 서비스입니다.
 * <ul>
 *     <li> LikedRecipe 생성 뿐 아니라, 수정, 삭제 등 엔티티 데이터를 변경하는 쓰기 전용 비즈니스 로직을 담당합니다. </li>
 *     <li> 읽기 전용 비즈니스 로직은 RatingQueryService에서 처리합니다. </li>
 * </ul>
 */
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class LikedRecipeService {

    private final EatzUserRepository userRepository;
    private final RecipeRepository recipeRepository;
    private final LikedRecipeRepository likedRecipeRepository;

    /**
     * 사용자가 레시피에 좋아요를 표시합니다.
     * <ul>
     *     <li> 사용자가 과거에 최소 1회 이상 해당 레시피에 좋아요를 표시 또는 취소한 적 있는 경우,
     *          좋아요 상태만 명시적으로 활성화하도록 업데이트합니다. </li>
     *     <li> 사용자가 과거에 한 번도 해당 레시피에 좋아요를 표시 또는 취소한 적 없는 경우, LikedRecipe 레코드를 생성, 저장합니다. </li>
     * </ul>
     *
     * @param recipeId 레시피의 ID
     * @param userId 사용자의 ID
     * @return 레시피 좋아요 기본 정보
     */
    @Transactional(rollbackFor = Exception.class)
    public LikedRecipeBasicDto like(Long recipeId, Long userId) {
        EatzUser user = userRepository.getReference(userId);
        Recipe recipe = recipeRepository.getReference(recipeId);
        Optional<LikedRecipe> existingLiked = likedRecipeRepository.findByUserIdAndRecipeId(userId, recipeId);
        LikedRecipe liked;
        liked = existingLiked.orElseGet(() -> LikedRecipe.create(user, recipe));
        liked.like();
        likedRecipeRepository.save(liked);
        long likedCount = likedRecipeRepository.countAllByRecipeIdAndIsLikedIsTrue(recipeId);

        return new LikedRecipeBasicDto(liked, likedCount);
    }

    /**
     * 사용자가 레시피에 표시했던 좋아요를 취소합니다.
     * <ul>
     *     <li> 사용자가 과거에 최소 1회 이상 해당 레시피에 좋아요를 표시 또는 취소한 적 있는 경우에만
     *          좋아요 상태를 명시적으로 비활성화하도록 업데이트합니다. </li>
     * </ul>
     *
     * @param recipeId 레시피의 ID
     * @param userId 사용자의 ID
     * @return 레시피 좋아요 기본 정보.
     *         해당 레시피에 한 번도 좋아요를 표시 또는 취소한 적 없는 사용자인 경우 DTO에 해당 레시피의 좋아요 수만 포함합니다.
     */
    @Transactional(rollbackFor = Exception.class)
    public LikedRecipeBasicDto unlike(Long recipeId, Long userId) {
        userRepository.validateExists(userId);
        recipeRepository.validateExists(recipeId);
        Optional<LikedRecipe> existingLiked = likedRecipeRepository.findByUserIdAndRecipeId(userId, recipeId);
        if (existingLiked.isPresent()) {
            LikedRecipe liked = existingLiked.get();
            liked.unlike();
        }
        long likedCount = likedRecipeRepository.countAllByRecipeIdAndIsLikedIsTrue(recipeId);
        return new LikedRecipeBasicDto(existingLiked.orElse(null), likedCount);
    }

    public CountResponse countByUserId(Long id) {
        userRepository.validateExists(id);
        long count = likedRecipeRepository.countAllByUserIdAndIsLikedIsTrue(id);
        return new CountResponse(count);
    }

}
