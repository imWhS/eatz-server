package imwhs.eatz_server.repository.comment;

import imwhs.eatz_server.dto.comment.CommentByRecipeResponseDto;
import imwhs.eatz_server.dto.comment.CommentDetailResponseDto;

import java.util.List;
import java.util.Optional;

public interface CommentCustomRepository {

    Optional<CommentDetailResponseDto> findCommentDetailById(Long id);

}
