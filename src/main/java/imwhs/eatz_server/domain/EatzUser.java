package imwhs.eatz_server.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * EatzUser 엔티티.<br/>
 * <p>
 * 사용자 정보를 저장, 관리하기 위한 클래스입니다.
 */
@Entity
@Table(name = "eatz_user")
@Getter
@Builder
@EqualsAndHashCode(of = "id")
@AllArgsConstructor
@NoArgsConstructor
public class EatzUser {

    @Id @GeneratedValue
    @Column(name = "eatz_user_id")
    private Long id;

    /**
     * 사용자 이름.<br/>
     * <p>
     * TODO: 데이터베이스에 인덱스 추가 고려
     */
    @Column(unique = true, nullable = false)
    private String username;

    /**
     * 이메일 주소.<br/>
     * <p>
     * TODO: 데이터베이스에 인덱스 추가 고려
     */
    @Column(unique = true, nullable = false)
    private String email;

    /**
     * 비밀 번호.<br/>
     * <p>
     * TODO: 보안을 위해 Spring Security를 이용한 필드 값 암호화 처리
     */
    private String password;

    /**
     * 사용자 역할.<br/>
     */
    @Enumerated(EnumType.STRING)
    private Role role;

    /**
     * 사용자가 등록한 레시피 목록.<br/>
     * <p>
     * 사용자와 레시피는 1:N(일대다) 연관 관계를 가질 수 있습니다.
     * <p>
     * TODO: 사용자 회원 탈퇴 시, 연관 관계인 레시피도 함께 삭제되어야 하는지 여부 결정
     */
    @OneToMany(mappedBy = "user")
    private List<Recipe> recipeList = new ArrayList<>();

    /**
     * 사용자 정보 수정.
     * <p>
     * 사용자 정보 중 사용자 이름, 이메일, 비밀 번호를 수정합니다.
     */
    public void update(String username, String email, String password) {
        if (username != null && !username.isEmpty()) this.username = username;
        if (email != null && !email.isEmpty()) this.email = email;
        if (password != null && !password.isEmpty()) this.password = password;
    }

}
