package imwhs.eatz_server.repository;

import imwhs.eatz_server.domain.eatzuser.EatzUser;
import imwhs.eatz_server.domain.eatzuser.EatzUserRole;
import imwhs.eatz_server.exception.EatzUserNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * EatzUser 엔티티의 기본적인 조회를 포함한 CRUD 쿼리 작업을 처리하는 Spring Data JPA 리포지토리입니다.
 */
@Repository
public interface EatzUserRepository extends JpaRepository<EatzUser, Long> {

    default EatzUser get(Long id) {
        if (id == null) { throw new IllegalArgumentException("사용자의 ID가 필요해요."); }
        return findByIdAndDeletedAtIsNull(id).orElseThrow(() -> new EatzUserNotFoundException(id));
    }

    default EatzUser getAdmin(Long id) {
        if (id == null) { throw new IllegalArgumentException("사용자의 ID가 필요해요."); }
        return findByIdAndEatzUserRoleAndDeletedAtIsNull(id, EatzUserRole.ROLE_ADMIN).orElseThrow(
                () -> new EatzUserNotFoundException(id, EatzUserRole.ROLE_ADMIN));
    }

    default EatzUser getByUsername(String username) {
        if (username == null) { throw new IllegalArgumentException("사용자의 사용자 이름이 필요해요."); }
        return findByUsernameAndDeletedAtIsNull(username).orElseThrow(() -> new EatzUserNotFoundException(username));
    }

    default EatzUser getByEmail(String email) {
        if (email == null) { throw new IllegalArgumentException("사용자의 이메일 주소가 필요해요."); }
        return findByEmailAndDeletedAtIsNull(email).orElseThrow(() -> new EatzUserNotFoundException(email, true));
    }

    /**
     * 연관 관계 매핑(외래 키 설정)을 위한 proxy 객체를 반환합니다.
     * <p> EatzUser.id 필드만 참조할 수 있기 때문에, 연관 관계로서의 엔티티를 새로 매핑할 때에만 사용해야 합니다. </p>
     * @param id 사용자의 ID
     * @return EatzUser의 proxy 객체
     */
    default EatzUser getReference(Long id) {
        if (id == null) { throw new IllegalArgumentException("사용자의 ID가 필요해요."); }
        return getReferenceById(id);
    }

    default void validateExists(Long id) {
        if (id == null) { throw new IllegalArgumentException("사용자의 ID가 필요해요."); }
        if (!existsByIdAndDeletedAtIsNull(id)) { throw new EatzUserNotFoundException(id); }
    }

    default void validateExistsAsAdmin(Long id) {
        if (id == null) { throw new IllegalArgumentException("사용자의 ID가 필요해요."); }
        if (!existsByIdAndEatzUserRoleAndDeletedAtIsNull(id, EatzUserRole.ROLE_ADMIN)) {
            throw new EatzUserNotFoundException(id); }
    }

    Optional<EatzUser> findByIdAndDeletedAtIsNull(Long id);

    /**
     * 사용자 이름으로 사용자를 조회합니다.
     * @param username 사용자 이름
     * @return Optional로 wrapping된 EatzUser 엔티티
     */
    Optional<EatzUser> findByUsernameAndDeletedAtIsNull(String username);

    /**
     * 이메일 주소로 사용자를 조회합니다.
     * @param email 이메일 주소
     * @return Optional로 wrapping된 EatzUser 엔티티
     */
    Optional<EatzUser> findByEmail(String email);

    /**
     * 이메일 주소로 삭제 처리되지 않은 사용자를 조회합니다.
     * @param email 이메일 주소
     * @return Optional로 wrapping된 EatzUser 엔티티
     */
    Optional<EatzUser> findByEmailAndDeletedAtIsNull(String email);

    Page<EatzUser> findAllByDeletedAtIsNull(Pageable pageable);

    @Query("SELECT u " +
            "FROM EatzUser u " +
            "WHERE " +
            "   u.deletedAt IS NOT null AND " +
            "   u.deletedAt <= :target AND " +
            "   u.email NOT LIKE '%@deleted.invalid'")
    List<EatzUser> findByDeletedAtBeforeAndNotAnonymised(@Param("target") LocalDateTime target);

    /**
     * 사용자 이름에 해당하는 사용자의 존재 여부를 조회합니다.
     * @param username 사용자 이름
     * @return 사용자의 존재 여부
     */
    boolean existsByUsernameAndDeletedAtIsNull(String username);

    /**
     * 이메일 주소에 해당하는 사용자의 존재 여부를 조회합니다.
     * @param email 이메일 주소
     * @return 사용자의 존재 여부
     */
    boolean existsByEmailAndDeletedAtIsNull(String email);

    /**
     * 특정 역할을 가진 사용자를 조회합니다.
     * @param id 사용자의 ID
     * @param role 역할
     * @return 특정 역할을 가진 EatzUser 엔티티
     */
    Optional<EatzUser> findByIdAndEatzUserRoleAndDeletedAtIsNull(Long id, EatzUserRole role);

    /**
     * 사용자의 존재 여부를 확인합니다.
     * @param id 사용자의 ID
     * @return 사용자의 존재 여부
     */
    boolean existsByIdAndDeletedAtIsNull(Long id);

    /**
     * 특정 역할을 가진 사용자의 존재 여부를 확인합니다.
     * @param id 사용자의 ID
     * @param role 역할
     * @return 특정 역할을 가진 사용자의 존재 여부
     */
    boolean existsByIdAndEatzUserRoleAndDeletedAtIsNull(Long id, EatzUserRole role);

}
