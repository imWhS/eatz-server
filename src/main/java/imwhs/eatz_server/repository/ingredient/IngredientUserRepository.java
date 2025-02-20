package imwhs.eatz_server.repository.ingredient;

import imwhs.eatz_server.domain.Ingredient;
import imwhs.eatz_server.domain.IngredientUser;
import imwhs.eatz_server.domain.eatzuser.EatzUser;
import imwhs.eatz_server.dto.ingredient.IngredientDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IngredientUserRepository extends JpaRepository<IngredientUser, Long> {

    boolean existsByIngredientAndUser(Ingredient ingredient, EatzUser user);

    @Query("select i.id from IngredientUser iu join iu.ingredient i where iu.user = :user")
    List<Long> findIngredientIdsByUser(@Param("user") EatzUser user);

    @Query("select new imwhs.eatz_server.dto.ingredient.IngredientDto(i.id, i.name) " +
            "from IngredientUser iu " +
            "join iu.ingredient i " +
            "where iu.user = :user")
    List<IngredientDto> findIngredientsByUser(@Param("user") EatzUser user);

    @Modifying
    @Query("delete from IngredientUser iu " +
            "where iu.user = :user and iu.ingredient.id in :ingredientIds")
    void deleteByUserAndIngredientIds(@Param("user") EatzUser user, @Param("ingredientIds") List<Long> ingredientIds);

}
