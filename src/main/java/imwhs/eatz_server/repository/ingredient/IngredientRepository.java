package imwhs.eatz_server.repository.ingredient;

import imwhs.eatz_server.domain.Ingredient;
import imwhs.eatz_server.dto.ingredient.IngredientBasicDto;
import imwhs.eatz_server.exception.IngredientNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Ingredient 엔티티의 기본적인 조회를 포함한 CRUD 쿼리 작업을 처리하는 Spring Data JPA 리포지토리입니다.
 */
@Repository
public interface IngredientRepository extends JpaRepository<Ingredient, Long>, IngredientQueryRepository {

    /**
     * 재료의 ID에 해당하는 Ingredient 엔티티를 조회합니다.
     * <p>
     *     Ingredient 입장에서 1:N 연관 관계로 매핑된 children(하위 재료 목록)은 지연 로딩 설정되어 있습니다.
     *     트랜잭션 내에서 children를 순회하면, children의 원소 수만큼 추가 쿼리가 발생합니다.
     *     해당 재료의 하위 재료 목록까지 필요한 경우 findAllBasicsByParentID 또는 페치 조인 쿼리 메서드 등을 이용해야 합니다.
     * </p>
     * @param id 재료의 ID
     * @return Ingredient 엔티티
     */
    default Ingredient get(Long id) {
        if (id == null) { throw new IllegalArgumentException("재료의 ID가 필요해요."); }
        return findByIdAndDeletedAtIsNull(id).orElseThrow(() -> new IngredientNotFoundException(id));
    }

    default Ingredient get(String name) {
        if (name == null) { throw new IllegalArgumentException("재료의 이름이 필요해요."); }
        return findByNameAndDeletedAtIsNull(name).orElseThrow(() -> new IngredientNotFoundException(name));
    }

    /**
     * 연관관계 매핑(외래 키 설정)을 위한 proxy 객체를 반환합니다.
     * <p> Ingredient.id 필드만 참조할 수 있기 때문에, 연관 관계로서의 엔티티를 새로 매핑할 때에만 사용해야 합니다. </p>
     * @param id 재료의 ID
     * @return Ingredient의 proxy 객체
     */
    default Ingredient getReference(Long id) {
        if (id == null) { throw new IllegalArgumentException("재료의 ID가 필요해요."); }
        return getReferenceByIdAndDeletedAtIsNull(id);
    }

    default Ingredient getWithParent(Long id) {
        if (id == null) { throw new IllegalArgumentException("재료의 ID가 필요해요."); }
        return findWithParentById(id).orElseThrow(() -> new IngredientNotFoundException(id));
    }

    default void validateExists(Long id) {
        if (id == null) { throw new IllegalArgumentException("재료의 ID가 필요해요."); }
        if (!existsByIdAndDeletedAtIsNull(id)) { throw new IngredientNotFoundException(id); }
    }

    /**
     * 이미 해당 이름을 가지고, 해당 상위 재료에 소속되어 있는 재료가 존재하는지 확인합니다.
     * <p> 재료의 중복 여부는 이름 뿐 아니라, 소속되어 있는 상위 재료까지 확인합니다. </p>
     * @param name 재료의 이름
     * @param parentId 재료의 상위 재료
     */
    default void validateDuplicates(String name, Long parentId) {
        if (name == null) throw new IllegalArgumentException("재료의 이름이 필요해요.");
        if (parentId != null && (existsByNameAndParentIdAndDeletedAtIsNull(name, parentId)))
            throw new IllegalArgumentException("동일한 이름을 가지고 있는 재료가 상위 재료에 이미 소속되어 있어요.");
    }

    Optional<Ingredient> findByIdAndDeletedAtIsNull(Long id);

    Optional<Ingredient> findByNameAndDeletedAtIsNull(String name);

    Ingredient getReferenceByIdAndDeletedAtIsNull(Long id);

    boolean existsByIdAndDeletedAtIsNull(Long id);

    Boolean existsByNameAndParentIdAndDeletedAtIsNull(String name, Long parentId);

    /**
     * ID로 Ingredient 엔티티를 조회하고, 연관 관계인 Ingredient.parent, Ingredient.children도 페치 조인으로 함께 조회합니다.<br/>
     * <ul>
     *     <li>Ingredient와 Ingredient.parent 데이터가 함께 로드됩니다.</li>
     *     <li>상위 재료에 속하지 않은 재료도 조회하기 위해 LEFT JOIN을 사용합니다.</li>
     * </ul>
     * @param id 조회할 Ingredient 엔티티의 식별자.
     * @return Optional로 wrapping된 Ingredient 엔티티
     */
    @Query("SELECT DISTINCT i " +
            "FROM Ingredient i " +
            "LEFT JOIN FETCH i.parent c " +
            "LEFT JOIN FETCH i.children ch " +
            "WHERE " +
            "   i.id = :id AND " +
            "   i.deletedAt IS null")
    Optional<Ingredient> findWithParentChildrenById(@Param("id") Long id); // TODO: BATCH vs. Dto projection

    /**
     * ID로 Ingredient 엔티티를 조회하고, 연관 관계인 Ingredient.parent도 페치 조인으로 함께 가져옵니다.
     * @param id 조회할 Ingredient 엔티티의 ID
     * @return Optional로 wrapping된 Ingredient 엔티티. Ingredient.parent도 페치 조인됐습니다.
     */
    @Query("SELECT DISTINCT i FROM Ingredient i " +
            "LEFT JOIN FETCH i.parent c " +
            "WHERE " +
            "   i.id = :id AND " +
            "   i.deletedAt IS null")
    Optional<Ingredient> findWithParentById(@Param("id") Long id);

    @Query(value = 
            "SELECT new imwhs.eatz_server.dto.ingredient.IngredientBasicDto(" +
            "i.id, " +
            "i.name, " +
            "(CASE WHEN count(c.id) > 0 THEN true ELSE false END), " +
            "false, " +
            "false)" +
            "FROM Ingredient i " +
            "LEFT JOIN i.children c ON " +
                "c.deletedAt IS null " +
            "WHERE " +
            "   i.parent IS null AND " +
            "   i.deletedAt IS null " +
            "GROUP BY i.id, i.name",
            countQuery = 
                    "SELECT count(i) " +
                    "FROM Ingredient i " +
                    "WHERE " +
                    "   i.parent IS null AND " +
                    "   i.deletedAt IS null")
    Page<IngredientBasicDto> findRootsOld(Pageable pageable);

    @Query(value =
            "SELECT new imwhs.eatz_server.dto.ingredient.IngredientBasicDto(" +
            "   i.id, " +
            "   i.name, " +
            "   (CASE WHEN count(c.id) > 0 THEN true ELSE false END), " +
            "   (CASE WHEN count(iu.id) > 0 THEN true ELSE false END), " +
            "   (CASE WHEN count(l.id) > 0 THEN true ELSE false END)) " +
            "FROM Ingredient i " +
            "LEFT JOIN i.children c ON " +
                "c.deletedAt IS null " +
            "LEFT JOIN PantryIngredient iu ON " +
                "iu.user.id = :userId AND " +
                "iu.ingredient = i AND " +
                "iu.deletedAt IS null " +
            "LEFT JOIN LikedIngredient l ON " +
                "l.ingredient = i AND " +
                "l.user.id = :userId AND " +
                "l.isLiked = true AND " +
                "l.deletedAt IS null " +
            "WHERE " +
            "   i.parent IS null AND " +
            "   i.deletedAt IS null " +
            "GROUP BY i.id, i.name",
            countQuery =
                    "SELECT count(i) " +
                    "FROM Ingredient i " +
                    "WHERE i.parent IS null AND " +
                    "   i.deletedAt IS null")
    Page<IngredientBasicDto> findRootsForUserOld(@Param("userId") Long userId, Pageable pageable);

    @Query(value = """
    WITH RECURSIVE
        ingredient_tree(
            ingredient_id,
            name,
            category_id,
            created_at,
            updated_at,
            deleted_at,
            deleted_by) AS (
        SELECT 
            i.id,
            i.name,
            i.parent_id,
            i.created_at,
            i.updated_at,
            i.deleted_at,
            i.deleted_by
        FROM ingredient i 
        WHERE 
            i.id = :id AND
            i.deleted_at IS null 
        UNION ALL
        SELECT 
            i.id, i.name, i.parent_id, i.created_at, i.updated_at, i.deleted_at, i.deleted_by
        FROM ingredient i
        JOIN ingredient_tree it ON i.parent_id = it.ingredient_id
        WHERE i.deleted_at IS null
    )
    SELECT * FROM ingredient_tree
    """, nativeQuery = true)
    List<Ingredient> findIngredientTreeOld(@Param("id") Long id);

    /**
     * 특정 재료를 기준점으로 삼아, 기준 재료부터 최하위 재료(leaf node)까지의 계층 구조에 포함된 모든 재료의 Ingredient 엔티티 목록을 조회합니다.
     * @param id 계층 구조의 기준점이 될 기준 재료의 ID
     * @return 기준 재료부터 최하위 재료에 속하는 모든 Ingredient 엔티티 목록. 기준점이 되는(ID에 해당하는) 재료가 먼저 삽입되어 있습니다.
     */
    @Query(value = """
        WITH RECURSIVE IngredientTree AS (
            SELECT 
                i.id,
                i.name,
                i.parent_id,
                i.created_at,
                i.updated_at,
                i.deleted_at,
                i.deleted_by
            FROM Ingredient i
            WHERE 
                i.id = :id AND 
                i.deleted_at IS null
            UNION ALL
            SELECT
                ri.id,
                ri.name,
                ri.parent_id,
                ri.created_at,
                ri.updated_at,
                ri.deleted_at,
                ri.deleted_by
            FROM Ingredient ri
            INNER JOIN IngredientTree it ON it.id = ri.parent_id
            WHERE ri.deleted_at IS null
        )
        SELECT * FROM IngredientTree
    """, nativeQuery = true)
    List<Ingredient> findAllAsHierarchy(@Param("id") Long id);


    // TODO: N + 1 이슈
    @Query("SELECT i " +
            "FROM Ingredient i " +
            "LEFT JOIN FETCH i.children " +
            "WHERE i.name = :name")
    Optional<Ingredient> findByName(@Param("name") String name);

    @Query(value =
            "SELECT new imwhs.eatz_server.dto.ingredient.IngredientBasicDto(" +
            "   i.id, " +
            "   i.name, " +
            "   (CASE WHEN count(c.id) > 0 THEN true ELSE false END), " +
            "   (CASE WHEN count(iu.id) > 0 THEN true ELSE false END), " +
            "   (CASE WHEN count(l.id) > 0 THEN true ELSE false END)) " +
            "FROM Ingredient i " +
            "LEFT JOIN i.children c ON " +
                "c.deletedAt IS null " +
            "LEFT JOIN PantryIngredient iu ON " +
            "   iu.ingredient = i AND " +
            "   iu.user.id = :userId AND " +
            "   iu.deletedAt IS null " +
            "LEFT JOIN LikedIngredient l ON " +
            "   l.ingredient = i AND " +
            "   l.user.id = :userId AND " +
            "   l.isLiked = true AND " +
            "   l.deletedAt IS null " +
            "WHERE " +
            "   replace(lower(i.name), ' ', '') LIKE lower(concat('%', :keyword, '%')) AND " +
            "   i.deletedAt IS null " +
            "GROUP BY i.id, i.name ",
            countQuery =
                    "SELECT count(i) " +
                    "FROM Ingredient i " +
                    "WHERE " +
                        "replace(lower(i.name), ' ', '') LIKE lower(concat('%', :keyword, '%')) AND " +
                        "i.deletedAt IS null ")
    Page<IngredientBasicDto> searchIngredientItemsForUser(
            @Param("keyword") String keyword,
            @Param("userId") Long userId,
            Pageable pageable);

    @Query(value =
            "SELECT new imwhs.eatz_server.dto.ingredient.IngredientBasicDto(" +
            "   i.id, " +
            "   i.name, " +
            "   (CASE WHEN count(c.id) > 0 THEN true ELSE false END), " +
            "   false, " +
            "   false) " +
            "FROM Ingredient i " +
            "LEFT JOIN i.children c ON " +
                "c.deletedAt IS null " +
            "WHERE " +
            "   replace(lower(i.name), ' ', '') LIKE lower(concat('%', :keyword, '%')) AND " +
            "   i.deletedAt IS null " +
            "GROUP BY i.id, i.name",
            countQuery =
                    "SELECT count(i) " +
                    "FROM Ingredient i " +
                    "WHERE " +
                        "replace(lower(i.name), ' ', '') LIKE lower(concat('%', :keyword, '%')) AND " +
                        "i.deletedAt IS null ")
    Page<IngredientBasicDto> searchIngredientItems(@Param("keyword") String keyword, Pageable pageable);

}
