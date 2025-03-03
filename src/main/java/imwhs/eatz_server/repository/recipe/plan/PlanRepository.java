package imwhs.eatz_server.repository.recipe.plan;

import imwhs.eatz_server.domain.eatzuser.EatzUser;
import imwhs.eatz_server.domain.recipe.Plan;
import imwhs.eatz_server.domain.recipe.Recipe;
import imwhs.eatz_server.dto.plan.ChecklistItemDto;
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
        select new imwhs.eatz_server.dto.plan.ChecklistItemDto(
            new imwhs.eatz_server.dto.recipe.RecipeBasicDto(p.recipe.id, p.recipe.title, p.recipe.imageUrl),
            new imwhs.eatz_server.dto.ingredient.IngredientDto(ir.ingredient.id, ir.ingredient.name),
            case when iu.id is null then true else false end)
        from Plan p
        join p.recipe r
        join IngredientRecipe ir on ir.recipe = r
        left join IngredientUser iu on iu.ingredient = ir.ingredient and iu.user = :user
        where p.user = :user and p.scheduledAt between :startDate and :endDate
    """)
    List<ChecklistItemDto> findChecklistByUserAndDateRangeV2(@Param("user") EatzUser user,
                                                             @Param("startDate") LocalDate startDate,
                                                             @Param("endDate") LocalDate endDate);

}
