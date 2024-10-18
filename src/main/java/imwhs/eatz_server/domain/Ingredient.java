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
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Ingredient {

    @EqualsAndHashCode.Include
    @Id @GeneratedValue
    private Long id;

    /**
     * 재료 이름.
     * <p>
     *     필수 값입니다.
     * </p>
     */
    @EqualsAndHashCode.Include
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
     */
    public Ingredient(String name) {
        this.name = name;
    }

    /**
     * 카테고리 설정.
     * 다른 재료를 카테고리로 지정해, 해당 재료와 연관 관계를 맺습니다.
     */
    public void setParent(Ingredient category) {
        // 카테고리로 설정할 재료의 유효성을 확인합니다.
        if (category == null) return;

        if (category == this) {
            throw new IllegalArgumentException("자신을 카테고리로 지정할 수 없습니다.");
        }

        if (isChildOf(this, category)) {
            throw new IllegalArgumentException("카테고리로 지정할 재료가 현재 재료의 하위 계층에 존재합니다.");
        }

        /**
         * 카테고리로 설정할 재료와 현재 재료가 이미 연관 관계를 갖고 있지는 않은지 확인합니다.
         *
         * 이미 연관 관계를 갖고 있는 경우: 우선 현재 재료의 parent에 설정된 값을 확인합니다.
         *   - 현재 재료의 parent가 null이거나, 다른 재료로 설정되어 있는 경우: 연관 관계를 지우고, 비정상적인 관계로 예외를 발생시킵니다.
         *   - 현재 재료의 parent가 null이 아니면서, 카테고리로 설정할 재료로 설정되어 있는 경우: 메서드를 종료합니다.
         */
        if (category.children.contains(this)) {
            if (this.parent == null || this.parent != category) {
                throw new IllegalStateException("비정상적인 카테고리 설정");
            }
            if (this.parent != null && this.parent == category) {
                return;
            }
        }

        // 카테고리로 설정할 재료와 연관 관계를 설정합니다.
        category.children.add(this);
        this.parent = category;
    }

    /**
     * 하위 재료 추가.
     * 현재 재료를 카테고리로 설정해, 하위 재료와 연관 관계를 맺습니다.
     */
    public void addChild(Ingredient child) {
        // 하위로 추가할 재료의 유효성을 확인합니다.
        if (child == null) return;

        if (child == this) {
            throw new IllegalArgumentException("자신을 하위 재료로 추가할 수 없습니다.");
        }

        // 이미 현재 재료를 카테고리로 설정해, 하위 재료와 연관 관계가 맺어져있지는 않은지 확인합니다.
        if (this.children.contains(child) && child.parent == this) return;

        if (isCategoryOf(this, child)) {
            throw new IllegalArgumentException("하위로 추가할 재료가 현재 재료의 카테고리로서 사용되고 있습니다.");
        }

        // 하위로 추가할 재료를 고립화시킵니다.
        /**
         * 하위로 추가할 재료가 이미 다른 카테고리와 연관 관계를 갖고 있지는 않은지 확인합니다.
         *
         * 연관 관계를 갖고 있는 경우: 재료는 단 하나의 카테고리에만 속할 수 있기에, 하위로 추가할 재료가 속한 카테고리와의 연관 관계를 지웁니다.
         *  - 1. 하위 재료가 아무 카테고리에 속하지 않은 상태로 변경합니다..
         */
        if (child.parent != null && !Objects.equals(child.parent, this)) {
            child.parent.children.remove(child);
        }

        // 하위로 추가할 재료와 연관 관계를 설정합니다.
        child.parent = this;
        this.children.add(child);
    }

    /**
     * 재료 기본 생성자.
     */
    public Ingredient() {}

    /**
     * 계층 관계인 재료 간 순환 참조를 방지하기 위해 현재 재료보다 상위 계층에 하위로 추가할 재료가 존재하는지 확인합니다.
     */
    private boolean isCategoryOf(Ingredient current, Ingredient target) {
        Ingredient parent = current.parent;

        while (parent != null) {
            if (parent == target) {
                return true;
            }

            parent = parent.parent;
        }

        return false;
    }

    private boolean isChildOf(Ingredient current, Ingredient target) {
        List<Ingredient> children = current.children;

        for (Ingredient child : children) {
            if (child == target || isChildOf(child, target)) {
                return true;
            }
        }

        return false;
    }

}
