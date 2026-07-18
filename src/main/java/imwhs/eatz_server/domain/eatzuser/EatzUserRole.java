package imwhs.eatz_server.domain.eatzuser;

/**
 * 로그인 사용자의 역할을 정의합니다.<br/>
 * 사용자는 ROLE_MEMBER(회원), ROLE_ADMIN(관리자) 중 하나의 역할을 가질 수 있씁니다.
 */
public enum EatzUserRole {

    /**
     * 회원 역할
     */
    ROLE_MEMBER,

    /**
     * 권리자 역할
     */
    ROLE_ADMIN

}
