package imwhs.eatz_server.repository.recipe;

import imwhs.eatz_server.domain.recipe.RecipeCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RecipeCategoryRepository extends JpaRepository<RecipeCategory, Long> {

    @Query("select rc from RecipeCategory rc join fetch rc.category c where rc.recipe.id = :id")
    List<RecipeCategory> findByRecipeIdWithCategory(@Param("id") Long id);

}
