package imwhs.eatz_server.repository.recipe.savedrecipe;

import com.querydsl.core.group.GroupBy;
import com.querydsl.jpa.impl.JPAQueryFactory;
import imwhs.eatz_server.domain.eatzuser.EatzUser;
import imwhs.eatz_server.domain.recipe.*;
import imwhs.eatz_server.dto.recipe.RecipeBasicDto;
import imwhs.eatz_server.dto.recipe.savedrecipe.SavedRecipeDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * SavedRecipeQueryRepository 클래스입니다.<br/>
 * SavedRecipe 엔티티 관련 리포지토리 쿼리 메서드들을 구현합니다.
 */
@RequiredArgsConstructor
@Repository
public class SavedRecipeCustomRepositoryImpl implements SavedRecipeCustomRepository {

    private final JPAQueryFactory queryFactory;

    /**
     * 사용자의 모든 저장된 레시피를 조회합니다.
     * <p>
     *     저장된 레시피 별로 지정된 일정 정보도 함께 조회합니다.
     *     사용자가 레시피 저장 시 일정을 지정하는 경우가 많은 상황에 맞춰 배치 쿼리를 활용합니다.</p>
     * @param user 사용자 엔티티 인스턴스.
     * @param pageable 페이징 정보.
     * @return 저장된 레시피 목록 관련 정보가 포함된 SavedRecipeDto 목록 인스턴스.
     */
    @Override
    public List<SavedRecipeDto> findByUser(EatzUser user, Pageable pageable) {
        QSavedRecipe savedRecipe = QSavedRecipe.savedRecipe;
        QSavedRecipeSchedule savedRecipeSchedule = QSavedRecipeSchedule.savedRecipeSchedule;

        List<SavedRecipe> savedRecipes = queryFactory
                .selectFrom(savedRecipe)
                .innerJoin(savedRecipe.recipe).fetchJoin()
                .innerJoin(savedRecipe.user).fetchJoin()
                .where(savedRecipe.user.id.eq(user.getId()))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        if (savedRecipes.isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> savedRecipeIds = savedRecipes.stream()
                .map(SavedRecipe::getId).toList();

        Map<Long, List<LocalDate>> dates = queryFactory
                .from(savedRecipeSchedule)
                .where(savedRecipeSchedule.savedRecipe.id.in(savedRecipeIds))
                .transform(GroupBy.groupBy(savedRecipeSchedule.savedRecipe.id).as(
                        GroupBy.list(savedRecipeSchedule.date)
                ));

        List<SavedRecipeDto> savedRecipeDtos = savedRecipes.stream().map(sr -> {
            Recipe recipe = sr.getRecipe();
            return new SavedRecipeDto(
                    sr.getId(),
                    sr.getUser().getId(),
                    sr.getCreatedAt(),
                    new RecipeBasicDto(recipe.getId(), recipe.getTitle(), recipe.getImageUrl()),
                    dates.getOrDefault(sr.getId(), Collections.emptyList())
            );
        }).toList();

        return savedRecipeDtos;
    }

}
