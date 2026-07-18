package imwhs.eatz_server.exception;

import imwhs.eatz_server.common.error.EatzCommonErrorType;
import imwhs.eatz_server.exception.base.BaseException;

public class CommentNotFoundException extends BaseException {

    public CommentNotFoundException() {
        super(EatzCommonErrorType.COMMENT_NOT_FOUND);
    }

    public CommentNotFoundException(Long id) {
        super(EatzCommonErrorType.COMMENT_NOT_FOUND, "ID가 " + id + "인 댓글을 찾을 수 없어요.");
    }

}
