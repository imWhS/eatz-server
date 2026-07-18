package imwhs.eatz_server.repository.liked;

import imwhs.eatz_server.domain.liked.Liked;
import imwhs.eatz_server.exception.LikedNotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

/**
 * LikedRecipe 엔티티의 기본적인 조회를 포함한 CRUD 쿼리 작업을 처리하는 Spring Data JPA 리포지토리입니다.
 */
@NoRepositoryBean
public interface LikedRepository<T extends Liked> extends JpaRepository<T, Long> {

    default T get(Long id) {
        return findById(id).orElseThrow(() -> new LikedNotFoundException(id));
    }

}
