package imwhs.eatz_server.domain;

import imwhs.eatz_server.common.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.BatchSize;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 재료의 정보를 정의하는 Ingredient 엔티티입니다.<br/>
 * <ul>
 *      <li> 재료는 상위 재료에 속할 수 있고, 하위 재료를 가질 수도 있습니다. </li>
 *      <li> 이때, 해당 재료가 속해있는 상위 재료 또는, 하위 재료를 가지고 있는 재료는 상위 재료(parent)로서의 역할을 합니다.
 *           — Ex. apple이라는 재료가 fruit라는 상위 재료에 속한다면, fruit는 apple의 상위 재료이자 apple이 속한 상위 재료가 됩니다. </li>
 * </ul>
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@Entity
public class Ingredient extends BaseEntity {

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
     * 상위 재료
     * <ul>
     *     <li> 재료가 속해 있는 상위 재료로, 상위 재료에 해당합니다. </li>
     *     <li> 아무 상위 재료에도 속하지 않은 경우 null을 가집니다. </li>
     * </ul>
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Ingredient parent;

    /**
     * 하위 재료 컬렉션
     * <ul>
     *     <li> 상위 재료로서의 해당 재료에 속해 있는 다른 재료들의 목록입니다. </li>
     *     <li> 엔티티가 목록으로 조회되어질 때 N+1 문제를 예방하기 위해,
     *          지연 로딩 시점에 조회 범위 내 최대 30개 단위로 batch fetching 처리합니다. </li>
     * </ul>
     */
    @NotNull
    @OneToMany(mappedBy = "parent")
    @BatchSize(size = 30)
    private List<Ingredient> children = new ArrayList<>();

    private Ingredient(String name) {
        this.name = name;
    }

    /**
     * 이름을 업데이트합니다.
     * @param name 새 이름
     */
    public void updateName(String name) {
       if (name == null || name.isBlank()) {
           throw new IllegalArgumentException("변경하려는 재료의 이름이 비어 있어요.");
       }

       this.name = name;
    }

    /**
     * 상위 재료를 설정합니다.
     * 다른 재료를 상위 재료로 설정해, 해당 재료와 연관 관계를 맺습니다.
     * @param parent 상위 재료로 설정할 새 재료
     * @throws IllegalArgumentException 유효하지 않은 상위 재료를 설정한 경우
     */
    public void setParent(Ingredient parent) {
        if (parent == null || parent.isMarkedAsDeleted()) {
            throw new IllegalArgumentException("설정하려는 상위 재료가 유효하지 않아요.");
        }

        // 상위 재료로 설정할 재료의 유효성을 확인합니다.
        if (parent == this) {
            throw new IllegalArgumentException("재료 자신을 상위 재료로 설정할 수 없어요.");
        }

        // 상위 재료로 설정할 재료와 이미 연관 관계 설정이 되어 있는지 확인합니다.
        if (parent.children.contains(this) && this.parent == parent) {
            return;
        }

        // 상위 재료로 설정할 재료가 이미 하위 재료로 설정되어 있지는 않은지 확인합니다.
        if (hasChildAs(parent)) {
            throw new IllegalArgumentException("이미 재료의 하위 계층에 존재하는 재료를 상위 재료로 설정할 수 없어요.");
        }

        // 기존 상위 재료와의 연관 관계를 해제합니다.
        if (this.parent != null && this.parent != parent) {
            removeParent();
        }

        // 상위 재료로 설정할 재료와 연관 관계를 설정합니다.
        parent.children.add(this);
        this.parent = parent;
    }

    /**
     * 상위 재료를 제거합니다.
     */
    public void removeParent() {
        if (parent == null) return;
        parent.children.remove(this);
        parent = null;
    }

    /**
     * 하위 재료를 추가합니다.
     * 현재 재료를 상위 재료로서, 하위 재료와 연관 관계를 맺음으로써 특정 재료를 현재 재료의 하위 계층에 추가합니다.
     * @param child 하위 재료로서 추가할 재료
     * @throws IllegalArgumentException 유효하지 않은 재료를 하위 재료로서 설정하려는 경우
     */
    public void addChild(Ingredient child) {
        // 하위로 추가할 재료의 유효성을 확인합니다.
        if (child == null || child.isMarkedAsDeleted()) {
            throw new IllegalArgumentException("하위 계층에 추가할 새 재료가 유효하지 않아요.");
        }

        if (child == this) {
            throw new IllegalArgumentException("자신을 하위 재료로 추가할 수 없어요.");
        }

        // 이미 현재 재료를 상위 재료로서, 하위 재료와 연관 관계가 맺어져있지는 않은지 확인합니다.
        if (children.contains(child) && child.parent == this) return;

        // 순환 참조 방지를 위해, 하위 계층에 둘 재료가 이미 현재 재료의 상위 재료로서 설정돼있지는 않은지 확인합니다.
        if (hasParentAs(child)) {
            throw new IllegalArgumentException("현재 재료의 상위 재료로서 사용하고 있는 재료를 현재 재료의 하위 계층에 추가할 수 없어요.");
        }

        // 하위 계층에 둘 재료가 이미 다른 상위 재료와 연관 관계를 갖고 있지 않은지 확인합니다.
        // 이미 다른 상위 재료와 연관 관계를 갖고 있다면, 해당 재료 엔티티를 통해 상위 재료 설정을 해제해야 합니다.
        if (child.parent != null && !Objects.equals(child.parent, this)) {
            throw new IllegalArgumentException(
                    "하위 계층에 추가할 재료가 이미 " + child.parent.getName() + " 상위 재료에 포함되어 있어요.");
        }

        // 하위로 추가할 재료와 연관 관계를 설정합니다.
        child.parent = this;
        children.add(child);
    }

    /**
     * 1개 이상의 하위 재료를 한 번에 추가합니다.
     * @param children 하위 재료로 추가할 재료 목록
     */
    public void addChildren(List<Ingredient> children) {
        for (Ingredient child : children) {
            addChild(child);
        }
    }

    /**
     * 특정 하위 재료를 제거합니다.
     * @param child 제거할 재료
     */
    public void removeChild(Ingredient child) {
        if (child == null) {
            throw new IllegalArgumentException("제거하려는 재료가 유효하지 않아요.");
        }

        if (child.parent == null) {
            throw new IllegalArgumentException("제거하려는 재료가 어떠한 상위 재료에도 포함되어 있지 않아요.");
        }

        if (child.parent != this) {
            throw new IllegalArgumentException(
                    "제거하려는 재료가 다른 상위 재료(" + child.parent.getName() + ")에 포함되어 있어요.");
        }

        child.parent = null;
        children.remove(child);
    }

    /**
     * 모든 하위 재료를 제거합니다.
     */
    public void clearChildren() {
        // 하위 재료 제거 시, removeChild에 의해 원본 컬렉션 필드 children이 직접 수정됩니다.
        // children을 for로 순회하다가, children 컬렉션 구성 변경이 발생하면, ConcurrentModificationException이 발생할 수 있기 떄문에
        // children의 복사본을 사용해 순회합니다.
        List<Ingredient> tmpChildren = new ArrayList<>(children);

        for (Ingredient child : tmpChildren) {
            removeChild(child);
        }
    }

    /**
     * 상위 계층(상위 재료)에 특정 재료가 존재하는지 확인합니다.
     * @param target 상위 계층에 존재하는지 확인할 특정 재료
     * @return 상위 계층에 특정 재료가 존재하는지 여부
     */
    public boolean hasParentAs(Ingredient target) {
        Ingredient current = parent;

        while (current != null) {
            if (current == target) return true;
            current = current.parent;
        }

        return false;
    }

    /**
     * 하위 계층에 특정 재료가 존재하는지 확인합니다. //
     * @param target 하위 계층에 존재하는지 확인할 특정 재료
     * @return 하위 계층에 특정 재료가 존재하는지 여부
     */
    public boolean hasChildAs(Ingredient target) {
        // 모든 하위 재료를 탐색합니다.
        for (Ingredient child : children) {
            // 하위 계층에 찾고자 하는 재료가 존재하는 경우, true를 반환하고 하위 재료 탐색을 중단합니다.
            if (child == target) return true;

            // 현재 탐색 중인 재료를 기준으로 모든 하위 재료를 탐색합니다.
            if (child.hasChildAs(target)) return true;
        }

        // 모든 하위 재료를 탐색했는데도 불구하고, 특정 재료를 찾지 못했다면 false를 반환합니다.
        return false;
    }

    public static void validateName(String name) {
        if (Objects.isNull(name) || name.isBlank()) {
            throw new IllegalArgumentException("필수 항목인 재료의 이름이 비어 있어요.");
        }
    }

    /**
     * Ingredient 엔티티 팩토리 메서드
     * @param name 이름
     * @return Ingredient 엔티티
     */
    public static Ingredient create(String name) {
        validateName(name);
        return new Ingredient(name);
    }

}
