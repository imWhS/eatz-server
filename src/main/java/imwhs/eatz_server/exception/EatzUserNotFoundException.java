package imwhs.eatz_server.exception;

import imwhs.eatz_server.common.error.EatzUserErrorType;
import imwhs.eatz_server.domain.eatzuser.EatzUserRole;
import imwhs.eatz_server.exception.base.BaseException;

public class EatzUserNotFoundException extends BaseException {

    private static final String MESSAGE_TEMPLATE_INVALID = "사용자를 찾을 수 없어요.";
    private static final String MESSAGE_TEMPLATE_INVALID_USERNAME = "사용자 이름이 %s인 사용자를 찾을 수 없어요.";
    private static final String MESSAGE_TEMPLATE_INVALID_EMAIL = "이메일 주소가 %s인 사용자를 찾을 수 없어요.";
    private static final String MESSAGE_TEMPLATE_INVALID_ID = "ID가 %s인 사용자를 찾을 수 없어요.";
    private static final String MESSAGE_TEMPLATE_INVALID_ID_ROLE = "ID가 %s인 %s를 찾을 수 없어요.";

    public EatzUserNotFoundException() {
        super(EatzUserErrorType.NOT_FOUND);
    }

    public EatzUserNotFoundException(String username) {
        super(EatzUserErrorType.NOT_FOUND, String.format(MESSAGE_TEMPLATE_INVALID_USERNAME, username));
    }

    public EatzUserNotFoundException(Long id, EatzUserRole role) {
        super(EatzUserErrorType.NOT_FOUND, String.format(MESSAGE_TEMPLATE_INVALID_ID_ROLE, id, role == EatzUserRole.ROLE_ADMIN ? "관리자" : "사용자"));
    }

    public EatzUserNotFoundException(String email, boolean isEmail) {
        super(EatzUserErrorType.NOT_FOUND, String.format(isEmail ? MESSAGE_TEMPLATE_INVALID_EMAIL : MESSAGE_TEMPLATE_INVALID, email));
    }

    public EatzUserNotFoundException(Long id) {
        super(EatzUserErrorType.NOT_FOUND, String.format(MESSAGE_TEMPLATE_INVALID_ID, id));
    }

}
