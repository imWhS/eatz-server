package imwhs.eatz_server.domain.recipe;

import imwhs.eatz_server.common.BaseEntity;
import imwhs.eatz_server.domain.Ingredient;
import imwhs.eatz_server.domain.Kitchenware;
import imwhs.eatz_server.domain.Tag;
import imwhs.eatz_server.domain.eatzuser.EatzUser;
import imwhs.eatz_server.domain.liked.LikedRecipe;
import imwhs.eatz_server.exception.UnauthorizedAccessException;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.BatchSize;

import java.util.*;

/**
 * 레시피 정보를 정의하고, 연관 데이터를 관리하는 Recipe 엔티티입니다.
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@Entity
public class Recipe extends BaseEntity {

    @EqualsAndHashCode.Include
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 작성자
     */
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private EatzUser author;

    /**
     * 제목
     * <ul>
     *     <li> 필수 항목입니다. </li>
     *     <li> 최대 100자 길이의 문장까지 저장할 수 있습니다. </li>
     * </ul>
     */
    @NotNull
    @Column(length = 100, nullable = false)
    private String title;

    /**
     * URL
     * <ul>
     *     <li> 필수 항목입니다. </li>
     *     <li> 최대 1000자 길이의 문장까지 저장할 수 있습니다. </li>
     * </ul>
     */
    @NotNull
    @Column(length = 1000)
    private String url;

    /**
     * 대표 이미지 URL
     * <ul>
     *     <li> 필수 항목입니다. </li>
     * </ul>
     */
    @NotNull
    @Column(nullable = false)
    private String imageUrl;

    /**
     * 요리 시간
     * <ul>
     *     <li> 시간 단위로 '초'를 사용합니다. </li>
     *     <li> 필수 항목입니다. </li>
     *     <li> 의도된 값과 단순 누락(null)을 구분하기 위해, 필수 항목이지만 wrapper 타입을 사용해 @NotNull을 통해 유효성을 검증합니다. </li>
     * </ul>
     */
    @NotNull
    @Column(nullable = false)
    private Integer cookingTime;

    /**
     * 1회 제공량
     * <ul>
     *     <li> 필수 항목입니다. </li>
     *     <li> 의도된 값과 단순 데이터 누락(null)을 구분하기 위해,
     *          필수 항목이지만 wrapper 타입을 사용해 @NotNull을 통해 유효성을 검증합니다. </li>
     * </ul>
     */
    @NotNull
    @Column(nullable = false)
    private Integer servings;

    /**
     * 댓글 기능 사용 여부
     * <ul>
     *     <li> 필수 항목입니다. </li>
     *     <li> 별도의 setter를 이용해 값을 설정해야 합니다.
     *          유효한 값을 명시적으로 설정하지 않을 경우, 기본 값인 true로 설정합니다. </li>
     *     <li> 데이터 누락(null)을 감지해 예외를 발생시키고, 데이터 누락 시 기본 값인 true가 아닌,
     *          원시 타입인 boolean의 기본 값인 false으로 왜곡되어 의도치 않게 댓글 기능이 미사용 상태가 되는 것을 막기 위해
     *          wrapping 타입인 Boolean 래퍼 타입을 사용합니다. </li>
     * </ul>
     */
    @NotNull
    @Column(nullable = false)
    private Boolean isCommentEnabled;

    /**
     * 재료 목록
     * <ul>
     *     <li> 레시피 요리 시 요구 재료 목록입니다. </li>
     *     <li> 엔티티가 목록으로 조회되어질 때 N+1 문제를 예방하기 위해,
     *          지연 로딩 시점에 조회 범위 내 최대 20개 단위로 batch fetching 처리합니다. </li>
     *     <li> Recipe를 삭제할 때 관련 RecipeIngredient도 모두 삭제합니다. 또한, 컬렉션에서 특정 RecipeIngredient가 제거되어
     *          Recipe와 연관 관계를 맺지 않을 경우, 해당 RecipeIngredient도 삭제합니다. </li>
     * </ul>
     */
    @BatchSize(size = 20)
    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<RecipeIngredient> ingredients = new HashSet<>();

    /**
     * 도구 목록
     * <ul>
     *     <li> 레시피 요리 시 요구 도구 목록입니다. </li>
     *     <li> 엔티티가 목록으로 조회되어질 때 N+1 문제를 예방하기 위해,
     *          지연 로딩 시점에 조회 범위 내 최대 20개 단위로 batch fetching 처리합니다. </li>
     *     <li> Recipe를 삭제할 때 관련 RecipeKitchenware도 모두 삭제합니다. 또한, 컬렉션에서 특정 RecipeKitchenware가 제거되어
     *          Recipe와 연관 관계를 맺지 않을 경우, 해당 RecipeKitchenware도 삭제합니다. </li>
     * </ul>
     */
    @BatchSize(size = 20)
    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<RecipeKitchenware> kitchenwares = new HashSet<>();

    /**
     * 태그 목록
     * <ul>
     *     <li> 레시피에 추가된 태그 목록입니다. </li>
     *     <li> 엔티티가 목록으로 조회되어질 때 N+1 문제를 예방하기 위해,
     *          지연 로딩 시점에 조회 범위 내 최대 20개 단위로 batch fetching 처리합니다. </li>
     *     <li> Recipe를 삭제할 때 관련 RecipeTag도 모두 삭제합니다. 또한, 컬렉션에서 특정 RecipeTag가 제거되어
     *          Recipe와 연관 관계를 맺지 않을 경우, 해당 RecipeTag도 삭제합니다. </li>
     * </ul>
     */
    @BatchSize(size = 20)
    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<RecipeTag> tags = new HashSet<>();

    /**
     * 설명
     */
    private String description;

    /**
     * 준비 시간
     * <ul>
     *     <li> 시간 단위로 '초'를 사용합니다. </li>
     * </ul>
     */
    private Integer prepTime;

    /**
     * 창작자 이름
     */
    private String creatorName;

    /**
     * 창작자 관련 URL
     */
    private String creatorUrl;

    /**
     * 조회 수
     * <ul>
     *     <li> 필수 항목입니다. </li>
     *     <li> 기본 값인 0으로 초기화합니다. </li>
     *     <li> 서버 애플리케이션 내부에서 자체적으로 값이 할당되는 도메인 특성 상, 엔티티 생성 시 반드시 초기 값을 가지며,
     *          데이터가 누락될 수 없기 때문에 wrapping 타입을 사용하지 않습니다. </li>
     * </ul>
     */
    private long viewCount = 0;

    /**
     * 조회 수
     * <ul>
     *     <li> 필수 항목입니다. </li>
     *     <li> 기본 값인 0으로 초기화합니다. </li>
     *     <li> 서버 애플리케이션 내부에서 자체적으로 값이 할당되는 도메인 특성 상, 엔티티 생성 시 반드시 초기 값을 가지며,
     *          데이터가 누락될 수 없기 때문에 wrapping 타입을 사용하지 않습니다. </li>
     * </ul>
     */
    private long outboundCount = 0;

    /**
     * Recipe의 주요 필드 초기화 생성자입니다.
     */
    private Recipe(
            EatzUser author,
            String title,
            String url,
            String imageUrl,
            Integer cookingTime,
            Integer servings,
            Boolean isCommentEnabled,
            String description,
            Integer prepTime,
            String creatorName,
            String creatorUrl) {
        this.author = author;
        this.title = title;
        this.url = url;
        this.imageUrl = imageUrl;
        this.cookingTime = cookingTime;
        this.servings = servings;
        setIsCommentEnabled(isCommentEnabled);
        this.description = description;
        this.prepTime = prepTime;
        this.creatorName = creatorName;
        this.creatorUrl = creatorUrl;
    }

    /**
     * Recipe의 주요 필드를 업데이트합니다.
     * <ul>
     *     <li> 삭제 처리 여부를 먼저 확인합니다. </li>
     *     <li> 필수 항목(제목, URL, 대표 이미지 URL, 요리 시간, 1회 제공량)의 유효성을 검증합니다. </li>
     *     <li> 댓글 기능 사용 여부도 필수 항목이지만, null일 경우 기본 값인 true로 자동 초기화하기 때문에 유효성을 검증하지 않습니다. </li>
     * </ul>
     *
     * @param userId 업데이트를 요청한 사용자 ID
     * @param title 제목. 필수 항목입니다.
     * @param url URL. 필수 항목입니다.
     * @param imageUrl 대표 이미지 URL. 필수 항목입니다.
     * @param cookingTime 요리 시간. 필수 항목입니다. 시간 단위로 '초'를 사용합니다.
     * @param servings 1회 제공량. 필수 항목입니다.
     * @param isCommentEnabled 댓글 기능 사용 여부. 필수 항목이지만, null일 경우 기본 값인 true로 자동 설정합니다.
     * @param description 설명
     * @param prepTime 준비 시간. 시간 단위로 '초'를 사용합니다.
     * @param creatorName 창작자 이름
     * @param creatorUrl 창작자 관련 URL
     */
    public void update(
            Long userId,
            String title,
            String url,
            String imageUrl,
            Integer cookingTime,
            Integer servings,
            Boolean isCommentEnabled,
            String description,
            Integer prepTime,
            String creatorName,
            String creatorUrl) {
        validate();
        verifyAuthor(userId);
        validateRequiredFields(this.author, title, url, imageUrl, cookingTime, servings);
//        validatePrepTime(prepTime);
        this.title = title;
        this.url = url;
        this.imageUrl = imageUrl;
        this.cookingTime = cookingTime;
        this.servings = servings;
        setIsCommentEnabled(isCommentEnabled);
        this.description = description;
        this.prepTime = prepTime;
        this.creatorName = creatorName;
        this.creatorUrl = creatorUrl;
    }

    /**
     * 레시피를 삭제 처리합니다.
     * @param user 삭제 처리를 요청한 사용자의 EatzUser 엔티티
     */
    public void markAsDeleted(EatzUser user) {
        validate();
        validateDeletableByUser(user);
        super.markAsDeleted();
    }

    /**
     * 댓글 기능 사용 여부를 설정합니다.
     * <p> null일 경우, 도메인 기본 값인 true로 강제 설정합니다. </p>
     * @param isCommentEnabled 댓글 기능 사용 여부
     */
    private void setIsCommentEnabled(Boolean isCommentEnabled) {
        this.isCommentEnabled = Objects.isNull(isCommentEnabled) ? Boolean.TRUE : isCommentEnabled;
    }

    /**
     * 레시피에 재료를 추가하기 위해 RecipeIngredient 엔티티를 생성한 후 연관 관계를 설정합니다.
     * <ul>
     *     <li> Cascade 옵션에 의해 Recipe 엔티티의 영속성 컨텍스트 상태가 RecipeIngredient 엔티티에 전이됩니다. </li>
     * </ul>
     * @param ingredient 재료의 Ingredient 엔티티
     */
    public void addIngredient(Ingredient ingredient) {
        validate();
        RecipeIngredient recipeIngredient = RecipeIngredient.create(this, ingredient);
        this.ingredients.add(recipeIngredient);
    }

    /**
     * 레시피에 도구를 추가하기 위해 RecipeKitchenware 엔티티를 생성한 후 연관 관계를 설정합니다.
     * <ul>
     *     <li> Cascade 옵션에 의해 Recipe 엔티티의 영속성 컨텍스트 상태가 RecipeKitchenware 엔티티에 전이됩니다. </li>
     * </ul>
     * @param kitchenware 도구의 Kitchenware 엔티티
     */
    public void addKitchenware(Kitchenware kitchenware) {
        validate();
        RecipeKitchenware recipeKitchenware = RecipeKitchenware.create(this, kitchenware);
        this.kitchenwares.add(recipeKitchenware);
    }

    /**
     * 레시피에 태그를 추가하기 위해 RecipeTag 엔티티를 생성한 후 연관 관계를 설정합니다.
     * <ul>
     *     <li> Cascade 옵션에 의해 Recipe 엔티티의 영속성 컨텍스트 상태가 RecipeTag 엔티티에 전이됩니다. </li>
     * </ul>
     * @param tag 태그의 Tag 엔티티
     */
    public void addTag(Tag tag) {
        validate();
        RecipeTag recipeTag = RecipeTag.create(this, tag);
        tags.add(recipeTag);
    }

    public void removeTag(Tag tag) {
        validate();
        tags.removeIf(
                recipeTag -> recipeTag.getTag().equals(tag));
    }

    public void removeRecipeTag(RecipeTag recipeTag) {
        validate();
        tags.remove(recipeTag);
    }

    public void clearAllIngredientRecipes() {
        validate();
        this.ingredients.clear();
    }

    public void clearAllKitchenwareRecipes() {
        validate();
        this.kitchenwares.clear();
    }

    public void clearAllRecipeTags() {
        validate();
        this.tags.clear();
    }

    public void increaseViewCount(int viewCount) {
        validate();
        this.viewCount += viewCount;
    }

    public void increaseOutboundCount(int outboundCount) {
        validate();
        this.outboundCount += outboundCount;
    }

    /**
     * 사용자의 작성자 여부를 검증합니다.
     * @param userId 사용자의 ID
     */
    public void verifyAuthor(Long userId) {
        if (!author.getId().equals(userId)) {
            throw new UnauthorizedAccessException("레시피의 작성자가 아니에요.");
        }
    }

    /**
     * 사용자의 레시피 삭제 가능 여부를 검증합니다.
     * @param user 사용자의 EatzUser 엔티티
     */
    public void validateDeletableByUser(EatzUser user) {
        boolean isRecipeAuthor = author.getId().equals(user.getId());
        boolean isUserAdmin = user.isAdmin();

        if (!(isRecipeAuthor || isUserAdmin)) {
            throw new UnauthorizedAccessException("레시피를 삭제할 권한이 없어요.");
        }
    }

    /**
     * 삭제 처리 여부를 검증합니다.
     */
    public void validateNotDeleted() {
        if (isMarkedAsDeleted()) {
            throw new IllegalStateException("삭제 처리된 레시피예요.");
        }
    }

    /**
     * 레시피의 유효성을 검증합니다.
     */
    private void validate() {
        validateNotDeleted();
    }

    /**
     * Recipe 엔티티 팩토리 메서드
     * <ul>
     *     <li> 필수 항목(작성한 사람, 제목, URL, 대표 이미지 URL, 요리 시간, 1회 제공량)의 유효성을 검증합니다. </li>
     *     <li> 댓글 기능 사용 여부도 필수 항목이지만, null일 경우
     *          생성자가 기본 값인 true로 자동 초기화하기 때문에 유효성을 검증하지 않습니다. </li>
     * </ul>
     *
     * @param author 작성자의 EatzUser 엔티티. 필수 항목입니다.
     * @param title 제목. 필수 항목입니다.
     * @param url URL. 필수 항목입니다.
     * @param imageUrl 대표 이미지 URL. 필수 항목입니다.
     * @param cookingTime 요리 시간. 필수 항목입니다. 시간 단위로 '초'를 사용합니다.
     * @param servings 1회 제공량. 필수 항목입니다.
     * @param isCommentEnabled 댓글 기능 사용 여부. 필수 항목이지만, null일 경우 기본 값인 true로 자동 초기화합니다.
     * @param description 설명
     * @param prepTime 준비 시간. 시간 단위로 '초'를 사용합니다.
     * @param creatorName 창작자 이름
     * @param creatorUrl 창작자 관련 URL
     * @return Recipe 엔티티
     */
    public static Recipe create(
            EatzUser author,
            String title,
            String url,
            String imageUrl,
            Integer cookingTime,
            Integer servings,
            Boolean isCommentEnabled,
            String description,
            Integer prepTime,
            String creatorName,
            String creatorUrl) {
        validateRequiredFields(author, title, url, imageUrl, cookingTime, servings);
//        validatePrepTime(prepTime);
        return new Recipe(
                author,
                title,
                url,
                imageUrl,
                cookingTime,
                servings,
                isCommentEnabled,
                description,
                prepTime,
                creatorName,
                creatorUrl);
    }

    /**
     * 모든 필수 항목 필드의 유효성을 검증합니다.
     * <p> 단, isCommentEnabled는 유효하지 않은 값이어도, 엔티티 팩토리 메서드 또는 update에서
     *     기본 값인 true로 초기화되기 때문에 유효성을 검증하지 않습니다. </p>
     */
    private static void validateRequiredFields(
            EatzUser author,
            String title,
            String url,
            String imageUrl,
            Integer cookingTime,
            Integer servings) {
        validateAuthor(author);
        validateTitle(title);
        validateUrl(url);
        // S3 구현 후 활성화 예정
//        validateImageUrl(imageUrl);
        validateCookingTime(cookingTime);
        validateServings(servings);
    }

    private static void validateServings(Integer servings) {
        if (Objects.isNull(servings) || servings < 1) {
            throw new IllegalArgumentException("필수 항목인 1회 제공량이 비어 있거나, 올바르지 않아요.");
        }
    }

    private static void validateCookingTime(Integer cookingTime) {
        if (Objects.isNull(cookingTime) || cookingTime <= 0) {
            throw new IllegalArgumentException("필수 항목인 요리 시간이 비어 있거나, 올바르지 않아요.");
        }
    }

//    private static void validatePrepTime(Integer prepTime) {
//        if (prepTime != null && prepTime <= 0) {
//            throw new IllegalArgumentException("준비 시간이 올바르지 않아요: " + prepTime);
//        }
//    }

    private static void validateImageUrl(String imageUrl) {
        if (Objects.isNull(imageUrl) || imageUrl.isBlank()) {
            throw new IllegalArgumentException("필수 항목인 레시피 대표 이미지 URL이 없어요.");
        }
    }

    private static void validateUrl(String url) {
        if (Objects.isNull(url) || url.isBlank()) {
            throw new IllegalArgumentException("필수 항목인 레시피 URL이 비어 있어요.");
        }
    }

    private static void validateTitle(String title) {
        if (Objects.isNull(title) || title.isBlank()) {
            throw new IllegalArgumentException("필수 항목인 레시피 제목이 비어 있어요.");
        }
    }

    private static void validateAuthor(EatzUser author) {
        if (Objects.isNull(author)) {
            throw new IllegalArgumentException("필수 항목인 레시피 작성자 정보가 비어 있어요.");
        }
    }


}
