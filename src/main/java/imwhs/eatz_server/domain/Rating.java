package imwhs.eatz_server.domain;

import imwhs.eatz_server.common.BaseEntity;
import imwhs.eatz_server.domain.eatzuser.EatzUser;
import imwhs.eatz_server.domain.recipe.Recipe;
import imwhs.eatz_server.exception.EatzInvalidRequestArgumentException;
import imwhs.eatz_server.exception.UnauthorizedAccessException;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.Objects;

/**
 * 평가 정보를 정의하고, 연관 데이터를 관리하는 Rating 엔티티입니다.
 * <p> 도메인 성격 상, 연관 관계인 Recipe에 의존합니다. </p>
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@Entity
public class Rating extends BaseEntity {

    @EqualsAndHashCode.Include
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 작성자
     * <ul>
     *     <li> 필수 항목입니다. </li>
     * </ul>
     */
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private EatzUser author;

    /**
     * 평가가 달려 있는 레시피
     * <ul>
     *     <li> 필수 항목입니다. </li>
     *     <li> 연관 관계인 레시피 레코드가 삭제되면, 해당 Recipe의 ID가 외래 키인 Rating 레코드도 일괄 삭제합니다. </li>
     * </ul>
     */
    @NotNull
    @OnDelete(action = OnDeleteAction.CASCADE)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipe_id", nullable = false)
    private Recipe recipe;

    /**
     * 점수
     * <ul>
     *     <li> 필수 항목입니다. </li>
     *     <li> 1부터 5 사이의 자연수로만 설정할 수 있습니다. </li>
     * </ul>
     */
    @NotNull
    @Column(nullable = false)
    private Integer score;

    /**
     * 숨김 처리 여부
     * <ul>
     *     <li> 필수 항목입니다. </li>
     *     <li> 별도의 setter를 이용해 값을 설정해야 합니다.
     *          유효한 값을 명시적으로 설정하지 않을 경우, 기본 값인 false로 설정합니다. </li>
     *     <li> 데이터 누락(null)을 감지해 예외를 발생시키고, 데이터 누락 시 원시 타입인 boolean의 기본 값(false)에 의해
     *          의도치 않은 상태가 되는 것을 막기 위해 wrapping 타입인 Boolean 래퍼 타입을 사용합니다. </li>
     * </ul>
     */
    @NotNull
    @Column(nullable = false)
    private Boolean isHidden;

    /**
     * 내용
     */
    private String content;

    /**
     * Rating의 필수 필드 초기화 생성자입니다.
     */
    private Rating(EatzUser author, Recipe recipe, Integer score, Boolean isHidden, String content
    ) {
        this.author = author;
        this.recipe = recipe;
        this.score = score;
        setIsHidden(isHidden);
        this.content = content;
    }

    /**
     * 점수를 업데이트합니다.
     * @param requesterId 업데이트를 요청한 사용자의 ID
     * @param score 점수
     */
    public void updateScore(Long requesterId, Integer score) {
        validate();
        validateAuthor(requesterId);
        validateScore(score);
        this.score = score;
    }

    /**
     * 내용을 업데이트합니다.
     * @param requesterId 업데이트를 요청한 사용자의 ID
     * @param content 내용
     */
    public void updateContent(Long requesterId, String content) {
        validate();
        validateAuthor(requesterId);
        this.content = content;
    }

    /**
     * 숨김 처리 여부를 업데이트합니다.
     * @param requester 업데이트를 요청한 사용자의 EatzUser 엔티티
     * @param isHidden 숨김 처리 여부
     */
    public void updateIsHidden(EatzUser requester, Boolean isHidden) {
        validate();
        Long recipeAuthorId = getRecipe().getAuthor().getId();
        validateHidableByUser(requester, recipeAuthorId);
        setIsHidden(isHidden);
    }

    /**
     * 숨김 처리 여부를 업데이트합니다.
     * @param requesterId 업데이트를 요청한 사용자의 ID
     * @param isHidden 숨김 처리 여부
     */
    public void updateIsHidden(long requesterId, Boolean isHidden) {
        validate();
        Long recipeAuthorId = getRecipe().getAuthor().getId();
        validateHidableByUser(requesterId, recipeAuthorId);
        setIsHidden(isHidden);
    }

    /**
     * 평가를 업데이트합니다.
     * @param authorId 업데이트를 요청한 작성자의 ID
     * @param score 점수
     * @param content 내용
     * @param isHidden 숨김 처리 여부
     */
    public void update(Long authorId, Integer score, String content, Boolean isHidden) {
        validate();
        validateAuthor(authorId);

        updateScore(authorId, score);
        updateContent(authorId, content);
        updateIsHidden(authorId, isHidden);
    }

    /**
     * 평가를 업데이트합니다.
     * @param authorId 업데이트를 요청한 작성자의 ID
     * @param score 점수
     * @param content 내용
     */
    public void update(Long authorId, Integer score, String content) {
        this.update(authorId, score, content, this.getIsHidden());
    }

    /**
     * 평가를 삭제 처리합니다.
     * @param requester 삭제 처리를 요청한 사용자 엔티티
     */
    public void markAsDeleted(EatzUser requester) {
        validateNotDeleted();
        Long recipeAuthorId = getRecipe().getAuthor().getId();
        validateDeletableByUser(requester, recipeAuthorId);
        super.markAsDeleted();
    }

    /**
     * 숨김 처리 여부를 설정합니다.
     * <p> null일 경우, 도메인 기본 값인 false로 강제 설정합니다. </p>
     * @param isHidden 숨김 처리 여부
     */
    private void setIsHidden(Boolean isHidden) {
        this.isHidden = Objects.isNull(isHidden) ? Boolean.FALSE : isHidden;
    }

    /**
     * 삭제 처리 여부를 검증합니다.
     */
    public void validateNotDeleted() {
        if (isMarkedAsDeleted()) { throw new IllegalStateException("삭제 처리된 평가예요."); }
    }

    /**
     * 사용자의 작성자 여부를 검증합니다.
     * @param userId 사용자의 ID
     */
    public void validateAuthor(Long userId) {
        if (!author.getId().equals(userId)) { throw new UnauthorizedAccessException("평가의 작성자가 아니에요."); }
    }

    /**
     * 특정 사용자에 대해 해당 Rating 엔티티의 삭제 가능 여부를 검증합니다.
     * @param user 사용자의 EatzUser 엔티티
     * @param recipeAuthorId 평가가 달린 레시피의 작성자 ID.
     *                       평가가 달린 레시피의 작성자는 자신의 레시피에 달린 평가를 삭제할 수 있기 때문에,
     *                       삭제를 요청한 사용자가 레시피의 작성자인지 확인할 때 사용합니다.
     */
    public void validateDeletableByUser(EatzUser user, long recipeAuthorId) {
        if (!hasControlPermission(user, recipeAuthorId)) {
            throw new UnauthorizedAccessException("평가를 삭제할 권한이 없어요.");
        }
    }

    /**
     * 특정 사용자에 대해 해당 Rating 엔티티의 숨김 처리 가능 여부를 검증합니다.
     * @param user 사용자의 EatzUser 엔티티
     * @param recipeAuthorId 평가가 달린 레시피의 작성자 ID.
     *                       평가가 달린 레시피의 작성자는 자신의 레시피에 달린 평가를 삭제할 수 있기 때문에,
     *                       숨김 처리를 요청한 사용자가 레시피의 작성자인지 확인할 때 사용합니다.
     */
    public void validateHidableByUser(EatzUser user, long recipeAuthorId) {
        if (!hasControlPermission(user, recipeAuthorId)) {
            throw new UnauthorizedAccessException("평가를 숨김 처리할 권한이 없어요.");
        }
    }

    /**
     * 특정 사용자에 대해 해당 Rating 엔티티의 숨김 처리 가능 여부를 검증합니다.
     * @param userId 평가 작성자 또는 평가가 달려 있는 레시피 작성자의 ID
     * @param recipeAuthorId 평가가 달린 레시피의 작성자 ID.
     *                       평가가 달린 레시피의 작성자는 자신의 레시피에 달린 평가를 삭제할 수 있기 때문에,
     *                       삭제를 요청한 사용자가 레시피의 작성자인지 확인할 때 사용합니다.
     */
    public void validateHidableByUser(long userId, long recipeAuthorId) {
        if (!hasControlPermission(userId, recipeAuthorId)) {
            throw new UnauthorizedAccessException("평가를 숨김 처리할 권한이 없어요.");
        }
    }

    /**
     * 유효성을 검증합니다.
     */
    private void validate() {
        validateNotDeleted();
    }

    /**
     * 사용자의 평가를 제어할 수 있는 권한의 소유 여부를 확인합니다.
     * @param user 사용자의 EatzUser 엔티티
     * @param recipeAuthorId 평가가 달린 레시피의 작성자 ID.
     *                       평가가 달린 레시피의 작성자는 자신의 레시피에 달린 평가를 제어할 수 있기 때문에,
     *                       사용자가 레시피의 작성자인지 확인하기 위해 사용합니다.
     * @return 평가를 제어할 수 있는 권한의 소유 여부
     */
    private boolean hasControlPermission(EatzUser user, long recipeAuthorId) {
        boolean isRatingAuthor = author.getId().equals(user.getId());
        boolean isRecipeAuthor = user.getId().equals(recipeAuthorId);
        boolean isUserAdmin = user.isAdmin();

        return isRatingAuthor || isRecipeAuthor || isUserAdmin;
    }

    /**
     * 사용자의 평가를 제어할 수 있는 권한의 소유 여부를 확인합니다.
     * @param userId 사용자의 ID
     * @param recipeAuthorId 평가가 달린 레시피의 작성자 ID.
     *                       평가가 달린 레시피의 작성자는 자신의 레시피에 달린 평가를 제어할 수 있기 때문에,
     *                       사용자가 레시피의 작성자인지 확인하기 위해 사용합니다.
     * @return 평가를 제어할 수 있는 권한의 소유 여부
     */
    private boolean hasControlPermission(long userId, long recipeAuthorId) {
        boolean isRatingAuthor = author.getId().equals(userId);
        boolean isRecipeAuthor = userId == recipeAuthorId;

        return isRatingAuthor || isRecipeAuthor;
    }

    /**
     * Rating 엔티티 팩토리 메서드
     * <ul>
     *     <li> isHidden이 null일 경우, 생성자가 기본 값인 false로 자동 초기화합니다. </li>
     * </ul>
     * @param author 작성자의 EatzUser 엔티티. 필수 항목입니다.
     * @param recipe 레시피의 Recipe 엔티티. 필수 항목입니다.
     * @param score 점수. 필수 항목입니다.
     * @param content 내용
     * @param isHidden 숨김 처리 여부. 필수 항목입니다.
     * @return Rating 엔티티
     */
    public static Rating create(EatzUser author, Recipe recipe, Integer score, String content, Boolean isHidden) {
        validateScore(score);
        return new Rating(author, recipe, score, isHidden, content);
    }

    /**
     * Rating 엔티티 팩토리 메서드
     * <ul>
     *     <li> 숨김 처리 여부가 false인 Rating 엔티티를 생성합니다. </li>
     * </ul>
     * @param author 작성자의 EatzUser 엔티티. 필수 항목입니다.
     * @param recipe 레시피의 Recipe 엔티티. 필수 항목입니다.
     * @param score 점수. 필수 항목입니다.
     * @param content 내용
     * @return Rating 엔티티
     */
    public static Rating create(EatzUser author, Recipe recipe, Integer score, String content) {
        validateScore(score);
        return create(author, recipe, score, content, Boolean.FALSE);
    }

    /**
     * 점수의 유효성을 검증합니다.
     * @param score 평가 점수
     */
    public static void validateScore(Integer score) {
        if (score == null) { throw new EatzInvalidRequestArgumentException("필수 항목인 평가 점수가 비어 있어요."); }
        if (score < 1 || score > 5) { throw new EatzInvalidRequestArgumentException("평가 점수는 1부터 5 사이의 자연수여야 해요."); }
    }

}
