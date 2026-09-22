package imwhs.eatz_server.domain.eatzuser;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.RequiredArgsConstructor;

/**
 * 로그인 사용자의 역할을 정의합니다.<br/>
 * 사용자는 ROLE_MEMBER(회원), ROLE_ADMIN(관리자) 중 하나의 역할을 가질 수 있씁니다.
 */
@RequiredArgsConstructor
public enum EatzUserRole {

    /**
     * 회원 역할
     */
    ROLE_MEMBER("member"),

    /**
     * 권리자 역할
     */
    ROLE_ADMIN("admin");

    private final String code;

    /**
     * HTTP 통신 시 클라이언트와 규격을 맞추기 위해 사용할 문자열 값입니다.
     * StringToEatzUserRoleConverter가 HTTP 요청 데이터를 역직렬화할 때 바인딩 기준으로서 사용하며,
     * HTTP 응답을 생성하기 위해 @JsonValue를 통해 직렬화할 때에도 사용됩니다.
     */
    @JsonValue
    public String getCode() {
        return code;
    }

}