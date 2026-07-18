package imwhs.eatz_server.dto.tag.theme;

import imwhs.eatz_server.domain.Tag;
import imwhs.eatz_server.domain.Theme;
import imwhs.eatz_server.dto.tag.TagDetailDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class ThemeNamedWithTagsDto {

    private final Long id;

    /**
     * 테마의 이름
     */
    private final String name;

    /**
     * 테마의 설명
     */
    private final String description;

    /**
     * 테마에 속한 태그 목록
     * <p>
     *     순서를 보장하기 위해 List를 사용합니다.
     * </p>
     */
    private final List<TagDetailDto> tags;

    public static ThemeNamedWithTagsDto from(Theme theme) {
        List<TagDetailDto> tagDetailDtos = theme.getTags().stream().map(themeTag -> {
            Tag tag = themeTag.getTag();
            TagDetailDto tagDetailDto = TagDetailDto.from(tag);
            return tagDetailDto;
        }).toList();

        return new ThemeNamedWithTagsDto(theme.getId(), theme.getName(), theme.getDescription(), tagDetailDtos);
    }

}
