package imwhs.eatz_server.domain.eatzuser;

import imwhs.eatz_server.common.BaseEntity;
import imwhs.eatz_server.common.util.EmailUtil;
import imwhs.eatz_server.exception.UnauthorizedAccessException;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.UUID;
import java.util.regex.Pattern;

/**
 * 서비스에 가입한 사용자의 정보를 정의하는 EatzUser 엔티티입니다.
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@Table(name = "eatz_user", uniqueConstraints = {
        @UniqueConstraint(name = "uk_username", columnNames = {"username"}),
        @UniqueConstraint(name = "uk_email", columnNames = {"email"})
})
@Entity
public class EatzUser extends BaseEntity {

    @EqualsAndHashCode.Include
    @Column(name = "eatz_user_id")
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 사용자 이름
     * <ul>
     *     <li> 필수 항목입니다. </li>
     * </ul>
     * TODO: 데이터베이스에 인덱스 추가 고려
     */
    @NotNull
    @Column(length = 20, nullable = false, unique = true)
    @Size(min = 4, max = 20)
    private String username;

    /**
     * 이메일 주소
     * <ul>
     *     <li> 필수 항목입니다. </li>
     * </ul>
     * TODO: 데이터베이스에 인덱스 추가 고려
     */
    @NotNull
    @Email(message = "유효한 이메일 주소가 아니에요.")
    @Column(nullable = false, unique = true)
    private String email;

    /**
     * 인코딩 된 암호
     * <ul>
     *     <li> 필수 항목입니다. </li>
     * </ul>
     */
    @NotNull
    @Column(nullable = false)
    @Size(min = 8, max = 64)
    private String password;

    /**
     * 역할
     * <ul>
     *     <li> 필수 항목입니다. </li>
     *     <li> 회원 역할로 기본 설정됩니다. </li>
     * </ul>
     *
     */
    @Enumerated(EnumType.STRING)
    private EatzUserRole eatzUserRole = EatzUserRole.ROLE_MEMBER;

    /**
     * 대표(프로필) 이미지 URL
     */
    @Setter
    private String imageUrl;

    /**
     * 소개
     */
    @Size(max = 200)
    private String bio;

    /**
     * 외부 공개 ID(UUID)
     * <ul>
     *     <li> 필수 항목입니다. </li>
     *     <li> HTTP 통신 시 데이터베이스 내부에서 사용하는 사용자의 ID 노출을 방지하고, 외부(클라이언트)에서 사용자를 식별하기 위해 사용합니다. </li>
     * </ul>
     * TODO: 데이터베이스에 인덱스 추가 고려
     */
    @Column(nullable = false, unique = true, updatable = false, length = 36)
    private String publicId = UUID.randomUUID().toString();

    private EatzUser(String username, String email, String password, EatzUserRole eatzUserRole) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.eatzUserRole = eatzUserRole == null ? EatzUserRole.ROLE_MEMBER : eatzUserRole;
    }

    /**
     * 사용자 이름을 업데이트합니다.
     * @param requester 요청한 사용자
     * @param username 사용자 이름
     */
    public void updateUsername(EatzUser requester, String username) {
        validateUpdatableByUser(requester);
        validateUsername(username);
        this.username = username;
    }

    /**
     * 인코딩 된 암호를 업데이트합니다.
     * @param requester 요청한 사용자
     * @param password 인코딩 된 암호
     */
    public void updatePassword(EatzUser requester, String password) {
        validateUpdatableByUser(requester);
        validatePassword(password);
        this.password = password;
    }

    /**
     * 대표 이미지 URL을 업데이트합니다.
     * @param requester 요청한 사용자
     * @param imageUrl 대표 이미지 URL
     */
    public void updateImageUrl(EatzUser requester, String imageUrl) {
        validateUpdatableByUser(requester);
        validateImageUrl(imageUrl);
        this.imageUrl = imageUrl;
    }

    /**
     * 대표 이미지를 삭제합니다.
     * @param requester 요청한 사용자
     */
    public void deleteImageUrl(EatzUser requester) {
        validateUpdatableByUser(requester);
        this.imageUrl = null;
    }

    /**
     * 소개를 업데이트합니다.
     * @param requester 요청한 사용자
     * @param bio 소개
     */
    public void updateBio(EatzUser requester, String bio) {
        validateUpdatableByUser(requester);
        validateBio(bio);
        this.bio = bio;
    }

    /**
     * 소개를 삭제합니다.
     * @param requester 요청한 사용자
     */
    public void deleteBio(EatzUser requester) {
        validateUpdatableByUser(requester);
        this.bio = null;
    }

    public void updateEmail(String email) {
        EmailUtil.validateEmail(email);
        this.email = email;
    }

    public boolean isAdmin() {
        return EatzUserRole.ROLE_ADMIN.equals(eatzUserRole);
    }

    /**
     * 사용자를 제어할 수 있는 권한의 소유 여부를 확인합니다.
     * @param user 사용자의 EatzUser 엔티티
     * @return 사용자를 제어할 수 있는 권한의 소유 여부
     */
    private boolean hasControlPermission(EatzUser user) {
        boolean isAdmin = user.isAdmin();
        boolean isSelf = this.id.equals(user.getId());

        return isAdmin || isSelf;
    }

    public static void validateUsername(String username) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("필수 항목인 사용자 이름이 비어 있어요.");
        }

        if (username.length() < 4 || 20 < username.length()) {
            throw new IllegalArgumentException("사용자 이름은 최소 4자부터 최대 20자까지의 길이로 사용할 수 있어요.");
        }
    }

    public static void validatePassword(String password) {
        if (password == null || password.isBlank()) { throw new IllegalArgumentException("필수 항목인 암호가 비어 있어요."); }
    }

    public static void validateImageUrl(String imageUrl) {
        if (imageUrl == null || imageUrl.isBlank()) { throw new IllegalArgumentException("이미지가 비어 있어요."); }
    }

    public static void validateBio(String bio) {
        if (bio == null || bio.isBlank()) { throw new IllegalArgumentException("소개가 비어 있어요."); }
        if (200 < bio.length()) { throw new IllegalArgumentException("소개는 최대 200자까지의 길이로 사용할 수 있어요."); }
    }

    /**
     * 특정 사용자에 대해 해당 EatzUser 엔티티의 업데이트 가능 여부를 검증합니다.
     * @param user 사용자의 EatzUser 엔티티
     */
    public void validateUpdatableByUser(EatzUser user) {
        if (!hasControlPermission(user)) {
            throw new UnauthorizedAccessException("사용자를 업데이트할 권한이 없어요.");
        }
    }

    /**
     * 특정 사용자에 대해 해당 EatzUser 엔티티의 삭제 가능 여부를 검증합니다.
     * @param user 사용자의 EatzUser 엔티티
     */
    public void validateDeletableByUser(EatzUser user) {
        if (!hasControlPermission(user)) {
            throw new UnauthorizedAccessException("사용자를 삭제할 권한이 없어요.");
        }
    }

    public static void validateRawPassword(String rawPassword) {
        if (rawPassword == null || rawPassword.isBlank()) {
            throw new IllegalArgumentException("암호가 비어있어요.");
        }
        if (rawPassword.length() < 8 || 64 < rawPassword.length()) {
            throw new IllegalArgumentException("암호는 최소 8자부터 최대 64자 사이의 길이여야 해요.");
        }
    }

    /**
     * 회원 역할을 가지는 EatzUser 엔티티 팩토리 메서드
     * @param username 사용자 이름
     * @param email 이메일 주소
     * @param password 인코딩 된 암호
     * @return 회원 역할을 가지는 EatzUser 엔티티
     */
    public static EatzUser createMember(
            String username,
            String email,
            String password) {
        validateUsername(username);
        EmailUtil.validateEmail(email);
        validatePassword(password);
        return new EatzUser(username, email, password, EatzUserRole.ROLE_MEMBER);
    }

    /**
     * 회원 역할을 가지는 EatzUser 엔티티 팩토리 메서드
     * @param username 사용자 이름
     * @param email 이메일 주소
     * @param password 인코딩 된 암호
     * @return 회원 역할을 가지는 EatzUser 엔티티
     */
    public static EatzUser createMember(
            String username,
            String email,
            String password,
            String imageUrl) {
        validateUsername(username);
        EmailUtil.validateEmail(email);
        validatePassword(password);
        validateImageUrl(imageUrl);
        EatzUser user = new EatzUser(username, email, password, EatzUserRole.ROLE_MEMBER);
        user.setImageUrl(imageUrl);
        return user;
    }

    /**
     * 권리자 역할을 가지는 EatzUser 엔티티 팩토리 메서드
     * @param username 사용자 이름
     * @param email 이메일 주소
     * @param password 인코딩 된 암호
     * @return 권리자 역할을 가지는 EatzUser 엔티티
     */
    public static EatzUser createAdmin(
            String username,
            String email,
            String password) {
        validateUsername(username);
        EmailUtil.validateEmail(email);
        validatePassword(password);
        return new EatzUser(username, email, password, EatzUserRole.ROLE_ADMIN);
    }

    /**
     * 권리자 역할을 가지는 EatzUser 엔티티 팩토리 메서드
     * @param username 사용자 이름
     * @param email 이메일 주소
     * @param password 인코딩 된 암호
     * @return 권리자 역할을 가지는 EatzUser 엔티티
     */
    public static EatzUser createAdmin(
            String username,
            String email,
            String password,
            String imageUrl) {
        validateUsername(username);
        EmailUtil.validateEmail(email);
        validatePassword(password);
        validateImageUrl(imageUrl);
        EatzUser user = new EatzUser(username, email, password, EatzUserRole.ROLE_ADMIN);
        user.setImageUrl(imageUrl);
        return user;
    }
}
