package imwhs.eatz_server.repository.comment;

import imwhs.eatz_server.dto.comment.CommentDto;

import java.util.Optional;

public interface CommentCustomRepository {

    Optional<CommentDto> findCommentDetailById(Long id);

}
