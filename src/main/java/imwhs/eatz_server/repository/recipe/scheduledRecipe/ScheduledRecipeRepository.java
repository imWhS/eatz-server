package imwhs.eatz_server.repository.recipe.scheduledRecipe;

import imwhs.eatz_server.domain.recipe.NScheduledRecipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScheduledRecipeRepository extends JpaRepository<NScheduledRecipe, Long> {



}
