package imwhs.eatz_server.dto.tag;

import imwhs.eatz_server.domain.Tag;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * 태그의 핵심 정보를 전달할 때 사용하는 DTO입니다.
 * <p>
 *     부가 정보나 연관 관계 엔티티의 정보 없이, 태그를 나타낼 수 있는 Tag 엔티티의 최소한의 필드만 포함합니다.
 * </p>
 */
@EqualsAndHashCode
@Getter
@AllArgsConstructor
public class TagEssentialDto {

    private Long id;

    private String name;

    public static TagEssentialDto from(Tag tag) {
        return new TagEssentialDto(tag.getId(), tag.getName());
    }

}
