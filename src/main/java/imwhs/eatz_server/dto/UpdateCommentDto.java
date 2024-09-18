package imwhs.eatz_server.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
@Data
public class UpdateCommentDto extends UpdateReactionDto {

    private String content;

    public UpdateCommentDto(String content) {
        this.content = content;
    }
}
