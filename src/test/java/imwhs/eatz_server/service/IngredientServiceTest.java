package imwhs.eatz_server.service;

import imwhs.eatz_server.domain.ingredient.Ingredient;
import imwhs.eatz_server.dto.ingredient.IngredientCreateDto;
import imwhs.eatz_server.dto.ingredient.IngredientWithCategoryChildDto;
import imwhs.eatz_server.dto.ingredient.IngredientTreeDto;
import imwhs.eatz_server.dto.ingredient.IngredientUpdateDto;
import imwhs.eatz_server.exception.IngredientNotFoundException;
import imwhs.eatz_server.repository.ingredient.IngredientRepository;
import imwhs.eatz_server.service.ingredient.IngredientService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Transactional(readOnly = true)
@SpringBootTest
class IngredientServiceTest {

    @Autowired
    private IngredientService ingredientService;

    @Autowired
    private IngredientRepository ingredientRepository;

    @Test
    @DisplayName("새 재료가 정상적으로 등록되는지 테스트합니다.")
    @Transactional
    void registerIngredientTest() {
        // given
        String ingredientName = "apple";
        IngredientCreateDto dto = new IngredientCreateDto(ingredientName);

        // when
        Long ingredientId = ingredientService.registerIngredient(dto.getName(), dto.getCategoryId(), dto.getChildIds());

        // then
        Optional<Ingredient> foundIngredient = ingredientRepository.findById(ingredientId);
        Assertions.assertTrue(foundIngredient.isPresent());
        Assertions.assertEquals(ingredientName, foundIngredient.get().getName());
    }

    @Test
    @DisplayName("새 재료가 정상적으로 등록되는지, 계층 구조 또한 정상적으로 반영되는지 테스트합니다.")
    @Transactional
    void registerIngredientWithCategoryAndChildrenTest() {
        // given
        String categoryName = "Category name";
        Ingredient category = new Ingredient(categoryName);
        ingredientRepository.save(category);

        String childName = "Child name";
        Ingredient child = new Ingredient(childName);
        ingredientRepository.save(child);

        String ingredientName = "apple";
        IngredientCreateDto dto = new IngredientCreateDto(ingredientName, category.getId(), Arrays.asList(child.getId()));

        // when
        Long ingredientId = ingredientService.registerIngredient(dto.getName(), dto.getCategoryId(), dto.getChildIds());

        // then
        Optional<Ingredient> foundIngredient = ingredientRepository.findById(ingredientId);
        Assertions.assertTrue(foundIngredient.isPresent());
        Assertions.assertEquals(ingredientName, foundIngredient.get().getName());

        Assertions.assertNotNull(foundIngredient.get().getCategory());
        Assertions.assertEquals(categoryName, foundIngredient.get().getCategory().getName());

        List<Long> foundChildIds = foundIngredient.get().getChildren().stream()
                .map(Ingredient::getId)
                .toList();
        Assertions.assertEquals(1, foundChildIds.size());
        Assertions.assertTrue(foundChildIds.contains(child.getId()));
    }

    @Test
    @DisplayName("유효하지 않은 카테고리를 설정해 재료를 추가하려고 할 때, 예외가 발생하는지 테스트합니다.")
    @Transactional
    void registerIngredientWithNonExistingCategoryTest() {
        // given
        String ingredientName = "apple";
        Long categoryId = 99999L;
        IngredientCreateDto dto = new IngredientCreateDto(ingredientName, categoryId);

        // when, then
        IngredientNotFoundException exception = Assertions.assertThrows(IngredientNotFoundException.class, () -> ingredientService.registerIngredient(dto.getName(), dto.getCategoryId(), dto.getChildIds()));
        Assertions.assertEquals("id가 " + categoryId + "인 재료를 찾을 수 없습니다.", exception.getMessage());
    }

    @Test
    @DisplayName("재료가 정상적으로 수정되는지 테스트합니다.")
    @Transactional
    void updateIngredientTest() {
        // given
        String categoryName = "Category name";
        Ingredient category = new Ingredient(categoryName);
        ingredientRepository.save(category);

        String updatedCategoryName = "Updated category name";
        Ingredient updatedCategory = new Ingredient(updatedCategoryName);
        ingredientRepository.save(updatedCategory);

        String childName = "Child name";
        Ingredient child = new Ingredient(childName);
        ingredientRepository.save(child);

        String updatedChildName = "Updated child name";
        Ingredient updatedChild = new Ingredient(updatedChildName);
        ingredientRepository.save(updatedChild);

        String ingredientName = "apple";
        Ingredient ingredient = new Ingredient(ingredientName);
        ingredientRepository.save(ingredient);
        ingredient.setCategory(category);
        ingredient.addChild(child);
        Long ingredientId = ingredient.getId();

        String updatedIngredientName = "banana";

        IngredientUpdateDto dto = new IngredientUpdateDto(
                ingredientId,
                updatedIngredientName,
                updatedCategory.getId(),
                Arrays.asList(updatedChild.getId()));

        // when
        ingredientService.updateIngredient(dto);

        // then
        Optional<Ingredient> foundIngredient = ingredientRepository.findById(ingredientId);
        Assertions.assertTrue(foundIngredient.isPresent());
        Assertions.assertEquals(updatedIngredientName, foundIngredient.get().getName());
        Assertions.assertEquals(updatedCategory.getId(), foundIngredient.get().getCategory().getId());
        Assertions.assertEquals(updatedCategory.getName(), foundIngredient.get().getCategory().getName());
        Assertions.assertEquals(updatedChildName, foundIngredient.get().getChildren().get(0).getName());
    }

    @Test
    @DisplayName("재료가 정상적으로 삭제되는지 테스트합니다.")
    @Transactional
    void deleteIngredientTest() {
        // given
        String categoryName = "Category name";
        Ingredient category = new Ingredient(categoryName);
        ingredientRepository.save(category);

        String child1Name = "Child1 name";
        Ingredient child = new Ingredient(child1Name);
        ingredientRepository.save(child);

        String child2Name = "Child2 name";
        Ingredient child2 = new Ingredient(child2Name);
        ingredientRepository.save(child2);

        String ingredientName = "apple";
        Ingredient ingredient = new Ingredient(ingredientName);
        ingredientRepository.save(ingredient);
        Long ingredientId = ingredient.getId();
        ingredient.setCategory(category);
        ingredient.addChild(child);
        ingredient.addChild(child2);

        // when
        ingredientService.deleteIngredient(ingredient.getId());

        // then: category는 children이 없어야 하며, 각 child는 category가 지정되어 있지 않아야 합니다.
        Assertions.assertTrue(category.getChildren().isEmpty());
        Assertions.assertNull(child.getCategory());
        Assertions.assertNull(child2.getCategory());
        Assertions.assertFalse(ingredientRepository.existsById(ingredientId));
        Assertions.assertEquals(3, ingredientRepository.count());
    }

    @Test
    @DisplayName("카테고리에 속해 있고, 하위 재료를 가지는 단일 재료가 식별자로 정상적으로 조회되는지 테스트합니다.")
    @Transactional
    void findByIdTest() {
        // given
        String categoryName = "Category name";
        Ingredient category = new Ingredient(categoryName);
        ingredientRepository.save(category);

        String child1Name = "Child1 name";
        Ingredient child1 = new Ingredient(child1Name);
        ingredientRepository.save(child1);

        String child2Name = "Child2 name";
        Ingredient child2 = new Ingredient(child2Name);
        ingredientRepository.save(child2);

        String ingredientName = "apple";
        Ingredient ingredient = new Ingredient(ingredientName);
        ingredientRepository.save(ingredient);
        ingredient.setCategory(category);
        ingredient.addChild(child1);
        ingredient.addChild(child2);

        // when
        IngredientWithCategoryChildDto foundIngredient = ingredientService.findIngredient(ingredient.getId());

        // then
        Assertions.assertNotNull(foundIngredient);
        Assertions.assertEquals(ingredientName, foundIngredient.getName());
        Assertions.assertNotNull(foundIngredient.getCategory());
        Assertions.assertEquals(foundIngredient.getCategory().getCategoryName(), categoryName);
        Assertions.assertFalse(foundIngredient.getChildren().isEmpty());
        Assertions.assertEquals(2, foundIngredient.getChildren().size());
        Assertions.assertEquals(child1Name, foundIngredient.getChildren().get(0).getChildName());
        Assertions.assertEquals(child2Name, foundIngredient.getChildren().get(1).getChildName());
    }

    @Test
    @Transactional
    @DisplayName("어느 카테고리에도 속하지 않고, 하위 재료를 가지는 단일 재료가 식별자로 정상적으로 조회되는지 테스트합니다.")
    void findByIdWithoutCategoryTest() {
        // given
        String child1Name = "Child1 name";
        Ingredient child = new Ingredient(child1Name);
        ingredientRepository.save(child);

        String child2Name = "Child2 name";
        Ingredient child2 = new Ingredient(child2Name);
        ingredientRepository.save(child2);

        String ingredientName = "apple";
        Ingredient ingredient = new Ingredient(ingredientName);
        ingredientRepository.save(ingredient);
        ingredient.addChild(child);
        ingredient.addChild(child2);

        // when
        IngredientWithCategoryChildDto foundIngredient = ingredientService.findIngredient(ingredient.getId());

        // then
        Assertions.assertNotNull(foundIngredient);
        Assertions.assertEquals(ingredientName, foundIngredient.getName());
        Assertions.assertNull(foundIngredient.getCategory());
        Assertions.assertFalse(foundIngredient.getChildren().isEmpty());
        Assertions.assertEquals(2, foundIngredient.getChildren().size());
        Assertions.assertEquals(child1Name, foundIngredient.getChildren().get(0).getChildName());
        Assertions.assertEquals(child2Name, foundIngredient.getChildren().get(1).getChildName());
    }

    @Test
    @Transactional
    @DisplayName("어느 카테고리에도 속하지 않고, 하위 재료도 가지지 않는 단일 재료가 식별자로 정상적으로 조회되는지 테스트합니다.")
    void findByIdWithoutCategoryChildrenTest() {
        // given
        String ingredientName = "apple";
        Ingredient ingredient = new Ingredient(ingredientName);
        ingredientRepository.save(ingredient);

        // when
        IngredientWithCategoryChildDto foundIngredient = ingredientService.findIngredient(ingredient.getId());

        // then
        Assertions.assertNotNull(foundIngredient);
        Assertions.assertEquals(ingredientName, foundIngredient.getName());
        Assertions.assertNull(foundIngredient.getCategory());
        Assertions.assertTrue(foundIngredient.getChildren().isEmpty());
    }
    
    @Test
    @Transactional
    @DisplayName("모든 재료가 계층 구조로 조회되는지 테스트합니다.")
    void findIngredientWithAllChildrenTreeTest() {
        // given
        Ingredient root = new Ingredient("모든 재료");
        ingredientRepository.save(root);

        Ingredient meat = new Ingredient("육류");
        ingredientRepository.save(meat);
        meat.setCategory(root);

        Ingredient seafood = new Ingredient("해산물");
        ingredientRepository.save(seafood);
        seafood.setCategory(root);

        Ingredient pork = new Ingredient("돼지고기");
        ingredientRepository.save(pork);
        pork.setCategory(meat);

        Ingredient porkSub1 = new Ingredient("앞다리살");
        ingredientRepository.save(porkSub1);
        porkSub1.setCategory(pork);

        Ingredient porkSub2 = new Ingredient("삼겹살");
        ingredientRepository.save(porkSub2);
        porkSub2.setCategory(pork);

        Ingredient beef = new Ingredient("소고기");
        ingredientRepository.save(beef);
        beef.setCategory(meat);

        Ingredient beefSub1 = new Ingredient("업진살");
        ingredientRepository.save(beefSub1);
        beefSub1.setCategory(beef);

        // when
        IngredientTreeDto rootOfIngredientTree = ingredientService.findIngredientTree(root.getId());

        // then
        Assertions.assertNotNull(rootOfIngredientTree);

        Assertions.assertNull(rootOfIngredientTree.getCategory());

        List<IngredientTreeDto> childrenOfRoot = rootOfIngredientTree.getChildren();
        Assertions.assertTrue(childrenOfRoot.containsAll(
                Arrays.asList(new IngredientTreeDto(meat), new IngredientTreeDto(seafood))));

        IngredientTreeDto meatInTree = childrenOfRoot.get(0);
        Assertions.assertEquals(meat.getName(), meatInTree.getName());

        IngredientTreeDto seafoodInTree = childrenOfRoot.get(1);
        Assertions.assertEquals(seafood.getName(), seafoodInTree.getName());

        List<IngredientTreeDto> childrenOfMeat = meatInTree.getChildren();
        Assertions.assertTrue(childrenOfMeat.containsAll(
                Arrays.asList(new IngredientTreeDto(pork), new IngredientTreeDto(beef))));

        IngredientTreeDto porkInTree = childrenOfMeat.get(0);
        Assertions.assertEquals(pork.getName(), porkInTree.getName());

        List<IngredientTreeDto> childrenOfPork = porkInTree.getChildren();
        Assertions.assertTrue(childrenOfPork.containsAll(
                Arrays.asList(new IngredientTreeDto(porkSub1), new IngredientTreeDto(porkSub2))));

        IngredientTreeDto beefInTree = childrenOfMeat.get(1);
        Assertions.assertEquals(beef.getName(), beefInTree.getName());

        List<IngredientTreeDto> childrenOfBeef = beefInTree.getChildren();
        Assertions.assertTrue(childrenOfBeef.contains(new IngredientTreeDto(beefSub1)));
    }

}