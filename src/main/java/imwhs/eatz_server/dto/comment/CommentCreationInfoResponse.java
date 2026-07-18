package imwhs.eatz_server.dto.comment;

import imwhs.eatz_server.domain.Comment;
import imwhs.eatz_server.dto.CreationInfoResponse;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
public class CommentCreationInfoResponse extends CreationInfoResponse {

    public CommentCreationInfoResponse(Long id, LocalDateTime createdAt) {
        super(id, createdAt);
    }

    public CommentCreationInfoResponse(Comment comment) {
        this(comment.getId(), comment.getCreatedAt());
    }

}
