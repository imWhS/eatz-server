package imwhs.eatz_server.repository.recipe;

import imwhs.eatz_server.domain.recipe.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    boolean existsByName(String name);

    Optional<Category> findByName(String name);

}
