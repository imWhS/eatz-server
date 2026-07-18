package imwhs.eatz_server.exception;

import imwhs.eatz_server.common.error.EatzPlanErrorType;
import imwhs.eatz_server.exception.base.BaseException;

public class PlanNotFoundException extends BaseException {

    private static final String MESSAGE_TEMPLATE_INVALID_ID = "ID가 %d인 플랜을 찾을 수 없어요.";

    public PlanNotFoundException() {
        super(EatzPlanErrorType.NOT_FOUND);
    }

    public PlanNotFoundException(Long id) {
        super(EatzPlanErrorType.NOT_FOUND,  String.format(MESSAGE_TEMPLATE_INVALID_ID, id));
    }

}
