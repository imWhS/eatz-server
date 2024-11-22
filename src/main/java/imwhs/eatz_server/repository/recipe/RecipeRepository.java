package imwhs.eatz_server.repository.recipe;

import imwhs.eatz_server.domain.Recipe;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


/**
 * RecipeRepository 리포지토리.<br/>
 * <p>
 * Recipe 엔티티에 대해 CRUD를 포함한 데이터 처리 작업을 수행합니다.
 */
@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Long> {

    Optional<Recipe> findByIdAndDeletedAtIsNull(Long id);

    Page<Recipe> findAllByDeletedAtIsNull(Pageable pageable);

    Page<Recipe> findAllByUserIdAndDeletedAtIsNull(Long userId, Pageable pageable);

}
