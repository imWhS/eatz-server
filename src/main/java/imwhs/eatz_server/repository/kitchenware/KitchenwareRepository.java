package imwhs.eatz_server.repository.kitchenware;

import imwhs.eatz_server.domain.Ingredient;
import imwhs.eatz_server.domain.Kitchenware;
import imwhs.eatz_server.dto.kitchenware.KitchenwareBasicDto;
import imwhs.eatz_server.exception.KitchenwareNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Kitchenware 엔티티의 기본적인 조회를 포함한 CRUD 쿼리 작업을 처리하는 Spring Data JPA 리포지토리입니다.
 */
@Repository
public interface KitchenwareRepository extends JpaRepository<Kitchenware, Long>, KitchenwareQueryRepository {

    default Kitchenware get(Long id) {
        if (id == null) { throw new IllegalArgumentException("도구의 ID가 필요해요."); }
        return findByIdAndDeletedAtIsNull(id).orElseThrow(() -> new KitchenwareNotFoundException(id));
    }

    default Kitchenware get(String name) {
        if (name == null || name.isBlank()) { throw new IllegalArgumentException("도구의 이름이 필요해요."); }
        return findByNameAndDeletedAtIsNull(name).orElseThrow(() -> new KitchenwareNotFoundException(name));
    }

    /**
     * 연관관계 매핑(외래 키 설정)을 위한 proxy 객체를 반환합니다.
     * <p> Kitchenware.id 필드만 참조할 수 있기 때문에, 연관 관계로서의 엔티티를 새로 매핑할 때에만 사용해야 합니다. </p>
     * @param id 재료의 ID
     * @return Ingredient의 proxy 객체
     */
    default Kitchenware getReference(Long id) {
        if (id == null) { throw new IllegalArgumentException("도구의 ID가 필요해요."); }
        return getReferenceByIdAndDeletedAtIsNull(id);
    }

    default void validateExists(Long id) {
        if (id == null) { throw new IllegalArgumentException("도구의 ID가 필요해요."); }
        if (!existsByIdAndDeletedAtIsNull(id)) { throw new KitchenwareNotFoundException(id); }
    }

    default void validateDuplicates(String name) {
        if (name == null) throw new IllegalArgumentException("도구의 이름이 필요해요.");
        if (existsByNameAndDeletedAtIsNull(name)) {
            throw new IllegalArgumentException("이미 동일한 이름을 가지고 있는 도구가 있어요.");
        }
    }

    Optional<Kitchenware> findByIdAndDeletedAtIsNull(Long id);

    Optional<Kitchenware> findByNameAndDeletedAtIsNull(String name);

    Kitchenware getReferenceByIdAndDeletedAtIsNull(Long id);

    boolean existsByIdAndDeletedAtIsNull(Long id);

    Boolean existsByNameAndDeletedAtIsNull(String name);

    @Query("SELECT k " +
            "FROM Kitchenware k " +
            "WHERE " +
            "   replace(lower(k.name), ' ', '') LIKE lower(concat('%', :keyword, '%')) AND " +
            "   k.deletedAt IS null")
    Page<Kitchenware> searchKitchenwares(@Param("keyword") String keyword, Pageable pageable);

    @Query(value =
            "SELECT new imwhs.eatz_server.dto.kitchenware.KitchenwareBasicDto(" +
            "   k.id, " +
            "   k.name, " +
            "   k.imageUrl, " +
            "   false) " +
            "FROM Kitchenware k " +
            "WHERE k.deletedAt IS null",
            countQuery =
                    "SELECT count(k) " +
                    "FROM Kitchenware k " +
                    "WHERE k.deletedAt IS null"
    )
    Page<KitchenwareBasicDto> findAllForGuest(Pageable pageable);

    @Query(value =
            "SELECT new imwhs.eatz_server.dto.kitchenware.KitchenwareBasicDto(" +
            "k.id, " +
            "k.name, " +
            "k.imageUrl, " +
            "(" +
                "SELECT count(ku.id) > 0 " +
                "FROM PantryKitchenware ku " +
                "WHERE " +
                "   ku.user.id = :userId AND " +
                "   ku.kitchenware = k)) " +
            "FROM Kitchenware k " +
            "WHERE k.deletedAt IS null",
            countQuery =
                    "SELECT count(k) " +
                    "FROM Kitchenware k " +
                    "WHERE k.deletedAt IS null"
    )
    Page<KitchenwareBasicDto> findAllForUser(@Param("userId") Long userId, Pageable pageable);

}
