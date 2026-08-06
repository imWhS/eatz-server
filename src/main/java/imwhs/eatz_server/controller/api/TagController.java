package imwhs.eatz_server.controller.api;

import imwhs.eatz_server.domain.Tag;
import imwhs.eatz_server.dto.tag.AddRecipesToTagRequest;
import imwhs.eatz_server.dto.tag.TagEssentialDto;
import imwhs.eatz_server.dto.tag.TagCreateRequest;
import imwhs.eatz_server.dto.tag.TagDetailDto;
import imwhs.eatz_server.dto.tag.TagUpdateRequest;
import imwhs.eatz_server.resolver.AuthenticatedEatzUserId;
import imwhs.eatz_server.service.tag.TagService;
import imwhs.eatz_server.service.tag.ThemeTagQueryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/**
 * 태그를 관리하기 위한 API를 제공하는 컨트롤러입니다.
 */
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v0/tags")
@RestController
public class TagController {

    private final TagService tagService;
    private final ThemeTagQueryService themeTagQueryService;

    /**
     * 새 태그를 등록합니다.
     * @param request 태그 등록 요청 정보
     * @return 생성된 태그 관련 정보
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TagDetailDto registerTag(@Valid @RequestBody TagCreateRequest request) {
        Long recipeId = request.getRecipeId();
        Tag tag;

        if (recipeId == null) {
            tag = tagService.register(request.getName(), request.getEmoji());
        } else {
            tag = tagService.registerAndAddToRecipe(request.getName(), request.getEmoji(), recipeId);
        }

        return new TagDetailDto(tag);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateTag(
            @PathVariable Long id,
            @Valid @RequestBody TagUpdateRequest request,
            @AuthenticatedEatzUserId Long userId) {
        tagService.update(
                id,
                userId,
                request.getName(),
                request.getKeyword(),
                request.getDescription(),
                request.getEmoji());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTag(@PathVariable Long id, @AuthenticatedEatzUserId Long userId) {
        tagService.delete(id, userId);
    }

    @GetMapping("/search")
    public Page<TagDetailDto> searchTags(@RequestParam("name") String name, Pageable pageable) {
        return themeTagQueryService.searchTagDetails(name, pageable);
    }

    @PostMapping("/{id}/recipes")
    public void addRecipes(@PathVariable Long id, @Valid @RequestBody AddRecipesToTagRequest request) {
        tagService.addRecipes(id, request.getRecipeIds());
    }

    @GetMapping("/themes")
    public Page<TagDetailDto> getAllThemedTagDetails(Pageable pageable) {
        return themeTagQueryService.getAllThemedTagDetails(pageable);
    }

    @GetMapping("/{id}")
    public TagEssentialDto getTag(@PathVariable Long id) {
        return themeTagQueryService.getTagEssentialByTagId(id);
    }

}
