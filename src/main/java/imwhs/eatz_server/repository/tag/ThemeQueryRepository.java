package imwhs.eatz_server.repository.tag;

import imwhs.eatz_server.dto.tag.theme.ThemeNamedWithTagsDto;

import java.util.List;

public interface ThemeQueryRepository {

    /**
     * 1개 이상의 태그를 포함하는 테마 목록을 각 테마에 속한 태그 목록을 포함해 조회합니다.
     * 이름이 없는 테마는 조회 대상에서 제외합니다.
     * @return 1개 이상의 태그를 포함하는 테마 목록
     */
    List<ThemeNamedWithTagsDto> findAllNamedWithTags();

}
