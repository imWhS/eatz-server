package imwhs.eatz_server.dto.tag.theme;

import imwhs.eatz_server.domain.Theme;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ThemeDto {

    private final Long id;
    private final String name;
    private final String description;

    public static ThemeDto from(Theme theme) {
        return new ThemeDto(theme.getId(), theme.getName(), theme.getDescription());
    }

}
