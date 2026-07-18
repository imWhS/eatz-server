package imwhs.eatz_server.repository.pantry;

import imwhs.eatz_server.domain.eatzuser.EatzUser;
import imwhs.eatz_server.domain.pantry.PantryKitchenware;
import imwhs.eatz_server.domain.Kitchenware;
import imwhs.eatz_server.dto.kitchenware.KitchenwareBasicDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

/**
 * PantryKitchenware 엔티티의 기본적인 조회를 포함한 CRUD 쿼리 작업을 처리하는 Spring Data JPA 리포지토리입니다.
 */
@Repository
public interface PantryKitchenwareRepository extends JpaRepository<PantryKitchenware, Long> {

    boolean existsByKitchenwareAndUser(Kitchenware kitchenware, EatzUser user);

    /**
     * 사용자가 보관함에 추가한 도구의 ID 목록을 조회합니다.
     * @param user 사용자의 EatzUser 엔티티
     * @return 사용자가 보관함에 추가한 도구의 ID 목록
     */
    @Query("SELECT k.id " +
            "FROM PantryKitchenware pk " +
            "JOIN pk.kitchenware k ON k.deletedAt IS null " +
            "WHERE " +
            "   pk.user = :user AND " +
            "   pk.deletedAt IS null")
    List<Long> findAllKitchenwareIdsByUser(@Param("user") EatzUser user);

    /**
     * ID에 해당하는 사용자가 보관함에 추가한 도구의 ID 집합을 조회합니다.
     * @param id 사용자의 ID
     * @return 사용자가 보관함에 추가한 도구의 ID 집합
     */
    @Query("SELECT k.id " +
            "FROM PantryKitchenware pk " +
            "JOIN pk.kitchenware k ON k.deletedAt IS null " +
            "WHERE " +
            "   pk.user.id = :id AND " +
            "   pk.deletedAt IS null")
    Set<Long> findAllKitchenwareIdsByUserId(@Param("id") Long id);

    @Query("SELECT k.id " +
            "FROM PantryKitchenware pk " +
            "JOIN pk.kitchenware k ON k.deletedAt IS null " +
            "WHERE " +
            "   pk.kitchenware.id IN :kitchenwareIds AND" +
            "   pk.deletedAt IS null AND " +
            "   pk.user.id = :id")
    Set<Long> findExistingKitchenwareIdsByUserId(@Param("id") Long id, @Param("kitchenwareIds") List<Long> kitchenwareIds);

    /**
     * 사용자가 보관함에 추가한 모든 도구의 기본 정보 목록을 조회합니다.
     * @param user 사용자의 EatzUser 엔티티
     * @param pageable 페이징 정보
     * @return 모든 도구의 기본 정보 목록과 페이징 정보
     */
    @Query("SELECT new imwhs.eatz_server.dto.kitchenware.KitchenwareBasicDto(" +
            "   k.id, " +
            "   k.name, " +
            "   k.imageUrl, " +
            "   true) " +
            "FROM PantryKitchenware pk " +
            "JOIN pk.kitchenware k ON k.deletedAt IS null " +
            "WHERE " +
            "   pk.user = :user AND " +
            "   pk.deletedAt IS null")
    Page<KitchenwareBasicDto> findAllByUser(@Param("user") EatzUser user, Pageable pageable);

    /**
     * 특정 도구들을 사용자의 보관함에서 제거합니다.
     * @param user 사용자의 EatzUser 엔티티
     * @param kitchenwareIds 사용자의 보관함에서 제거할 도구 ID 목록
     */
    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM PantryKitchenware pk " +
            "WHERE " +
            "   pk.user = :user AND " +
            "   pk.kitchenware.id in :kitchenwareIds")
    void deleteByUserAndKitchenwareIds(
            @Param("user") EatzUser user, @Param("kitchenwareIds") List<Long> kitchenwareIds);

    /**
     * 사용자가 보관함에 추가한 도구의 수를 집계합니다.
     * @param user 사용자의 EatzUser 엔티티
     * @return 사용자가 보관함에 추가한 도구의 수
     */
    @Query("SELECT count(pk) " +
            "FROM PantryKitchenware pk " +
            "WHERE " +
            "   pk.user = :user AND " +
            "   pk.deletedAt IS null ")
    long countKitchenwareUserByUser(@Param("user") EatzUser user);

    /**
     * 사용자의 보관함에서 모든 도구를 제거합니다.
     * @param user 사용자의 EatzUser 엔티티
     */
    @Modifying
    @Query("DELETE FROM PantryKitchenware pk " +
            "WHERE pk.user = :user")
    void deleteAllByUser(EatzUser user);

}
