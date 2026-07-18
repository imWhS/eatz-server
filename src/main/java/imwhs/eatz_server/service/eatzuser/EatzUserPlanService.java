package imwhs.eatz_server.service.eatzuser;

import imwhs.eatz_server.domain.eatzuser.EatzUser;
import imwhs.eatz_server.domain.Plan;
import imwhs.eatz_server.domain.recipe.Recipe;
import imwhs.eatz_server.dto.plan.*;
import imwhs.eatz_server.exception.*;
import imwhs.eatz_server.repository.EatzUserRepository;
import imwhs.eatz_server.repository.recipe.RecipeRepository;
import imwhs.eatz_server.repository.plan.PlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 사용자의 플랜(Plan) 상태를 변경할 수 있는 서비스를 제공합니다.
 * <ul>
 *     <li> Plan 생성 뿐 아니라, 수정, 삭제 등 엔티티 데이터를 변경하는 쓰기 전용 비즈니스 로직을 담당합니다. </li>
 *     <li> 사용자의 읽기 전용 비즈니스 로직은 EatzUserPlanQueryService에서 처리합니다. </li>
 * </ul>
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EatzUserPlanService {

    private final PlanRepository planRepository;
    private final RecipeRepository recipeRepository;
    private final EatzUserRepository userRepository;

    /**
     * 레시피를 이용해 사용자의 플랜을 생성 또는 매핑한 후 저장합니다.
     * @param userId 사용자의 ID
     * @param recipeId 레시피의 ID
     * @param scheduledAt 플랜을 등록할 플래너의 날짜
     * @param priority 플랜의 우선 순위
     * @return 플랜 생성 정보를 담은 DTO
     */
    @Transactional(rollbackFor = Exception.class)
    public PlanCreationInfoResponse register(Long userId, Long recipeId, LocalDateTime scheduledAt, Integer priority) {
        planRepository.validateDuplicates(recipeId, userId, scheduledAt);

        Recipe recipe = recipeRepository.getReference(recipeId);
        EatzUser user = userRepository.getReference(userId);
        Plan plan = Plan.create(recipe, user, scheduledAt, priority);
        planRepository.save(plan);
        return new PlanCreationInfoResponse(plan);
    }

    /**
     * 사용자의 플랜을 업데이트합니다.
     * @param id 플랜의 ID
     * @param userId 사용자의 ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void update(Long id, Long userId, LocalDateTime date, Integer priority) {
        userRepository.validateExists(userId);
        Plan plan = planRepository.get(id);
        plan.update(userId, date, priority);
    }

    /**
     * 사용자의 플랜을 삭제합니다.
     * @param id 플랜의 ID
     * @param userId 사용자의 ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id, Long userId) {
        userRepository.validateExists(userId);
        Plan plan = planRepository.get(id);
        plan.validateOwner(userId);
        planRepository.delete(plan);
    }

}
