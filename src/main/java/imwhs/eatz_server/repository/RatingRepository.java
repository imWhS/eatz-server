package imwhs.eatz_server.repository;

import imwhs.eatz_server.domain.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {

    boolean existsByRecipeIdAndUserId(Long recipeId, Long userId);

    Optional<Rating> findByIdAndDeletedAtIsNull(Long id);

}
