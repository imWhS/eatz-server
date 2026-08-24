package imwhs.eatz_server.domain;

import imwhs.eatz_server.common.BaseEntity;
import imwhs.eatz_server.exception.EatzInvalidRequestArgumentException;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 도구 정보를 정의하는 Kitchenware 엔티티입니다.<br/>
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@Entity
public class Kitchenware extends BaseEntity {

    @EqualsAndHashCode.Include
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 이름
     * <p> 필수 항목입니다. </p>
     */
    @NotNull
    @Column(nullable = false)
    private String name;

    /**
     * 대표 이미지 URL
     */
    private String imageUrl;

    private Kitchenware(String name) {
        this.name = name;
    }

    private Kitchenware(String name,  String imageUrl) {
        this.name = name;
        this.imageUrl = imageUrl;
    }

    /**
     * 대표 이미지를 삭제합니다.
     */
    public void deleteImageUrl() {
        this.imageUrl = null;
    }

    /**
     * 이름을 업데이트합니다.
     * @param name 새 이름
     */
    public void updateName(String name) {
        if (name == null || name.isBlank()) {
            throw new EatzInvalidRequestArgumentException("변경하려는 도구의 이름이 비어 있어요.");
        }

        this.name = name;
    }

    /**
     * 대표 이미지 URL을 업데이트합니다.
     * @param imageUrl 새 이미지 URL
     */
    public void updateImageUrl(String imageUrl) {
        if (imageUrl == null || imageUrl.isBlank()) {
            throw new EatzInvalidRequestArgumentException("변경하려는 도구의 대표 이미지 URL이 비어 있어요.");
        }

        this.imageUrl = imageUrl;
    }

    /**
     * 도구를 업데이트합니다.
     * @param name 새 도구 이름. null이거나 빈 문자열이면 기존 이름을 계속 사용합니다.
     */
    public void update(String name) {
        // 이름을 업데이트합니다. 이름은 필수 항목이기 때문에, name이 null이거나 빈 문자열이면 기존 이름을 유지합니다.
        this.name = (name == null || name.isBlank()) ? this.name : name;
    }

    public static Kitchenware create(String name) {
        return new Kitchenware(name);
    }

    public static Kitchenware create(String name, String imageUrl) {
        return new Kitchenware(name, imageUrl);
    }

}
