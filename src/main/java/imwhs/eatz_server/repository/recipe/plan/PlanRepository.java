package imwhs.eatz_server.repository.recipe.plan;

import imwhs.eatz_server.domain.eatzuser.EatzUser;
import imwhs.eatz_server.domain.recipe.Plan;
import imwhs.eatz_server.domain.recipe.Recipe;
import imwhs.eatz_server.dto.plan.ChecklistItemResponseDto;
import imwhs.eatz_server.dto.recipe.PlanDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PlanRepository extends JpaRepository<Plan, Long>, PlanCustomRepository {

    @Query("select p from Plan p join fetch p.user where p.id = :id")
    Optional<Plan> findByIdWithEatzUser(@Param("id") Long id);

    boolean existsByRecipeAndUserAndScheduledAt(Recipe recipe, EatzUser user, LocalDate date);

    @Query("select new imwhs.eatz_server.dto.recipe.PlanDto(p.id, r.id, u.id, r.title, r.imageUrl, p.scheduledAt)" +
            "from Plan p " +
            "join p.user u " +
            "join p.recipe r " +
            "where p.user = :user and p.scheduledAt between :startDate and :endDate " +
            "order by p.priority DESC, p.scheduledAt ASC")
    List<PlanDto> findAllByUserAndDateRange(
            @Param("user") EatzUser user,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query("select p.recipe.id " +
            "from Plan p " +
            "where p.user = :user and p.scheduledAt between :startDate and :endDate")
    List<Long> findRecipeIdsByUserAndDateRange(
            @Param("user") EatzUser user,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query("""
    select new imwhs.eatz_server.dto.plan.ChecklistItemResponseDto(
        new imwhs.eatz_server.dto.recipe.RecipeBasicDto(
            ir.recipe.id,
            ir.recipe.title,
            ir.recipe.imageUrl),
        new imwhs.eatz_server.dto.ingredient.IngredientDto(
            ir.ingredient.id,
            ir.ingredient.name),
        case when iu.id is null then true else false end
    )
    from IngredientRecipe ir
    left join IngredientUser iu on ir.ingredient = iu.ingredient and iu.user = :user
    where ir.recipe.id in (
        select p.recipe.id from Plan p
        where p.user = :user
        and p.scheduledAt between :startDate and :endDate
    )
    """)
    List<ChecklistItemResponseDto> findChecklistByUserAndDateRange(@Param("user") EatzUser user,
                                                                   @Param("startDate") LocalDate startDate,
                                                                   @Param("endDate") LocalDate endDate);


    /*
    특정 사용자, 일정의 '플랜에 추가된 레시피' 별 요리 가능 여부


    PLAN A.
    1. 사용자 ID, 일정에 대한 Plan 목록 조회 - Plan 목록에서 사용자가 요리하려는 레시피 ID 목록을 가져올 수 있음.
    2. 사용자가 요리하려는 레시피 ID 목록을 이용해 재료 조회 -  사용자가 요리하려는 레시피들이 필요로 하는 재료 목록을 가져올 수 있음.
    3.
     */

    /*
    select new imwhs.eatz_server.dto.plan.ChecklistItemResponseDto(
        new imwhs.eatz_server.dto.recipe.RecipeBasicDto(
            ir.recipe.id,
            ir.recipe.title,
            ir.recipe.imageUrl),
        new imwhs.eatz_server.dto.ingredient.IngredientDto(
            ir.ingredient.id,
            ir.ingredient.name),
        case when iu.id is null then true else false end
    )
     */

    @Query("""
        select new imwhs.eatz_server.dto.plan.ChecklistItemResponseDto(
            new imwhs.eatz_server.dto.recipe.RecipeBasicDto(p.recipe.id, p.recipe.title, p.recipe.imageUrl),
            new imwhs.eatz_server.dto.ingredient.IngredientDto(ir.ingredient.id, ir.ingredient.name),
            case when iu.id is null then true else false end)
        from Plan p
        join p.recipe r
        join IngredientRecipe ir on ir.recipe = r
        left join IngredientUser iu on iu.ingredient = ir.ingredient and iu.user = :user
        where p.user = :user and p.scheduledAt between :startDate and :endDate
    """)
    List<ChecklistItemResponseDto> findChecklistByUserAndDateRangeV2(@Param("user") EatzUser user,
                                                                     @Param("startDate") LocalDate startDate,
                                                                     @Param("endDate") LocalDate endDate);

}
