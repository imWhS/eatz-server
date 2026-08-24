package imwhs.eatz_server.repository.plan;

import imwhs.eatz_server.domain.Plan;
import imwhs.eatz_server.exception.EatzInvalidRequestArgumentException;
import imwhs.eatz_server.exception.PlanDuplicatedException;
import imwhs.eatz_server.exception.PlanNotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Plan 엔티티의 기본적인 조회를 포함한 CRUD 쿼리 작업을 처리하는 Spring Data JPA 리포지토리입니다.
 */
@Repository
public interface PlanRepository extends JpaRepository<Plan, Long>, PlanQueryRepository {

    default Plan get(Long id) {
        if (id == null) { throw new EatzInvalidRequestArgumentException("플랜의 ID가 필요해요."); }
        return findByIdAndDeletedAtIsNull(id).orElseThrow(() -> new PlanNotFoundException(id));
    }

    default void validateDuplicates(Long recipeId, Long userId, LocalDateTime date) {
        if (recipeId == null) { throw new EatzInvalidRequestArgumentException("레시피의 ID가 필요해요."); }
        if (userId == null) { throw new EatzInvalidRequestArgumentException("사용자의 ID가 필요해요."); }
        if (existsByRecipeIdAndUserIdAndScheduledAtAndDeletedAtIsNull(recipeId, userId, date)) {
            throw new PlanDuplicatedException();
        }
    }

    Optional<Plan> findByIdAndDeletedAtIsNull(Long id);

    boolean existsByRecipeIdAndUserIdAndScheduledAtAndDeletedAtIsNull(Long recipeId, Long userId, LocalDateTime date);

}
