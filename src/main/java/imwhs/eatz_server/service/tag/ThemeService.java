package imwhs.eatz_server.service.tag;

import imwhs.eatz_server.domain.Tag;
import imwhs.eatz_server.domain.Theme;
import imwhs.eatz_server.dto.tag.theme.*;
import imwhs.eatz_server.exception.ThemeNotFoundException;
import imwhs.eatz_server.repository.tag.TagRepository;
import imwhs.eatz_server.repository.tag.ThemeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class ThemeService {

    private final ThemeRepository themeRepository;
    private final TagRepository tagRepository;

    /**
     * 테마를 생성하고, 테마와 연관된 태그와 매핑한 후 저장합니다.
     * @param request 테마 생성 및 등록 요청 정보
     */
    @Transactional(rollbackFor = Exception.class)
    public void register(ThemeCreateRequest request) {
        Theme theme = Theme.create(request.getName(), request.getDescription());
        themeRepository.save(theme);

        if (request.getTagIds() != null && !request.getTagIds().isEmpty()) {
            findAndAddTagsToTheme(theme, request.getTagIds());
        }
    }

    /**
     * 테마에 태그를 추가합니다.
     * @param id 테마의 ID
     * @param request 태그 추가 요청 정보
     */
    @Transactional(rollbackFor = Exception.class)
    public void addTags(Long id, AddTagsToThemeRequest request) {
        Theme theme = themeRepository.findById(id).orElseThrow(() -> new ThemeNotFoundException(id));

        if (request.getTagIds() != null && !request.getTagIds().isEmpty()) {
            findAndAddTagsToTheme(theme, request.getTagIds());
        }
    }

    /**
     * 테마를 업데이트합니다.
     * @param id 테마의 ID
     * @param request 테마 업데이트 요청 정보
     * @return 업데이트 완료된 테마 정보
     */
    @Transactional(rollbackFor = Exception.class)
    public ThemeDto update(Long id, ThemeUpdateRequest request) {
        Theme theme = themeRepository.findById(id).orElseThrow(() -> new ThemeNotFoundException(id));
        theme.update(request.getName(), request.getDescription());
        return ThemeDto.from(theme);
    }

    @Transactional(rollbackFor = Exception.class)
    public void replaceThemeTags(Long id, ThemeTagsReplaceRequest request) {
        Theme theme = themeRepository.findById(id).orElseThrow(() -> new ThemeNotFoundException(id));
        theme.getTags().clear();

        if (request.getTagIds() != null && !request.getTagIds().isEmpty()) {
            findAndAddTagsToTheme(theme, request.getTagIds());
        }
    }

    /**
     * 테마를 삭제 처리합니다.
     * @param id 테마의 ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        Theme theme = themeRepository.findById(id).orElseThrow(() -> new ThemeNotFoundException(id));
        theme.markAsDeleted();
    }

    private void findAndAddTagsToTheme(Theme theme, List<Long> tagIds) {
        // 테마에 추가할 태그의 중복을 제거하기 위해 Set으로 변환합니다.
        Set<Long> ids = Set.copyOf(tagIds);

        List<Tag> tags = tagRepository.findAllByIdInAndDeletedAtIsNull(ids);
        if (tags.size() != ids.size()) {
            throw new IllegalArgumentException("테마에 추가하려는 태그 중 일부가 유효하지 않아요.");
        }

        tags.forEach(theme::addTag);
    }

}