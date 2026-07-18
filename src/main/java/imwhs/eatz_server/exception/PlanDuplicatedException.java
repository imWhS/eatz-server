package imwhs.eatz_server.exception;

import imwhs.eatz_server.common.error.EatzPlanErrorType;
import imwhs.eatz_server.exception.base.BaseException;

public class PlanDuplicatedException extends BaseException {

    public PlanDuplicatedException() {
        super(EatzPlanErrorType.DUPLICATED);
    }

}
