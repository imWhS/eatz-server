package imwhs.eatz_server.domain;

import imwhs.eatz_server.common.BaseEntity;
import imwhs.eatz_server.exception.EatzInvalidRequestArgumentException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

/**
 * 테마 정보를 정의하고, 연관 데이터를 관리하는 Theme 엔티티입니다.
 * <ul>
 *     <li> 테마는 태그를 특정 기준으로 분류/그룹핑하거나, 카테고리로 지정하기 위해 사용합니다. </li>
 *     <li> 테마의 이름은 선택 사항입니다. 이름이 null인 테마에 추가된 태그는 카테고리로 지정하되, 분류/그룹핑 하지 않은 상태가 됩니다. </li>
 * </ul>
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@Entity
public class Theme extends BaseEntity {

    @EqualsAndHashCode.Include
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 이름
     */
    private String name;

    /**
     * 태그 목록
     * <ul>
     *     <li> 테마에 추가된 태그 목록입니다. </li>
     *     <li> Theme를 삭제할 때 관련 ThemeTag도 모두 삭제합니다. 또한, 컬렉션에서 특정 ThemeTag가 제거되어
     *          Theme와 연관 관계를 맺지 않을 경우, 해당 ThemeTag도 삭제합니다. </li>
     * </ul>
     */
    @OneToMany(mappedBy = "theme", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ThemeTag> tags = new HashSet<>();

    /**
     * 설명
     */
    private String description;

    private Theme(String name, String description) {
        updateName(name);
        updateDescription(description);
    }

    /**
     * Theme의 주요 필드를 업데이트합니다.
     */
    public void update(String name, String description) {
        updateName(name);
        updateDescription(description);
    }

    public void updateName(String name) {
        validateName(name);
        this.name = name;
    }

    public void updateDescription(String description) {
        validateDescription(description);
        this.description = description;
    }

    public void deleteDescription() {
        this.description = null;
    }

    /**
     * 테마에 태그를 추가하기 위해 ThemeTag 엔티티를 생성한 후 연관 관계를 설정합니다.
     * <ul>
     *     <li> Cascade 옵션에 의해 Theme 엔티티의 영속성 컨텍스트 상태가 ThemeTag 엔티티에 전이됩니다. </li>
     * </ul>
     * @param tag 태그의 Tag 엔티티
     */
    public void addTag(Tag tag) {
        ThemeTag themeTag = ThemeTag.create(this, tag);
        tags.add(themeTag);
        tag.getThemeTags().add(themeTag);
    }

    /**
     * 이름의 유효성을 검증합니다.
     * @param name 이름
     */
    public static void validateName(String name) {
        if (name != null && name.isBlank()) { throw new EatzInvalidRequestArgumentException("이름이 비어 있어요."); }
    }

    /**
     * 설명의 유효성을 검증합니다.
     * @param description 설명
     */
    public static void validateDescription(String description) {
        if (description != null && description.isBlank()) {
            throw new EatzInvalidRequestArgumentException("설명이 비어 있어요."); }
    }

    /**
     * 이름이 없는 Theme 엔티티 팩토리 메서드
     * @return 이름이 없는 Theme 엔티티
     */
    public static Theme create() {
        Theme theme = new Theme();
        return theme;
    }

    /**
     * Theme 엔티티 팩토리 메서드
     * @param name 이름
     * @return Theme 엔티티
     */
    public static Theme create(String name) {
        Theme theme = new Theme(name, null);
        theme.name = name;
        return theme;
    }

    /**
     * Theme 엔티티 팩토리 메서드
     * @param name 이름
     * @param description 설명
     * @return Theme 엔티티
     */
    public static Theme create(String name, String description) {
        Theme theme = new Theme(name, description);
        theme.name = name;
        theme.description = description;
        return theme;
    }

}
