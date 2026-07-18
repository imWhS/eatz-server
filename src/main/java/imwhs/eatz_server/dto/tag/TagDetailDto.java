package imwhs.eatz_server.dto.tag;

import imwhs.eatz_server.domain.Tag;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 태그(Tag)의 상세한 정보를 전달할 때 사용하는 DTO입니다.
 */
@EqualsAndHashCode
@Getter
@AllArgsConstructor
public class TagDetailDto {

    private Long id;
    private String name;
    private String keyword;
    private String emoji;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public TagDetailDto(Tag tag) {
        this.id = tag.getId();
        this.name = tag.getName();
        this.keyword = tag.getKeyword();
        this.emoji = tag.getEmoji();
        this.description = tag.getDescription();
        this.createdAt = tag.getCreatedAt();
        this.updatedAt = tag.getUpdatedAt();
    }

    public static TagDetailDto from(Tag tag) {
        return new TagDetailDto(tag);
    }

}
