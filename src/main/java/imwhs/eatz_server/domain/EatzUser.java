package imwhs.eatz_server.domain;

import jakarta.persistence.*;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * EatzUser 엔티티입니다.<br/>
 * 사용자 정보를 담고 있습니다.
 */
@Entity
@Table(name = "eatz_user")
@Getter
public class EatzUser {

    @Id @GeneratedValue
    @Column(name = "eatz_user_id")
    private Long id;

    // TODO: 데이터베이스에 인덱스 추가 고려
    @Column(unique = true, nullable = false)
    private String username;

    // TODO: 데이터베이스에 인덱스 추가 고려
    @Column(unique = true, nullable = false)
    private String email;

    // TODO: 보안을 위한 필드 값 암호화 처리 - Spring Security
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    // TODO: 사용자 회원 탈퇴 시, 연관 관계인 레시피도 함께 삭제되어야 하는지 여부 결정
    @OneToMany(mappedBy = "user")
    private List<Recipe> recipeList = new ArrayList<>();

}
