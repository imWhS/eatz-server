package imwhs.eatz_server.service.rating;

import imwhs.eatz_server.dto.CountResponse;
import imwhs.eatz_server.dto.rating.*;
import imwhs.eatz_server.dto.recipe.RecipeBasicDto;
import imwhs.eatz_server.repository.EatzUserRepository;
import imwhs.eatz_server.repository.rating.RatingRepository;
import imwhs.eatz_server.repository.recipe.RecipeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * 평가(Rating) 관련 정보를 조회하기 위한 서비스입니다.
 * Rating에 대한 읽기 전용 쿼리 메서드를 제공합니다.
 */
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class RatingQueryService {

    private final RatingRepository ratingRepository;
    private final RecipeRepository recipeRepository;
    private final EatzUserRepository userRepository;

    /**
     * 레시피에 달린 모든 평가의 기본 정보 목록을 가져옵니다.
     * <ul>
     *     <li> 사용자가 차단한 사용자가 작성한 평가는 조회 대상에서 제외합니다. </li>
     * </ul>
     * @param id 레시피의 ID
     * @param userId 사용자의 ID
     * @param pageable 페이징 정보
     * @return 레시피에 달린 모든 평가의 기본 정보 목록과 페이징 정보
     */
    public Page<RatingBasicDto> getAllBasicsByRecipeId(Long id, Long userId, Pageable pageable) {
        recipeRepository.existsById(id);
        return ratingRepository.findAllBasicsByRecipeId(id, userId, pageable);
    }

    /**
     * 사용자가 등록한 모든 평가 및 레시피의 필수 정보 목록을 가져옵니다.
     * @param id 레시피의 ID
     * @param pageable 페이징 정보
     * @return 레시피에 달린 모든 평가 및 레시피의 필수 정보 목록과 페이징 정보
     */
    public Page<RatingEssentialWithRecipeDto> getAllEssentialsWithRecipeByAuthorId(Long id, Pageable pageable) {
        userRepository.existsById(id);
        return ratingRepository.findAllEssentialsWithRecipeByAuthorId(id, pageable);
    }

    /**
     * 레시피에 달린 모든 평가의 지표 정보를 가져옵니다.
     * @param id 레시피의 ID
     * @return 레시피에 달린 모든 평가의 지표 정보
     */
    public RatingIndicatorDto getIndicatorByRecipeId(Long id) {
        recipeRepository.existsById(id);
        return ratingRepository.findIndicatorByRecipeId(id);
    }

    /**
     * 특정 사용자(작성자)가 특정 레시피에 단 평가의 기본 정보를 가져옵니다.
     * @param recipeId 레시피의 ID
     * @param authorId 평가 작성자의 ID
     * @return 평가 기본 정보
     */
    public Optional<RatingBasicDto> getBasicByRecipeIdAndAuthorId(Long recipeId, Long authorId) {
        return ratingRepository.findBasicByRecipeIdAndAuthorId(recipeId, authorId);
    }

    /**
     * 사용자가 평가를 등록한 모든 레시피의 기본 정보 목록을 가져옵니다.
     * <ul>
     *     <li> 차단한 사용자의 레시피는 조회 대상에서 제외합니다. </li>
     * </ul>
     * @param id 평가 작성자의 ID
     * @return 레시피의 기본 정보 목록을 담고 있는 DTO
     */
    public Page<RecipeBasicDto> getAllRatedRecipesByAuthorId(Long id, Pageable pageable) {
        return recipeRepository.findAllRatedRecipeBasicsByAuthorId(id, pageable);
    }

    /**
     * 사용자가 평가를 등록한 모든 레시피의 수를 가져옵니다.
     * @param id 평가 작성자의 ID
     * @return 사용자가 평가를 등록한 모든 레시피의 수를 담은 응답 DTO
     */
    public CountResponse countAllRatedRecipesByAuthorId(Long id) {
        return new CountResponse(ratingRepository.countAllRatedRecipesByAuthorId(id));
    }

}
