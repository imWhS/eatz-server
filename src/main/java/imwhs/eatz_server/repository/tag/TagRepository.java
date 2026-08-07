package imwhs.eatz_server.repository.tag;

import imwhs.eatz_server.domain.Tag;
import imwhs.eatz_server.exception.TagNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface TagRepository extends JpaRepository<Tag, Long> {

    default Tag get(Long id) {
        if (id == null) { throw new IllegalArgumentException("태그의 ID가 필요해요."); }
        return findByIdAndDeletedAtIsNull(id).orElseThrow(() -> new TagNotFoundException(id));
    }

    default void validateDuplicatesByName(String name) {
        if (name == null) { throw new IllegalArgumentException("태그의 이름이 필요해요."); }
        if (existsByNameAndDeletedAtIsNull(name)) {
            throw new IllegalArgumentException("이미 '" + name + "' 이름을 가진 태그가 있어요.");
        }
    }

    Optional<Tag> findByIdAndDeletedAtIsNull(Long id);

    boolean existsByNameAndDeletedAtIsNull(String name);

    List<Tag> findAllByIdInAndDeletedAtIsNull(Set<Long> ids);

    @Query("SELECT t " +
            "FROM Tag t " +
            "WHERE " +
            "   t.name IN :names AND " +
            "   t.deletedAt IS null")
    List<Tag> findAllByNameIn(@Param("names") List<String> names);

    @Query("SELECT t " +
            "FROM Tag t " +
            "WHERE " +
            "   replace(t.name, ' ', '') LIKE %:name% AND   " +
            "   t.deletedAt IS null ")
    Page<Tag> searchByName(@Param("name") String name, Pageable pageable);

    Optional<Tag> findByNameAndDeletedAtIsNull(String name);

    Optional<Tag> findFirstByNameAndDeletedAtIsNull(String name);

}
