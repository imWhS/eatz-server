package imwhs.eatz_server.controller.api;

import imwhs.eatz_server.dto.tag.TagDetailDto;
import imwhs.eatz_server.dto.tag.theme.*;
import imwhs.eatz_server.service.tag.ThemeTagQueryService;
import imwhs.eatz_server.service.tag.ThemeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/api/v0/themes")
@RestController
public class ThemeController {

    private final ThemeService themeService;
    private final ThemeTagQueryService themeTagQueryService;

    /**
     * 테마를 생성합니다.
     */
    @PostMapping
    public void registerTheme(@RequestBody ThemeCreateRequest request) {
        themeService.register(request);
    }

    @PutMapping("/{id}")
    public ThemeDto updateThemeEssential(@PathVariable long id, @RequestBody ThemeUpdateRequest request) {
        return themeService.update(id, request);
    }

    @PostMapping("/{id}/tags")
    public void addTagsToTheme(@PathVariable long id, @RequestBody AddTagsToThemeRequest request) {
        themeService.addTags(id, request);
    }

    @PutMapping("/{id}/tags")
    public void replaceThemeTags(@PathVariable long id, @RequestBody ThemeTagsReplaceRequest request) {
        themeService.replaceThemeTags(id, request);
    }

    @DeleteMapping("/{id}")
    public void deleteTheme(@PathVariable long id) {
        themeService.delete(id);
    }

    /**
     * 모든 테마 목록을 가져옵니다.
     */
    @GetMapping
    public List<ThemeDto> getAllThemes() {
        return themeTagQueryService.getAllThemes();
    }

    /**
     * 특정 테마에 포함되어 있는 태그 목록을 가져옵니다.
     */
    @GetMapping("/{id}")
    public List<TagDetailDto> getTagsByTheme(@PathVariable long id) {
        return themeTagQueryService.getAllTagDetailsByThemeId(id);
    }

    /**
     * 모든 테마 및 각 테마에 포함되어 있는 태그 목록을 가져옵니다.
     */
    @GetMapping("/with-tags")
    public List<ThemeNamedWithTagsDto> getAllThemesWithTags() {
        return themeTagQueryService.getAllNamedWithTags();
    }

}
