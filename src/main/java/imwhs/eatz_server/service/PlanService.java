package imwhs.eatz_server.service;

import imwhs.eatz_server.domain.eatzuser.EatzUser;
import imwhs.eatz_server.domain.recipe.Plan;
import imwhs.eatz_server.domain.recipe.Recipe;
import imwhs.eatz_server.dto.rating.RatingSummaryByRecipeDto;
import imwhs.eatz_server.dto.rating.RatingSummaryDto;
import imwhs.eatz_server.dto.recipe.PlanDto;
import imwhs.eatz_server.exception.*;
import imwhs.eatz_server.repository.eatzuser.EatzUserRepository;
import imwhs.eatz_server.repository.rating.RatingRepository;
import imwhs.eatz_server.repository.recipe.RecipeRepository;
import imwhs.eatz_server.repository.recipe.plan.PlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PlanService {

    private final PlanRepository planRepository;

    private final RecipeRepository recipeRepository;

    private final EatzUserRepository userRepository;

    private final RatingRepository ratingRepository;

    @Transactional
    public Long registerPlan(Long recipeId, String username, LocalDate date, Integer priority) {
        Recipe recipe = findRecipe(recipeId);
        EatzUser user = findUser(username);
        validateDate(date);
        validatePriority(priority);
        validateDuplicatePlan(recipe, user, date);

        Plan plan = Plan.create(recipe, user, date, priority);
        planRepository.save(plan);

        return plan.getId();
    }

    @Transactional
    public void updatePlan(Long planId, String username, LocalDate date, Integer priority) {
        Plan plan = planRepository.findByIdWithEatzUser(planId).orElseThrow(() -> new PlanNotFoundException(planId));

        if (!plan.getUser().getUsername().equals(username)) {
            throw new UnauthorizedAccessException("플랜을 수정할 권한이 없어요.");
        }

        validateDate(date);
        validatePriority(priority);

        plan.update(date, priority);
    }

    @Transactional
    public void deletePlan(Long planId, String username) {
        Plan plan = planRepository.findById(planId).orElseThrow(() -> new PlanNotFoundException(planId));

        if (!plan.getUser().getUsername().equals(username)) {
            throw new UnauthorizedAccessException("플랜을 수정할 권한이 없어요.");
        }

        planRepository.delete(plan);
    }

    public List<PlanDto> findAllByUserAndDateRange(String username, LocalDate startDate, LocalDate endDate) {
        EatzUser user = findUser(username);
        List<PlanDto> plans = planRepository.findAllByUserAndDateRange(user, startDate, endDate);

        // PlanDto에서 레시피 ID 목록 추출 후, 레시피 별 Rating 데이터 조합
        List<Long> recipeIds = plans.stream().map(PlanDto::getRecipeId).toList();
        Map<Long, RatingSummaryDto> ratingSummariesByRecipeIdMap = ratingRepository.findRatingSummariesByRecipeIds(recipeIds).stream()
                .collect(Collectors.toMap(
                        RatingSummaryByRecipeDto::getRecipeId,
                        ratingSummary -> new RatingSummaryDto(ratingSummary.getRatingCount(), ratingSummary.getAverageRatingScore())
                ));

        for (PlanDto plan : plans) {
            plan.setRating(ratingSummariesByRecipeIdMap.get(plan.getRecipeId()));
        }

        return plans;
    }

    private EatzUser findUser(String username) {
        return userRepository.findByUsername(username).orElseThrow(() -> new EatzUserNotFoundException(username));
    }

    private Recipe findRecipe(Long recipeId) {
        return recipeRepository.findById(recipeId).orElseThrow(() -> new RecipeNotFoundException(recipeId));
    }

    private void validatePriority(Integer priority) {
        if (priority == null) return;

        if (priority < 0) {
            throw new IllegalArgumentException("우선 순위는 0 이상의 자연수여야 해요.");
        }
    }

    public void validateDate(LocalDate date) {
        LocalDate now = LocalDate.now();

        if (date.isBefore(now)) {
            throw new IllegalArgumentException("날짜는 현재 이후의 시점이어야 해요.");
        }
    }

    public void validateDuplicatePlan(Recipe recipe, EatzUser user, LocalDate date) {
        if (planRepository.existsByRecipeAndUserAndScheduledAt(recipe, user, date)) {
            throw new DuplicatedPlanException("이미 플래너의 해당 날짜에 등록되어 있어요.");
        }
    }

}
