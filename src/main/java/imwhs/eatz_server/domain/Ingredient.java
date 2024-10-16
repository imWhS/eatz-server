package imwhs.eatz_server.domain;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Ingredient 엔티티.<br/>
 * <p>
 * Recipe를 요리하기 위해 필요한 재료를 나타내는 엔티티 클래스입니다.
 * 재료는 상위 재료에 속할 수 있고, 하위 재료를 가질 수도 있습니다.
 * 이때, 해당 재료가 속해있는 상위 재료 또는, 하위 재료를 가지고 있는 재료는 카테고리(category)로서 역할을 합니다.
 */
@Entity
@EqualsAndHashCode
public class Ingredient {

    @Id @GeneratedValue
    private Long id;

    /**
     * 재료 이름.
     * <p>
     *     필수 값입니다.
     * </p>
     */
    @Column(nullable = false)
    private String name;

    /**
     * 상위 재료.
     * <p>
     *     재료가 속한 카테고리입니다.
     *     아무 카테고리에도 속하지 않은 경우 null 값을 가집니다.
     * </p>
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Ingredient parent;

    /**
     * 하위 재료.
     * <p>
     *     카테고리로서의 해당 재료에 속해있는 다른 재료들의 목록입니다.
     * </p>
     */
    @OneToMany(mappedBy = "parent")
    private List<Ingredient> children = new ArrayList<>();

    /**
     * 재료 생성자.
     * @param name 재료 이름
     * @param parent 카테고리로서 지정할 상위 재료. null이면 카테고리를 지정하지 않습니다.
     * @param children 해당 재료를 카테고리로서 묶을 하위 재료 목록. null이면 하위 재료를 추가하지 않습니다.
     */
    public Ingredient(String name, Ingredient parent, List<Ingredient> children) {
        this.name = name;

        // 지정할 상위 재료를 파라미터로 전달받은 경우
        if (parent != null) {
            parent.children.add(this);
            this.parent = parent;
        }

        // 하위 재료로 추가할 재료 목록을 파라미터로 전달받은 경우
        if (children != null) {
            for (Ingredient child : children) {
                if (child.parent != null) {
                    // 하위 재료가 다른 카테고리에 속해있는 경우, 해당 카테고리와의 연관 관계를 제거합니다.
                    child.parent.children.remove(child);
                }

                // 하위 재료의 카테고리를 현재 재료로 설정합니다.
                child.parent = this;

                // 해당 재료를 카테고리로서 하위 재료 목록에 추가합니다.
                this.children.add(child);
            }
        }
    }

    /**
     * 재료 기본 생성자.
     */
    public Ingredient() {}
}
