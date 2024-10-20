package imwhs.eatz_server.domain;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class IngredientTest {

    private Ingredient parent1;
    private Ingredient parent2;
    private Ingredient child1;
    private Ingredient child2;
    private Ingredient grandchild;

    private Ingredient flour;
    private Ingredient sugar;
    private Ingredient salt;
    private Ingredient baking;

    @BeforeEach
    void setUp() {
        // 기본적인 재료 계층 구조 설정
        parent1 = new Ingredient("Parent");
        parent2 = new Ingredient("Parent");
        child1 = new Ingredient("Child 1");
        child2 = new Ingredient("Child 2");
        grandchild = new Ingredient("Grandchild 1");

        // 하위 관계 설정
        parent1.addChild(child1);
        parent1.addChild(child2);
        child1.addChild(grandchild);

        flour = new Ingredient("Flour");
        sugar = new Ingredient("Sugar");
        salt = new Ingredient("Salt");
        baking = new Ingredient("Baking");
    }

    @Test
    @DisplayName("isChildOf()가 자식 여부를 정상적으로 판별하는지 확인합니다.")
    void isChildOfTest() {
        // when, then
        Assertions.assertTrue(parent1.isChildOf(child1));
    }

    @Test
    @DisplayName("isCategoryOf()가 카테고리 여부를 정상적으로 판별하는지 확인합니다.")
    void isCategoryOfTest() {
        // when, then
        Assertions.assertTrue(child1.isCategoryOf(parent1));
    }

    @Test
    @DisplayName("isChildOf()가 직접적인 연관 관계가 아닌 자식(손자) 여부를 정상적으로 판별하는지 확인합니다.")
    void testIsChildOf_GrandChild() {
        // when, then
        Assertions.assertTrue(parent1.isChildOf(grandchild));
    }

    @Test
    @DisplayName("isCategoryOf()가 직접적인 연관 관계가 아닌 상위 재료의 카테고리 여부를 정상적으로 판별하는지 확인합니다.")
    void testIsCategoryOf_GrandChild() {
        // when, then
        Assertions.assertTrue(grandchild.isCategoryOf(parent1));
    }

    @Test
    @DisplayName("isChildOf()가 연관 관계가 아닌 재료 여부를 정상적으로 판별하는지 확인합니다.")
    void testIsChildOf_NotChild() {
        // when, then
        Assertions.assertFalse(child2.isChildOf(parent1));
    }

    @Test
    @DisplayName("isCategoryOf()가 연관 관계가 아닌 재료 여부를 정상적으로 판별하는지 확인합니다.")
    void testIsCategoryOf_NotCategory() {
        // when, then
        Assertions.assertFalse(parent1.isCategoryOf(child2));
    }

    @Test
    @DisplayName("isChildOf()가 자기 자신을 자식으로서 간주하지 않는지 확인합니다.")
    void testIsChildOf_Self() {
        // when, then
        Assertions.assertFalse(parent1.isChildOf(parent1));
    }

    @Test
    @DisplayName("isCategoryOf()가 자기 자신을 카테고리로서 간주하지 않는지 확인합니다.")
    void testIsCategoryOf_Self() {
        // when, then
        Assertions.assertFalse(parent1.isCategoryOf(parent1));
    }

    @Test
    @DisplayName("isChildOf()가 아무 연관 관계 없는 재료를 자식으로서 간주하지 않는지 확인합니다.")
    void testIsChildOf_Unrelated() {
        // given
        Ingredient ingredient = new Ingredient("Another child");

        // when
        parent2.addChild(ingredient);

        // then
        Assertions.assertFalse(parent1.isChildOf(ingredient));
    }

    @Test
    @DisplayName("isCategoryOf()가 아무 연관 관계 없는 재료를 카테고리로서 간주하지 않는지 확인합니다.")
    void testIsCategoryOf_Unrelated() {
        // given
        Ingredient ingredient = new Ingredient("Another child");

        // when
        parent2.addChild(ingredient);

        // then
        Assertions.assertFalse(parent1.isCategoryOf(ingredient));
    }

    @Test
    @DisplayName("addChild()를 통한 하위 재료 추가가 정상적으로 진행되는지 확인합니다.")
    void addChildTest() {
        // when
        flour.addChild(sugar);

        // when, then
        Assertions.assertTrue(flour.isChildOf(sugar)); // sugar가 flour의 하위 재료인지 확인
        Assertions.assertEquals(flour, sugar.getParent()); // sugar의 상위 재료가 flour인지 확인
    }

    @Test
    @DisplayName("setCategory()를 통한 카테고리 지정이 정상적으로 진행되는지 확인합니다.")
    void setCategoryTest() {
        // when
        sugar.setCategory(flour);

        // when, then
        assertTrue(sugar.isCategoryOf(flour)); // flour가 sugar의 카테고리인지 확인
        Assertions.assertEquals(flour, sugar.getParent()); // flour가 sugar의 상위 재료인지 확인
    }

    @Test
    @DisplayName("addChild()를 통해 자기 자신을 하위 재료로 추가하려고 할 때 예외가 발생하는지 확인합니다.")
    void addChildFailTest() {
        // when
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> flour.addChild(flour));

        // then
        Assertions.assertEquals("자신을 하위 재료로 추가할 수 없습니다.", exception.getMessage());
    }

    @Test
    @DisplayName("setCategory()를 통해 자기 자신을 카테고리로 지정하려고 할 때 예외가 발생하는지 확인합니다.")
    void setCategoryFailTest() {
        // when
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> sugar.setCategory(sugar));

        // then
        Assertions.assertEquals("자신을 카테고리로 지정할 수 없습니다.", exception.getMessage());
    }

    @Test
    @DisplayName("하위 재료 추가 시, 순환 참조 문제를 예방할 수 있는지 확인합니다.")
    void preventCircularRefTest() {
        // given
        flour.addChild(sugar);
        sugar.addChild(salt);

        // when
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> flour.setCategory(salt));

        // then
        Assertions.assertEquals("현재 재료의 하위 계층에 존재하는 재료를 카테고리로 지정할 수 없습니다.", exception.getMessage());
    }

    @Test
    @DisplayName("addChild()를 통해 카테고리를 변경했을 때, 재료 간 계층 관계가 유효한 구조를 유지하는지 확인합니다.")
    void changeCategoryTest_addChild() {
        // given
        flour.addChild(sugar);
        baking.addChild(sugar); // sugar를 baking의 하위로 이동

        // when, then
        Assertions.assertEquals(baking, sugar.getParent()); // sugar의 부모가 baking으로 바뀌었는지 확인
        Assertions.assertFalse(flour.getChildren().contains(sugar)); // sugar가 더 이상 flour의 하위에 있지 않은지 확인
    }

    @Test
    @DisplayName("setCategory()를 통해 카테고리를 변경했을 때, 재료 간 계층 관계가 유효한 구조를 유지하는지 확인합니다.")
    void changeCategoryTest_setCategory() {
        // given
        sugar.setCategory(flour);
        sugar.setCategory(baking);

        // when, then
        Assertions.assertEquals(baking, sugar.getParent()); // sugar의 부모가 baking으로 바뀌었는지 확인
        Assertions.assertFalse(flour.getChildren().contains(sugar)); // sugar가 더 이상 flour의 하위에 있지 않은지 확인
    }

    @Test
    @DisplayName("하위 재료가 정상적으로 제거되는지 확인합니다.")
    void testRemoveChild() {
        // given
        flour.addChild(sugar);

        // when
        flour.removeChild(sugar);

        // then
        Assertions.assertNull(sugar.getParent());
        Assertions.assertFalse(flour.getChildren().contains(sugar));

        Assertions.assertThrows(IllegalArgumentException.class, () -> flour.removeChild(sugar));
    }

    @Test
    @DisplayName("카테고리가 정상적으로 지정 해제되는지 확인합니다.")
    void testRemoveCategory() {
        // given
        flour.addChild(sugar);

        // when
        sugar.removeCategory();

        // then
        Assertions.assertNull(sugar.getParent());
        Assertions.assertFalse(flour.getChildren().contains(sugar));

        Assertions.assertThrows(IllegalArgumentException.class, sugar::removeCategory);
    }

}