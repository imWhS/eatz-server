package imwhs.eatz_server.repository.recipe;

import imwhs.eatz_server.domain.Recipe;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


/**
 * RecipeRepository 클래스.
 * <p>
 *     Recipe 엔티티에 대해 기본적인 조회를 포함한 CRUD 쿼리 작업을 처리하는 Spring Data JPA 리포지토리입니다.
 * </p>
 */
@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Long> {

    /**
     * 식별자로 특정 레시피를 조회합니다.
     * 삭제 처리된 레시피는 조회 대상에서 제외됩니다.
     * @param id 레시피 식별자.
     * @return Optional로 wrapping된 레시피 엔티티.
     */
    Optional<Recipe> findByIdAndDeletedAtIsNull(Long id);

    /**
     * 모든 레시피를 페이징 적용해 조회합니다.
     * 삭제 처리된 레시피는 조회 대상에서 제외됩니다.
     * @param pageable 페이징 설정 정보.
     * @return 페이징 적용된 모든 레시피 컬렉션.
     */
    Page<Recipe> findAllByDeletedAtIsNull(Pageable pageable);

    /**
     * 특정 사용자가 등록한 모든 레시피를 페이징 적용해 조회합니다.
     * 삭제 처리된 레시피는 조회 대상에서 제외됩니다.
     * @param userId 사용자 식별자.
     * @param pageable 페이징 설정 정보.
     * @return 페이징 적용된 모든 레시피 컬렉션.
     */
    Page<Recipe> findAllByUserIdAndDeletedAtIsNull(Long userId, Pageable pageable);

}
