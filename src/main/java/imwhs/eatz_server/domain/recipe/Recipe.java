package imwhs.eatz_server.domain.recipe;

import imwhs.eatz_server.common.BaseEntity;
import imwhs.eatz_server.domain.IngredientRecipe;
import imwhs.eatz_server.domain.eatzuser.EatzUser;
import imwhs.eatz_server.dto.recipe.RecipeUpdateDto;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Recipe 클래스입니다.
 * <p>
 *     레시피 정보를 저장, 관리하는 엔티티 클래스입니다.
 * </p>
 */
@Getter
@EqualsAndHashCode(of = "id")
@Entity
public class Recipe extends BaseEntity {

    @Id @GeneratedValue
    @Column(name = "recipe_id")
    private Long id;

    /**
     * 레시피를 등록한 사용자.
     */
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private EatzUser user;

    /**
     * 제목.
     * <p>
     * 최대 100자 길이의 문장까지 저장할 수 있습니다.
     * </p>
     */
    @NotNull
    @Column(length = 100, nullable = false)
    private String title;

    /**
     * URL.
     * <p>
     * 최대 1000자 길이의 문장까지 저장할 수 있습니다.
     * </p>
     */
    @NotNull
    @Column(length = 1000)
    private String url;

    /**
     * 대표 이미지 URL.
     */
    private String imageUrl;

    /**
     * 설명.
     */
    private String description;

    /**
     * 재료.
     */
    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<IngredientRecipe> ingredientRecipes = new ArrayList<>();

    /**
     * 카테고리.
     */
    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RecipeCategory> recipeCategories = new ArrayList<>();

    /**
     * Recipe, EatzUser의 양방향 연관 관계 설정 메서드.
     * @param user 레시피를 등록한 사용자.
     */
    public void setUser(EatzUser user) {
        this.user = user;
        user.getRecipes().add(this);
    }

    protected Recipe() {}

    /**
     * Recipe 팩토리 메서드.
     * <p>
     * 엔티티의 주요 필드와 함께 Recipe 엔티티 인스턴스를 생성합니다.
     * </p>
     * @param user 레시피를 등록하려는 사용자. 필수 항목입니다.
     * @param title 레시피 제목. 필수 항목입니다.
     * @param url 레시피 URL. 필수 항목입니다.
     * @param imageUrl 레시피 대표 이미지 URL.
     * @param description 레시피 설명.
     * @return Recipe 엔티티 객체.
     * @throws IllegalArgumentException Recipe 엔티티의 필수 항목인 제목 또는 URL이 null이거나 빈 값일 경우.
     */
    public static Recipe create(
            EatzUser user,
            String title,
            String url,
            String imageUrl,
            String description
    ) {
        if (Objects.isNull(user)) {
            throw new IllegalArgumentException("레시피를 등록하려는 사용자 정보가 없습니다.");
        }
        if (Objects.isNull(title) || title.isEmpty()) {
            throw new IllegalArgumentException("레시피 제목은 필수 항목입니다.");
        }
        if (Objects.isNull(url) || url.isEmpty()) {
            throw new IllegalArgumentException("레시피 URL은 필수 항목입니다.");
        }

        Recipe recipe = new Recipe();
        recipe.setUser(user);
        recipe.title = title;
        recipe.url = url;
        recipe.imageUrl = imageUrl;
        recipe.description = description;

        return recipe;
    }

    /**
     * Recipe의 주요 필드를 업데이트합니다.
     * @param title
     * @param url
     * @param imageUrl
     * @param description
     */
    public void update(String title, String url, String imageUrl, String description) {
        if (Objects.isNull(title) || title.isEmpty()) {
            throw new IllegalArgumentException("레시피 제목은 필수 항목입니다.");
        }
        if (Objects.isNull(url) || url.isEmpty()) {
            throw new IllegalArgumentException("레시피 URL은 필수 항목입니다.");
        }

        this.title = title;
        this.url = url;
        this.imageUrl = imageUrl;
        this.description = description;
    }

    public void addIngredientRecipe(IngredientRecipe ingredientRecipe) {
        this.ingredientRecipes.add(ingredientRecipe);
    }

    public void addRecipeCategory(RecipeCategory recipeCategory) {
        this.recipeCategories.add(recipeCategory);
    }

    public void clearAllIngredientRecipes() {
        this.ingredientRecipes.clear();
    }

    public void clearAllRecipeCategories() {
        this.recipeCategories.clear();
    }

}
