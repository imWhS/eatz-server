package imwhs.eatz_server.repository.recipe;

import imwhs.eatz_server.domain.recipe.Category;
import imwhs.eatz_server.dto.recipe.CategoryDto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    boolean existsByName(String name);

    Optional<Category> findByName(String name);

    /*
    레시피로 카테고리 조회하기
     */


}
