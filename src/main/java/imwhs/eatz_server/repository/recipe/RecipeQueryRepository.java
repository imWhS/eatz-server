package imwhs.eatz_server.repository.recipe;

import imwhs.eatz_server.dto.recipe.*;
import imwhs.eatz_server.dto.recipe.cookable.CookableRecipeDto;
import imwhs.eatz_server.dto.recipe.cookable.CookableRecipesRequest;
import imwhs.eatz_server.dto.recipe.explore.ExploreRecipeDto;
import imwhs.eatz_server.dto.recipe.explore.ExploreRecipesRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface RecipeQueryRepository {

    /**
     * 사용자가 좋아하는 레시피 목록을 조회합니다.
     * <ul>
     *     <li> 차단한 사용자의 레시피는 조회 대상에서 제외합니다. </li>
     * </ul>
     * @param id 사용자의 ID
     * @param pageable 페이징 정보
     * @return 사용자가 좋아하는 레시피 목록 및 페이징 정보
     */
    Page<RecipeBasicDto> findAllLikedBasicsByUserId(Long id, Pageable pageable);

    /**
     * 사용자가 평가한 레시피의 기본 정보 목록을 조회합니다.
     * <ul>
     *     <li> 차단한 사용자의 레시피는 조회 대상에서 제외합니다. </li>
     * </ul>
     * @param id 사용자의 ID
     * @param pageable 페이징 정보
     * @return 사용자가 평가한 레시피 목록 및 페이징 정보
     */
    Page<RecipeBasicDto> findAllRatedRecipeBasicsByAuthorId(Long id, Pageable pageable);

    /**
     * 사용자가 저장한 레시피의 기본 정보 목록을 조회합니다.
     * <ul>
     *     <li> 차단한 사용자의 레시피는 조회 대상에서 제외합니다. </li>
     * </ul>
     * @param id 사용자의 ID
     * @param pageable 페이징 정보
     * @return 사용자가 저장한 레시피 목록 및 페이징 정보
     */
    Page<RecipeBasicDto> findAllSavedRecipeBasicsByUserId(Long id, Pageable pageable);

    /**
     * 사용자가 작성한 모든 레시피의 기본 정보 목록을 조회합니다.
     * <ul>
     *     <li> 사용자의 ID를 전달받으면, 조회하려는 레시피에 대한 해당 사용자의 context를 포함합니다. </li>
     * </ul>
     * @param id 작성자의 ID
     * @param pageable 페이징 정보
     * @return 조회된 레시피의 기본 정보 목록 및 페이징 정보
     */
    Page<RecipeBasicDto> findAllBasicsByAuthorId(Long id, Pageable pageable);

    /**
     * 레시피의 상세한 정보를 조회합니다.
     * <ul>
     *     <li> 레시피의 상세한 정보에는 Recipe의 모든 필드 데이터와
     *          Recipe와 연관 관계인 Author, Comment, Rating 등의 데이터가 포함됩니다. </li>
     *     <li> 레시피가 속한 태그(RecipeTag) 등 1:N 연관 관계의 컬렉션 필드는 카테시안 곱을 방지하고,
     *          쿼리 성능을 최적화하기 위해 조회 대상에서 제외합니다. </li>
     *     <li> 사용자의 ID를 전달받으면, 조회하려는 레시피에 대한 해당 사용자의 context를 포함합니다. </li>
     *     <li> 차단한 사용자의 레시피는 조회 대상에서 제외합니다. </li>
     * </ul>
     * @param id 레시피의 ID
     * @param userId 사용자의 ID
     * @return 조회된 레시피의 상세한 정보를 담고 있는 Optional
     */
    Optional<RecipeDetailDto> findDetail(Long id, Long userId);

    /**
     * 키워드(제목, 설명 검색어)로 검색된 레시피 기본 정보 목록을 조회합니다.
     * <ul>
     *     <li> 키워드가 없으면, 전체 레시피 목록을 조회합니다. </li>
     *     <li> 차단한 사용자의 레시피는 조회 대상에서 제외합니다. </li>
     * </ul>
     * @param keyword 키워드
     * @param userId 사용자의 ID. 게스트 사용자일 경우 null을 전달합니다.
     * @param pageable 페이징 정보
     * @return 검색된 레시피 기본 정보 목록 및 페이징 정보
     */
    Page<RecipeBasicDto> findAllBasics(String keyword, Long userId, Pageable pageable);

    /**
     * '둘러보기(Explore)'에서 사용할 레시피 목록을 선택 조건에 맞춰 조회합니다.
     * <ul>
     *     <li> 차단한 사용자의 레시피는 조회 대상에서 제외합니다. </li>
     * </ul>
     * @param request 검색 키워드, 필터링 등의 선택 조건과 정렬 조건 등을 포함하는 요청 DTO
     * @param userId 사용자의 ID. 게스트 사용자일 경우 null을 전달합니다.
     * @param pageable 페이징 정보
     * @return 검색 결과 레시피 목록 및 페이징 정보
     */
    Page<ExploreRecipeDto> findAllExploreRecipes(ExploreRecipesRequest request, Long userId, Pageable pageable);

    /**
     * '지금 요리(Cookable)'에서 사용할 레시피 목록을 선택 조건에 맞춰 조회합니다.
     * <ul>
     *     <li> 차단한 사용자의 레시피는 조회 대상에서 제외합니다. </li>
     * </ul>
     * @param request 검색 키워드, 필터링, 지금 요리 가능한 레시피만 포함 여부 등의 선택 조건과 정렬 조건 등을 포함하는 요청 DTO
     * @param userId 사용자의 ID. 게스트 사용자일 경우 null을 전달합니다.
     * @param ingredientIdsByUser 현재 로그인한 사용자가 보관함에 추가한 재료 ID 목록
     * @param kitchenwareIdsByUser 현재 로그인한 사용자가 보관함에 추가한 도구 ID 목록
     * @param pageable 페이징 정보
     * @return 검색 결과 레시피 목록 및 페이징 정보
     */
    Page<CookableRecipeDto> findAllCookableRecipes(
            CookableRecipesRequest request,
            Long userId,
            List<Long> ingredientIdsByUser,
            List<Long> kitchenwareIdsByUser, Pageable pageable);

}
