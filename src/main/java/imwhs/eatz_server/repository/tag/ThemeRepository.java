package imwhs.eatz_server.repository.tag;

import imwhs.eatz_server.domain.Theme;
import imwhs.eatz_server.exception.ThemeNotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ThemeRepository extends JpaRepository<Theme, Long>, ThemeQueryRepository {

    default Theme get(Long id) {
        if (id == null) { throw new IllegalArgumentException("테마의 ID가 필요해요."); }
        return findById(id).orElseThrow(() -> new ThemeNotFoundException(id));
    }

    Optional<Theme> findByNameIsNullAndDeletedAtIsNull();

}
