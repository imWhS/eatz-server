package imwhs.eatz_server.exception;

import imwhs.eatz_server.common.error.EatzCommonErrorType;
import imwhs.eatz_server.exception.base.BaseException;

public class AffiliateLinkNotFoundException extends BaseException {

    public AffiliateLinkNotFoundException() {
        super(EatzCommonErrorType.AFFILIATE_LINK_NOT_FOUND);
    }

}
