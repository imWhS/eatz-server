package imwhs.eatz_server.service.tag;

import imwhs.eatz_server.domain.Tag;
import imwhs.eatz_server.domain.Theme;
import imwhs.eatz_server.domain.recipe.Recipe;
import imwhs.eatz_server.exception.EatzInvalidRequestArgumentException;
import imwhs.eatz_server.exception.TagNotFoundException;
import imwhs.eatz_server.repository.EatzUserRepository;
import imwhs.eatz_server.repository.tag.TagRepository;
import imwhs.eatz_server.repository.recipe.RecipeRepository;
import imwhs.eatz_server.repository.tag.ThemeRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class TagService {

    private final TagRepository tagRepository;
    private final ThemeRepository themeRepository;
    private final RecipeRepository recipeRepository;
    private final EatzUserRepository userRepository;

    /**
     * 태그를 생성한 후 저장합니다.
     * @param name 태그의 이름
     * @param emoji 태그의 이모지
     * @return 생성된 Tag 엔티티
     */
    @Transactional(rollbackFor = Exception.class)
    public Tag register(String name, String emoji) {
        if (name == null || name.isBlank()) { throw new EatzInvalidRequestArgumentException("태그 이름은 필수 항목이에요."); }
        tagRepository.validateDuplicatesByName(name);

        Tag tag = Tag.create(name, emoji);
        tagRepository.save(tag);
        return tag;
    }

    /**
     * 새 태그를 등록한 후 레시피에 추가합니다.
     * 태그를 생성하고, 레시피와 연관 관계를 설정한 후 저장합니다.
     * @param name 태그의 이름
     * @param emoji 태그의 이모지
     * @param recipeId 레시피의 ID
     * @return 생성된 Tag 엔티티
     */
    @Transactional(rollbackFor = Exception.class)
    public Tag registerAndAddToRecipe(String name, String emoji, Long recipeId) {
        Tag tag = register(name, emoji);

        if (recipeId != null) {
            Recipe recipe = recipeRepository.get(recipeId);
            recipe.addTag(tag);
        }

        return tag;
    }

    /**
     * 새 태그를 등록한 후 테마에 추가합니다.
     * 태그를 생성하고, 테마와 연관 관계를 설정한 후 저장합니다.
     * @param name 태그의 이름
     * @param emoji 태그의 이모지
     * @param themeId 테마의 ID
     * @return 생성된 Tag 엔티티
     */
    @Transactional(rollbackFor = Exception.class)
    public Tag registerAndAddToThemeById(String name, String emoji, Long themeId) {
        Tag tag = register(name, emoji);

        if (themeId != null) {
            Theme theme = themeRepository.get(themeId);
            theme.addTag(tag);
        }

        return tag;
    }

    /**
     * 태그를 생성하고, 태그와 연관된 테마와 매핑한 후 저장합니다.
     * @param name 태그의 이름
     * @param emoji 태그의 이모지
     * @return 생성된 Tag 엔티티
     */
    @Transactional(rollbackFor = Exception.class)
    public Tag registerAndAddToNoNameTheme(String name, String emoji) {
        Tag tag = register(name, emoji);
        Theme theme = themeRepository.findByNameIsNullAndDeletedAtIsNull().orElseGet(() -> { return createThemeNoNamed(); });
        theme.addTag(tag);
        return tag;
    }

    /**
     * 태그를 업데이트합니다.
     * @param id 업데이트할 태그의 ID
     * @param userId 업데이트를 요청한 사용자의 ID
     * @param name 태그의 이름
     * @param description 업데이트할 태그의 설명
     */
    @Transactional(rollbackFor = Exception.class)
    public void update(
            Long id,
            Long userId,
            String name,
            String keyword,
            String description,
            String emoji) {
        userRepository.validateExistsAsAdmin(userId);

        Tag tag = tagRepository.findById(id).orElseThrow(() -> new TagNotFoundException(id));
        if (!tag.getName().equals(name)) { tagRepository.validateDuplicatesByName(name); }

        tag.updateName(name);
        tag.updateKeyword(keyword);
        tag.updateDescription(description);
        tag.updateEmoji(emoji);
    }

    /**
     * 태그를 삭제합니다.
     * @param id 태그의 ID
     * @param userId 사용자의 ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id, Long userId) {
        userRepository.validateExistsAsAdmin(userId);
        tagRepository.deleteById(id);
    }

    /**
     * 이름에 해당하는 여러 태그들을 레시피에 추가합니다.
     * <p>
     *     이름에 해당하는 태그가 존재하지 않으면, 해당 태그의 Tag 엔티티를 생성한 후 레시피에 추가합니다.
     * </p>
     * @param names 레시피에 추가하려는 태그 이름 목록
     * @param recipe 레시피의 Recipe 엔티티
     */
    @Transactional(rollbackFor = Exception.class)
    public void addAllToRecipe(List<String> names, Recipe recipe) {
        if (names == null || names.isEmpty()) { return; }

        List<Tag> existingTags = tagRepository.findAllByNameIn(names);

        Map<String, Tag> existingTagMap = existingTags.stream()
                .collect(Collectors.toMap(Tag::getName, tag -> tag));

        // 사용자가 태그를 추가한 순서를 보장하기 위해, names 목록을 순회하며 레시피에 태그를 추가합니다.
        for (String name : names) {
            Tag tag = existingTagMap.get(name);

            // 해당 이름의 Tag가 없을 경우, 해당 이름으로 새 Tag 엔티티를 생성한 후 저장합니다.
            // 이미 해당 이름의 Tag가 존재한다면 바로 RecipeTag 엔티티를 생성해서, Recipe와 연관 관계를 설정합니다.
            if (tag == null) {
                tag = Tag.create(name);
                tagRepository.save(tag);
            }

            // Recipe를 저장할 때, RecipeTag도 함께 저장되도록 Recipe와 연관 관계를 설정합니다.
            recipe.addTag(tag);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void addRecipes(Long id, List<Long> recipeIds) {
        Tag tag = tagRepository.get(id);
        addRecipesToTag(tag, recipeIds);
    }

    private Theme createThemeNoNamed() {
        Theme newTheme = Theme.create();
        themeRepository.save(newTheme);
        return newTheme;
    }

    private void addRecipesToTag(Tag tag, List<Long> recipeIds) {
        Set<Long> ids = Set.copyOf(recipeIds);
        List<Recipe> recipes = recipeRepository.findAllById(ids);
        if (recipes.size() != ids.size()) {
            throw new EntityNotFoundException("태그에 추가하려는 레시피 중 일부가 유효하지 않아요.");
        }
        recipes.forEach(recipe -> {
            recipe.addTag(tag);
        });
    }

}
