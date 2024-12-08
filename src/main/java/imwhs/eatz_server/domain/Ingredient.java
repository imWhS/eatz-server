package imwhs.eatz_server.domain;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.hibernate.annotations.BatchSize;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Ingredient 클래스입니다.<br/>
 * <ul>
     * <li>레시피를를 요리하기 위해 필요한 재료 정보를 저장, 관리하는 엔티티 클래스입니다.</li>
     * <li>재료는 상위 재료에 속할 수 있고, 하위 재료를 가질 수도 있습니다.</li>
     * <li>
     * 이때, 해당 재료가 속해있는 상위 재료 또는, 하위 재료를 가지고 있는 재료는 카테고리(category)로서의 역할을 합니다.
     * apple이라는 재료가 fruit라는 상위 재료에 속한다면, fruit는 apple의 상위 재료이자 apple이 속한 카테고리가 됩니다.
     * </li>
 * </ul>
 */
@Getter
@Entity
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Ingredient {

    @EqualsAndHashCode.Include
    @Id @GeneratedValue
    @Column(name = "ingredient_id")
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
     * 카테고리.
     * <ul>
     *     <li>재료가 속해 있는 카테고리로, 상위 재료에 해당합니다.</li>
     *     <li>아무 카테고리에도 속하지 않은 경우 null 값을 가집니다.</li>
     * </ul>
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Ingredient category;

    /**
     * 하위 재료 컬렉션.
     * <p>
     *     카테고리로서의 해당 재료에 속해 있는 다른 재료들의 목록입니다.
     * </p>
     */
    @OneToMany(mappedBy = "category")
    @BatchSize(size = 10)
    private List<Ingredient> children = new ArrayList<>();

    /**
     * 재료 생성자.
     */
    public Ingredient(String name) {
        this.name = name;
    }

    /**
     * 재료 정보를 업데이트합니다.
     * @param name 새 재료 이름. null이거나 빈 문자열이면 기존 이름을 계속 사용합니다.
     * @param category 새 카테고리. null이면 카테고리를 해제합니다.
     * @param children 새 하위 재료 목록. 기존 하위 재료는 모두 제거됩니다. null이거나 비어있으면 기존 하위 재료를 유지합니다.
     */
    public void update(String name, Ingredient category, List<Ingredient> children) {
        // 이름을 업데이트합니다. 이름은 필수 항목이기 때문에, name이 null이거나 빈 문자열이면 기존 이름을 유지합니다.
        this.name = (name == null || name.isEmpty()) ? this.name : name;

        // 카테고리를 업데이트합니다. category가 null인 경우 카테고리를 해제합니다.
        if (category == null) this.removeCategory();
        else this.setCategory(category);

        if (children != null && !children.isEmpty()) {
            // 하위 재료와의 연관 관계를 해제함으로써, 순회 중인 하위 재료 목록이 수정되지 않도록 하기 위해 순회를 위한 임시 목록을 만듭니다.
            List<Ingredient> tmpChildren = new ArrayList<>(this.children);

            // 기존 하위 재료와의 연관 관계를 해제합니다.
            for (Ingredient child : tmpChildren) {
                child.removeCategory();
            }

            // 기존 모든 하위 재료들의 연관 관계를 해제합니다.
            this.children.clear();

            // children을 하위 재료 목록에 반영하면서, 양방향 연관 관계를 설정합니다.
            for (Ingredient child : children) {
                this.addChild(child);
            }
        }
    }

    /**
     * 카테고리를 설정합니다.
     * 다른 재료를 카테고리로 설정해, 해당 재료와 연관 관계를 맺습니다.
     * @param category 카테고리로 설정할 재료.
     * @throws IllegalArgumentException 유효하지 않은 카테고리를 설정한 경우.
     */
    public void setCategory(Ingredient category) {
        if (category == null) return;

        // 카테고리로 설정할 재료의 유효성을 확인합니다.
        if (category == this) {
            throw new IllegalArgumentException("자신을 카테고리로 설정할 수 없습니다.");
        }

        // 카테고리로 설정할 재료와 이미 연관 관계 설정이 되어 있는지 확인합니다.
        if (category.children.contains(this) && this.category == category) {
            return;
        }

        // 카테고리로 설정할 재료가 이미 하위 재료로 설정되어 있지는 않은지 확인합니다.
        if (isChildOf(category)) {
            throw new IllegalArgumentException("현재 재료의 하위 계층에 존재하는 재료를 카테고리로 설정할 수 없습니다.");
        }

        // 기존 카테고리와의 연관 관계를 해제합니다.
        if (this.category != null && this.category != category) {
            this.category.children.remove(this);
        }

        // 카테고리로 설정할 재료와 연관 관계를 설정합니다.
        category.children.add(this);
        this.category = category;
    }

    /**
     * 카테고리를 해제합니다.
     */
    public void removeCategory() {
        this.category.children.remove(this);
        this.category = null;
    }

    /**
     * 하위 재료를 추가합니다.
     * 현재 재료를 카테고리로서, 하위 재료와 연관 관계를 맺음으로써 특정 재료를 현재 재료의 하위 계층에 추가합니다.
     * @param child 하위 재료로서 추가할 재료.
     * @throws IllegalArgumentException 유효하지 않은 재료를 하위 재료로서 설정하려는 경우.
     */
    public void addChild(Ingredient child) {
        // 하위로 추가할 재료의 유효성을 확인합니다.
        if (child == null) {
            throw new IllegalArgumentException("하위 계층에 추가할 재료가 존재하지 않습니다.");
        }

        if (child == this) {
            throw new IllegalArgumentException("자신을 하위 재료로 추가할 수 없습니다.");
        }

        // 이미 현재 재료를 카테고리로서, 하위 재료와 연관 관계가 맺어져있지는 않은지 확인합니다.
        if (this.children.contains(child) && child.category == this) return;

        // 순환 참조 방지를 위해, 하위 계층에 둘 재료가 이미 현재 재료의 카테고리로서 설정돼있지는 않은지 확인합니다.
        if (isCategoryOf(child)) {
            throw new IllegalArgumentException("현재 재료의 카테고리로서 사용 중인 재료를 현재 재료의 하위 계층에 추가할 수 없습니다.");
        }

        // 하위 계층에 둘 재료가 이미 다른 카테고리와 연관 관계를 갖고 있지 않은지 확인합니다.
        // 이미 다른 카테고리와 연관 관계를 갖고 있다면, 해당 재료 엔티티를 통해 카테고리 설정을 해제해야 합니다.
        if (child.category != null && !Objects.equals(child.category, this)) {
            throw new IllegalArgumentException("하위 계층에 추가할 재료가 이미 " + child.category.getName() + " 카테고리에 속해 있습니다.");
        }

        // 하위로 추가할 재료와 연관 관계를 설정합니다.
        child.category = this;
        this.children.add(child);
    }

    /**
     * 하위 재료를 삭제합니다.
     */
    public void removeChild(Ingredient child) {
        if (child == null) {
            throw new IllegalArgumentException("삭제하려는 재료가 유효하지 않습니다.");
        }

        if (child.category == null) {
            throw new IllegalArgumentException("삭제하려는 재료가 어떠한 카테고리에도 속해 있지 않습니다.");
        }

        if (child.category != this) {
            throw new IllegalArgumentException("삭제하려는 재료가 다른 카테고리(" + child.category.getName() + ")에 속해 있습니다.");
        }

        child.category = null;
        this.children.remove(child);
    }

    /**
     * 모든 하위 재료를 삭제합니다.
     */
    public void removeChildren() {
        List<Ingredient> tmpChildren = new ArrayList<>(this.children);

        for (Ingredient child : tmpChildren) {
            removeChild(child);
        }
    }

    /**
     * 재료 기본 생성자.
     */
    public Ingredient() {}

    /**
     * 상위 계층(카테고리)에 특정 재료가 존재하는지 확인합니다.
     * @param target  상위 계층에 존재하는지 확인할 특정 재료.
     * @return 상위 계층에 특정 재료가 존재하는지 여부.
     */
    public boolean isCategoryOf(Ingredient target) {
        Ingredient current = this.category;

        while (current != null) {
            if (current == target) return true;
            current = current.category;
        }

        return false;
    }

    /**
     * 하위 계층에 특정 재료가 존재하는지 확인합니다.
     * @param target 하위 계층에 존재하는지 확인할 특정 재료.
     * @return 하위 계층에 특정 재료가 존재하는지 여부.
     */
    public boolean isChildOf(Ingredient target) {
        // 모든 하위 재료를 탐색합니다.
        for (Ingredient child : this.children) {
            // 하위 계층에 찾고자 하는 재료가 존재하는 경우, true를 반환하고 하위 재료 탐색을 중단합니다.
            if (child == target) return true;

            // 현재 탐색 중인 재료를 기준으로 모든 하위 재료를 탐색합니다.
            if (child.isChildOf(target)) return true;
        }

        // 모든 하위 재료를 탐색했는데도 불구하고, 특정 재료를 찾지 못했다면 false를 반환합니다.
        return false;
    }

}
