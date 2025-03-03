package imwhs.eatz_server.domain.eatzuser;

/**
 * Role 열거형.<br/>
 * <p>
 * 사용자 역할을 정의합니다.<br/>
 * 사용자는 MEMBER(회원), ADMIN(관리자) 중 하나의 역할을 가질 수 있씁니다.
 */
public enum EatzUserRole {

    /**
     * 회원 권한
     */
    ROLE_MEMBER,

    /**
     * 관리자 권한
     */
    ROLE_ADMIN

}
