package imwhs.eatz_server.exception;

import imwhs.eatz_server.common.error.ErrorCode;

public class CategoryNotFoundException extends BaseException {

    public CategoryNotFoundException() {
        super(ErrorCode.CATEGORY_NOT_FOUND);
    }

    public CategoryNotFoundException(Long id) {
        super(ErrorCode.CATEGORY_NOT_FOUND, "ID가 " + id + "인 카테고리를 찾을 수 없어요.");
    }

}