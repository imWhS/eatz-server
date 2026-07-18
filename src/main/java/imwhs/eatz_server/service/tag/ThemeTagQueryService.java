package imwhs.eatz_server.service.tag;

import imwhs.eatz_server.domain.Tag;
import imwhs.eatz_server.domain.Theme;
import imwhs.eatz_server.dto.tag.TagDetailDto;
import imwhs.eatz_server.dto.tag.TagEssentialDto;
import imwhs.eatz_server.dto.tag.theme.ThemeDto;
import imwhs.eatz_server.dto.tag.theme.ThemeNamedWithTagsDto;
import imwhs.eatz_server.repository.tag.TagRepository;
import imwhs.eatz_server.repository.tag.ThemeTagRepository;
import imwhs.eatz_server.repository.tag.ThemeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 태그(Tag), 테마(Theme) 관련 정보를 조회하기 위한 서비스입니다.
 * Tag 및 Theme, ThemeTag(테마에 추가된 태그)에 대한 읽기 전용 쿼리 메서드를 제공합니다.
 */
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class ThemeTagQueryService {

    private final TagRepository tagRepository;
    private final ThemeTagRepository themeTagRepository;
    private final ThemeRepository themeRepository;

    /**
     * 검색어(이름)에 해당하는 태그의 상세한 정보 목록을 검색합니다.
     * <ul>
     *     <li> 검색어에 포함된 모든 공백은 제거됩니다. </li>
     * </ul>
     * @param keyword 검색어
     * @param pageable 페이징 정보
     * @return 검색된 태그의 상세한 정보 목록과 페이징 정보
     */
    public Page<TagDetailDto> searchTagDetails(String keyword, Pageable pageable) {
        String keywordIgnoredBlanks = keyword.replace(" ", "");
        Page<Tag> tags = tagRepository.searchByName(keywordIgnoredBlanks, pageable);
        return tags.map(TagDetailDto::new);
    }

    /**
     * 태그의 핵심 정보를 가져옵니다.
     * @param id 태그의 ID
     * @return 태그의 핵심 정보
     */
    public TagEssentialDto getTagEssentialByTagId(Long id) {
        Tag tag = tagRepository.get(id);
        return TagEssentialDto.from(tag);
    }

    /**
     * 테마에 포함되어 있는 모든 태그의 상세한 정보 목록을 가져옵니다.
     * @param pageable 페이징 정보
     * @return 테마에 포함되어 있는 태그의 상세한 정보 목록과 페이징 정보
     */
    public Page<TagDetailDto> getAllThemedTagDetails(Pageable pageable) {
        Page<Tag> tags = themeTagRepository.findAllThemedTags(pageable);
        return tags.map(TagDetailDto::new);
    }

    /**
     * 모든 테마 목록을 가져옵니다.
     * @return 모든 테마 목록
     */
    public List<ThemeDto> getAllThemes() {
        List<Theme> themes = themeRepository.findAll();
        return themes.stream().map(ThemeDto::from).toList();
    }

    /**
     * 특정 테마에 포함되어 있는 모든 태그 목록을 가져옵니다.
     * @param id 테마의 ID
     */
    public List<TagDetailDto> getAllTagDetailsByThemeId(Long id) {
        List<Tag> tags = themeTagRepository.findAllTagsByThemeId(id);
        return tags.stream().map(TagDetailDto::from).toList();
    }

    /**
     * 이름이 있는 모든 테마 및 각 테마에 포함되어 있는 태그 목록을 가져옵니다.
     * 전체 테마 별 태그 목록이 필요한 경우에 활용할 수 있습니다.
     */
    public List<ThemeNamedWithTagsDto> getAllNamedWithTags() {
        return themeRepository.findAllNamedWithTags();
    }

}
