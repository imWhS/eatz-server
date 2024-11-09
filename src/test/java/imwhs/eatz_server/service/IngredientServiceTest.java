package imwhs.eatz_server.service;

import imwhs.eatz_server.domain.Ingredient;
import imwhs.eatz_server.dto.ingredient.IngredientCreateDto;
import imwhs.eatz_server.dto.ingredient.IngredientUpdateDto;
import imwhs.eatz_server.exception.IngredientNotFoundException;
import imwhs.eatz_server.repository.ingredient.IngredientRepository;
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
        Long ingredientId = ingredientService.registerIngredient(dto);

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
        Long ingredientId = ingredientService.registerIngredient(dto);

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
    @DisplayName("유효하지 않은 카테고리를 지정해 재료를 추가하려고 할 때, 예외가 발생하는지 테스트합니다.")
    @Transactional
    void registerIngredientWithNonExistingCategoryTest() {
        // given
        String ingredientName = "apple";
        Long categoryId = 99999L;
        IngredientCreateDto dto = new IngredientCreateDto(ingredientName, categoryId);

        // when, then
        IngredientNotFoundException exception = Assertions.assertThrows(IngredientNotFoundException.class, () -> ingredientService.registerIngredient(dto));
        Assertions.assertEquals("id가 " + categoryId + "인 재료 엔티티를 찾을 수 없습니다.", exception.getMessage());
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

}