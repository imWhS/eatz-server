package imwhs.eatz_server.exception;

import imwhs.eatz_server.common.error.EatzCommonErrorType;
import imwhs.eatz_server.exception.base.BaseException;

public class AffiliateNotFoundException extends BaseException {

    public AffiliateNotFoundException() {
        super(EatzCommonErrorType.AFFILIATE_NOT_FOUND);
    }

}
