package imwhs.eatz_server.domain;

import imwhs.eatz_server.common.BaseEntity;
import imwhs.eatz_server.domain.recipe.RecipeTag;
import imwhs.eatz_server.exception.EatzInvalidRequestArgumentException;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.BatchSize;

import java.text.BreakIterator;
import java.util.HashSet;
import java.util.Set;

/**
 * 태그 정보를 정의하는 Tag 엔티티입니다.
 * <p> 특정 Thame에 포함되어서, ThemeTag와 연관 관계를 가지는 Tag는 Recipe들을 분류하는 카테고리로서도 사용됩니다. </p>
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE) // 정적 엔티티 팩토리 메서드 사용 유도
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@Entity
public class Tag extends BaseEntity {

    @EqualsAndHashCode.Include
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 이름
     * <ul>
     *     <li> 필수 항목입니다. </li>
     * </ul>
     */
    @NotNull
    private String name;

    /**
     * 키워드
     * <p>
     *     태그를 대표하는 이름 대신 나타내거나 표현할 수 있는 이름을 추가로 포함합니다.
     * </p>
     */
    private String keyword;

    /**
     * 이모지
     */
    private String emoji;

    /**
     * 설명
     */
    private String description;

    /**
     * 레시피 목록
     * <ul>
     *     <li> 태그가 추가되어 있는 레시피 목록입니다. </li>
     *     <li> 엔티티가 목록으로 조회되어질 때 N+1 문제를 예방하기 위해,
     *          지연 로딩 시점에 조회 범위 내 최대 20개 단위로 batch fetching 처리합니다. </li>
     *     <li> Tag를 삭제할 때 관련 RecipeTag도 모두 삭제합니다. 또한, 컬렉션에서 특정 RecipeTag가 제거되어
     *          Tag와 연관 관계를 맺지 않을 경우, 해당 RecipeTag도 삭제합니다. </li>
     * </ul>
     */
    @BatchSize(size = 20)
    @OneToMany(mappedBy = "tag", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<RecipeTag> recipeTags = new HashSet<>();

    /**
     * 테마 목록
     * <ul>
     *     <li> 태그가 추가되어 있는 레시피 목록입니다. </li>
     *     <li> 엔티티가 목록으로 조회되어질 때 N+1 문제를 예방하기 위해,
     *          지연 로딩 시점에 조회 범위 내 최대 20개 단위로 batch fetching 처리합니다. </li>
     *     <li> Tag를 삭제할 때 관련 ThemeTag도 모두 삭제합니다. 또한, 컬렉션에서 특정 ThemeTag가 제거되어
     *          Tag와 연관 관계를 맺지 않을 경우, 해당 ThemeTag도 삭제합니다. </li>
     * </ul>
     */
    @OneToMany(mappedBy = "tag", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ThemeTag> themeTags = new HashSet<>();

    public void update(String name, String subtitle, String description, String emoji) {
        updateName(name);
        updateKeyword(subtitle);
        updateDescription(description);
        updateEmoji(emoji);
    }

    public void updateName(String name) {
        validateName(name);
        this.name = name;
    }

    public void updateKeyword(String keyword) {
        validateKeyword(keyword);
        this.keyword = keyword;
    }

    public void deleteSubtitle() {
        this.keyword = null;
    }

    public void updateDescription(String description) {
        validateDescription(description);
        this.description = description;
    }

    public void deleteDescription() {
        this.description = null;
    }

    public void updateEmoji(String emoji) {
        validateEmoji(emoji);
        this.emoji = emoji;
    }

    public void deleteEmoji() {
        this.emoji = null;
    }

    private static void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new EatzInvalidRequestArgumentException("태그의 이름이 비어 있어요.");
        }
    }

    private static void validateKeyword(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            throw new EatzInvalidRequestArgumentException("태그의 키워드가 비어 있어요."); }
    }

    private static void validateDescription(String description) {
        if (description == null || description.isBlank()) {
            throw new EatzInvalidRequestArgumentException("태그의 설명이 비어 있어요.");
        }
    }

    public static void validateEmoji(String emoji) {
        if (emoji == null || emoji.isBlank()) { return; }

        BreakIterator it = BreakIterator.getCharacterInstance();
        it.setText(emoji);
    }

    /**
     * Tag 엔티티 팩토리 메서드
     * @param name 이름
     * @return Tag 엔티티
     */
    public static Tag create(String name) {
        validateName(name);
        Tag tag = new Tag();
        tag.name = name;
        return tag;
    }

    /**
     * Tag 엔티티 팩토리 메서드
     * @param name 이름
     * @param emoji 이모지
     * @return Tag 엔티티
     */
    public static Tag create(String name, String emoji) {
        validateName(name);
        validateEmoji(emoji);
        Tag tag = new Tag();
        tag.name = name;
        tag.emoji = emoji;
        return tag;
    }

    /**
     * Tag 엔티티 팩토리 메서드
     * @param name 이름
     * @param keyword 키워드
     * @param emoji 이모지
     * @return Tag 엔티티
     */
    public static Tag create(String name, String keyword, String emoji) {
        validateName(name);
        validateKeyword(keyword);
        validateEmoji(emoji);
        Tag tag = new Tag();
        tag.name = name;
        tag.keyword = keyword;
        tag.emoji = emoji;
        return tag;
    }

}
