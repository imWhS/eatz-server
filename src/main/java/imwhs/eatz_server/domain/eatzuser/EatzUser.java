package imwhs.eatz_server.domain.eatzuser;

import imwhs.eatz_server.common.BaseEntity;
import imwhs.eatz_server.domain.IngredientUser;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * EatzUser 클래스입니다.
 * <ul>
 *     <li>사용자 정보를 저장, 관리하는 엔티티 클래스입니다.</li>
 *     <li>사용자의 기본적인 정보와 사용자가 등록한 레시피 등과 같은 연관 정보를 저장, 관리합니다.</li>
 * </ul>
 */
@Slf4j
@Table(name = "eatz_user")
@Getter
@EqualsAndHashCode(of = "id")
@Entity
public class EatzUser extends BaseEntity {

    /**
     * 엔티티 ID
     * <p>
     *     엔티티 고유 식별자에 해당하는 필수 값입니다.
     * </p>
     */
    @Id @GeneratedValue
    @Column(name = "eatz_user_id")
    private Long id;

    /**
     * 사용자 이름
     *     <ul>
     *         <li>필수 값입니다.</li>
     *         <li>다른 사용자의 사용자 이름과 중복되지 않는, 고유한 값이어야 합니다.</li>
     *     </ul>
     * TODO: 데이터베이스에 인덱스 추가 고려
     */
    @Column(unique = true, nullable = false)
    private String username;

    /**
     * 이메일 주소.
     *     <ul>
     *         <li>필수 값입니다.</li>
     *         <li>다른 사용자의 이메일 주소와 중복되지 않는, 고유한 값이어야 합니다.</li>
     *     </ul>
     * TODO: 데이터베이스에 인덱스 추가 고려
     */
    @Email(message = "유효한 이메일 주소가 아닙니다.")
    @Column(unique = true, nullable = false)
    private String email;

    /**
     * 비밀 번호.
     * <p>
     *     필수 값입니다.
     * </p>
     * TODO: 보안을 위해 서비스 계층에서 Spring Security를 이용한 암호화 된 값을 할당하도록 구현
     */
    private String password;

    /**
     * 사용자 역할.
     * <ul>
     *     <li>필수 값입니다.</li>
     *     <li>기본적으로 회원 역할로 설정됩니다.</li>
     * </ul>
     *
     */
    @Enumerated(EnumType.STRING)
    private EatzUserRole eatzUserRole = EatzUserRole.ROLE_MEMBER;

    /**
     * 사용자 대표(프로필) 이미지 URL.
     */
    private String imageUrl;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<IngredientUser> ingredientUsers = new ArrayList<>();

//    /**
//     * 사용자가 등록한 모든 레시피 목록.
//     * <ul>
//     *     <li>사용자는 레시피와 1:N(One-To-Many) 연관 관계를 가질 수 있습니다.</li>
//     *     <li>EATZ_USER 테이블의 행을 참조하는 외래 키 필드가 RECIPE 테이블에 존재하기에,
//     *     해당 테이블에 매핑될 Recipe를 연관 관계의 주인으로 설정합니다.</li>
//     * </ul>
//     * TODO: 사용자 회원 탈퇴 시, 연관 관계인 레시피도 함께 삭제되어야 하는지 여부 결정
//     */
//    @OneToMany(mappedBy = "user")
//    private List<Recipe> recipes = new ArrayList<>();

    protected EatzUser() {}

    public EatzUser(String username, String email, String password, EatzUserRole eatzUserRole) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.eatzUserRole = eatzUserRole == null ? EatzUserRole.ROLE_MEMBER : eatzUserRole;
    }

    /**
     * 회원 권한을 가지는 EatzUser 객체를 생성합니다.
     * @param username 사용자 이름.
     * @param email 이메일 주소.
     * @param password 비밀 번호.
     * @return 생성된 EatzUser 객체.
     * </p>
     */
    public static EatzUser createMember(
            String username,
            String email,
            String password) { // TODO: 암호화된 password 설정 메서드 별도로 분리
        return new EatzUser(username, email, password, EatzUserRole.ROLE_MEMBER);
    }

    /**
     * 사용자 주요 정보 업데이트 메서드.
     * <ul>
     * <li>사용자의 정보 중, 사용자 이름, 비밀 번호를 변경합니다.</li>
     * <li>변경할 값이 null이거나 비어있는 필드는 기존 값을 유지합니다.</li>
     * </ul>
     * @param username 변경할 사용자 이름
     * @param password 변경할 비밀 번호
     */
    public void update(String username, String password) {
        if (username != null && !username.isEmpty()) this.username = username;
        if (password != null && !password.isEmpty()) {
            log.info("{}의 비밀 번호를 재설정했어요.", username);
            this.password = password;
        } else {
            log.info("{}의 비밀 번호를 유지할게요.", username);
        }
    }

    /**
     * 사용자 대표 이미지 업데이트 메서드.
     * <p>사용자의 대표 이미지를 등록, 수정합니다.</p>
     * @param imageUrl
     */
    public void updateImageUrl(String imageUrl) {
        if (imageUrl != null && !imageUrl.isEmpty()) this.imageUrl = imageUrl;
    }

    /**
     * 사용자 대표 이미지 삭제 메서드.
     */
    public void deleteImageUrl() {
        this.imageUrl = null;
    }

    public void addIngredientUser(IngredientUser ingredientUser) {
        this.ingredientUsers.add(ingredientUser);
    }

    public void clearIngredientUsers() {
        this.ingredientUsers.clear();
    }

}
