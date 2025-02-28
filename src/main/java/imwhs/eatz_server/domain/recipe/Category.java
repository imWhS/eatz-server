package imwhs.eatz_server.domain.recipe;

import imwhs.eatz_server.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
public class Category extends BaseEntity {

    @Id @GeneratedValue
    private Long id;

    private String name;

    private String description;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL) //TODO: orphanRemoval
    private List<RecipeCategory> recipeCategories = new ArrayList<>();

    /**
     * 카테고리를 생성합니다.
     * @param name 카테고리 이름.
     * @return 카테고리 인스턴스.
     */
    public static Category create(String name) {
        Category category = new Category();
        category.name = name;
        return category;
    }

    /**
     * 카테고리를 생성합니다.
     * @param name 카테고리 이름.
     * @param description 카테고리 설명.
     * @return 카테고리 인스턴스.
     */
    public static Category create(String name, String description) {
        Category category = new Category();
        category.name = name;
        category.description = description;
        return category;
    }

    /**
     * RecipeCategory를 통해 카테고리에 레시피를 추가합니다.
     * @param recipeCategory 카테고리에 추가할 레시피에 대한 RecipeCategory 인스턴스.
     */
    public void addRecipeCategory(RecipeCategory recipeCategory) {
        this.recipeCategories.add(recipeCategory);
        recipeCategory.setCategory(this);
    }

}
