package imwhs.eatz_server.service.recipe;

import imwhs.eatz_server.domain.Category;
import imwhs.eatz_server.domain.eatzuser.EatzUser;
import imwhs.eatz_server.domain.eatzuser.EatzUserRole;
import imwhs.eatz_server.domain.recipe.Recipe;
import imwhs.eatz_server.domain.recipe.RecipeCategory;
import imwhs.eatz_server.exception.CategoryNotFoundException;
import imwhs.eatz_server.exception.EatzUserNotFoundException;
import imwhs.eatz_server.exception.RecipeNotFoundException;
import imwhs.eatz_server.exception.UnauthorizedEatzUserException;
import imwhs.eatz_server.repository.eatzuser.EatzUserRepository;
import imwhs.eatz_server.repository.recipe.CategoryRepository;
import imwhs.eatz_server.repository.recipe.RecipeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    private final RecipeRepository recipeRepository;

    private final EatzUserRepository userRepository;

    /**
     * 새 카테고리를 등록합니다.
     * @param name 카테고리 이름.
     * @param description 카테고리 설명.
     * @return 생성된 카테고리 인스턴스.
     */
    @Transactional
    public Category register(String name, String description) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("카테고리 이름은 필수 항목이에요.");
        }

        if (categoryRepository.existsByName(name)) {
            throw new IllegalArgumentException("'" + name + "' 이름을 가진 카테고리가 이미 존재해요.");
        }

        Category category = Category.create(name, description);
        categoryRepository.save(category);

        return category;
    }

    /**
     * 새 카테고리를 등록한 후, 레시피에 추가합니다.
     * @param name 카테고리 이름.
     * @param description 카테고리 설명.
     * @param recipeId 레시피 ID.
     * @return 생성된 카테고리 인스턴스.
     */
    @Transactional
    public Category register(String name, String description, Long recipeId) {
        Category category = register(name, description);
        Recipe recipe = findRecipe(recipeId);
        addRecipeToCategory(category, recipe);
        return category;
    }

    /**
     * 카테고리를 업데이트합니다.
     * @param id 카테고리 ID.
     * @param userId 카테고리 업데이트를 요청한 사용자의 ID.
     * @param name 업데이트할 카테고리 이름.
     * @param description 업데이트할 카테고리 설명.
     */
    @Transactional
    public void update(Long id, Long userId, String name, String description) {
        validateUserAdminPermission(userId);
        Category category = categoryRepository.findById(id).orElseThrow(() -> new CategoryNotFoundException(id));
        category.update(name, description);
    }

    /**
     * 카테고리를 삭제합니다.
     * @param id 카테고리 ID.
     * @param userId 사용자 ID.
     */
    @Transactional
    public void delete(Long id, Long userId) {
        validateUserAdminPermission(userId);
        categoryRepository.deleteById(id);
    }

    @Transactional
    public void addRecipe(Long id, Long recipeId) {
        Category category = categoryRepository.findById(id).orElseThrow(() -> new CategoryNotFoundException(id));
        Recipe recipe = findRecipe(recipeId);
        addRecipeToCategory(category, recipe);
    }

    private void validateUserAdminPermission(Long userId) {
        EatzUser user = userRepository.findById(userId).orElseThrow(() -> new EatzUserNotFoundException(userId));
        if (!user.getEatzUserRole().equals(EatzUserRole.ROLE_ADMIN)) {
            throw new UnauthorizedEatzUserException("관리자 권한이 필요해요.");
        }
    }

    private Recipe findRecipe(Long id) {
        Recipe recipe = recipeRepository.findById(id).orElseThrow(
                () -> new RecipeNotFoundException(id));
        return recipe;
    }

    private void addRecipeToCategory(Category category, Recipe recipe) {
        RecipeCategory recipeCategory = RecipeCategory.of(recipe, category);
        category.addRecipeCategory(recipeCategory);
        categoryRepository.save(category);
    }

}
