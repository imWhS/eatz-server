package imwhs.eatz_server.domain;

import imwhs.eatz_server.common.BaseEntity;
import imwhs.eatz_server.exception.EatzInvalidRequestArgumentException;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

/**
 * 테마에 추가된 태그를 정의하고, 연관 데이터를 관리하는 ThemeTag 엔티티입니다.
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@Table(name = "theme_tag", uniqueConstraints = {
        @UniqueConstraint(name = "uk_theme_tag", columnNames = {"theme_id", "tag_id"})
})
@Entity
public class ThemeTag extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 테마
     * <ul>
     *     <li> 필수 항목입니다. </li>
     *     <li> 태그를 추가한 테마입니다. </li>
     *     <li> 연관 관계인 Theme 레코드가 삭제되면,
     *          데이터베이스를 통해 해당 Theme의 ID가 외래 키인 ThemeTag 레코드도 일괄 삭제합니다. </li>
     * </ul>
     */
    @NotNull
    @OnDelete(action = OnDeleteAction.CASCADE)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "theme_id", nullable = false)
    @EqualsAndHashCode.Include
    private Theme theme;

    /**
     * 태그
     * <ul>
     *     <li> 필수 항목입니다. </li>
     *     <li> 테마에 추가된 태그입니다. </li>
     *     <li> 연관 관계인 Tag 레코드가 삭제되면,
     *          데이터베이스를 통해 해당 Recipe의 ID가 외래 키인 ThemeTag 레코드도 일괄 삭제합니다. </li>
     * </ul>
     */
    @NotNull
    @OnDelete(action = OnDeleteAction.CASCADE)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tag_id", nullable = false)
    @EqualsAndHashCode.Include
    private Tag tag;

    /**
     * ThemeTag 엔티티 팩토리 메서드
     * <ul>
     *     <li> Theme와 Tag로 ThemeTag 엔티티를 생성합니다. </li>
     *     <li> ThemeTag는 특정 테마에 태그를 추가하기 위해 사용하며, Theme와 Tag의 N:M 연관 관계를 N:1로 풀어서 매핑하는 엔티티입니다.
     *          테마에 태그를 추가하기 위해 ThemeTag 엔티티를 생성하려면 Theme.addTag()를 호출해야 합니다. </li>
     * </ul>
     * @param theme 테마
     * @param tag 태그의 Tag 엔티티
     * @return ThemeTag 엔티티
     */
    protected static ThemeTag create(Theme theme, Tag tag) {
        validateTheme(theme);
        validateTag(tag);
        return new ThemeTag(null, theme, tag);
    }

    public static void validateTheme(Theme theme) {
        if (theme == null) {
            throw new EatzInvalidRequestArgumentException("필수 항목인 테마가 비어 있어요."); // TODO: Assert.notNull
        }
    }

    public static void validateTag(Tag tag) {
        if (tag == null) {
            throw new EatzInvalidRequestArgumentException("필수 항목인 태그가 비어 있어요.");
        }
    }

}
