package imwhs.eatz_server.domain;

import imwhs.eatz_server.common.BaseEntity;
import imwhs.eatz_server.domain.eatzuser.EatzUser;
import imwhs.eatz_server.domain.recipe.Recipe;
import imwhs.eatz_server.exception.UnauthorizedAccessException;
import imwhs.eatz_server.exception.UnauthorizedEatzUserException;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.Objects;

/**
 * 댓글 정보를 정의하고, 연관 데이터를 관리하는 Comment 엔티티입니다.
 * <p> 도메인 성격 상, 연관 관계인 Recipe에 의존합니다. </p>
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@Entity
public class Comment extends BaseEntity {

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
     * 댓글이 달려 있는 레시피
     * <ul>
     *     <li> 필수 항목입니다. </li>
     *     <li> 연관 관계인 레시피 레코드가 삭제되면, 해당 Recipe의 ID가 외래 키인 Comment 레코드도 일괄 삭제합니다. </li>
     * </ul>
     */
    @NotNull
    @OnDelete(action = OnDeleteAction.CASCADE)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipe_id", nullable = false)
    private Recipe recipe;

    /**
     * 내용
     * <ul>
     *     <li> 필수 항목입니다. </li>
     * </ul>
     */
    @NotNull
    private String content;

    /**
     * 숨김 처리 여부
     * <ul>
     *     <li> 필수 항목입니다. </li>
     *     <li> 별도의 setter를 이용해 값을 설정해야 합니다.
     *          유효한 값을 명시적으로 설정하지 않을 경우, 기본 값인 false로 설정합니다. </li>
     *     <li> 데이터 누락(null)를 감지해 예외를 발생시키고, 데이터 누락 시 원시 타입인 boolean의 기본 값(false)에 의해
     *          의도치 않은 상태가 되는 것을 막기 위해 wrapping 타입인 Boolean 래퍼 타입을 사용합니다. </li>
     * </ul>
     */
    @NotNull
    private Boolean isHidden;

    /**
     * Comment의 필수 필드 초기화 생성자입니다.
     */
    private Comment(EatzUser author, Recipe recipe, String content, Boolean isHidden) {
        this.author = author;
        this.recipe = recipe;
        this.content = content;
        setIsHidden(isHidden);
    }

    /**
     * 내용을 업데이트합니다.
     * @param requesterId 업데이트를 요청한 사용자의 ID
     * @param content 내용
     */
    public void updateContent(Long requesterId, String content) {
        validate();
        validateAuthor(requesterId);
        validateContent(content);
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
    public void updateIsHidden(Long requesterId, Boolean isHidden) {
        validate();
        Long recipeAuthorId = getRecipe().getAuthor().getId();
        validateHidableByUser(requesterId, recipeAuthorId);
        setIsHidden(isHidden);
    }

    /**
     * 댓글을 삭제 처리합니다.
     * @param user 삭제 처리를 요청한 사용자의 EatzUser 엔티티
     */
    public void markAsDeleted(EatzUser user) {
        validateNotDeleted();
        Long recipeAuthorId = getRecipe().getAuthor().getId();
        validateDeletableByUser(user, recipeAuthorId);
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
        if (this.isMarkedAsDeleted()) {
            throw new IllegalStateException("삭제 처리된 댓글이에요.");
        }
    }

    /**
     * 사용자의 작성자 여부를 검증합니다.
     * @param userId 사용자의 ID
     */
    public void validateAuthor(Long userId) {
        if (!author.getId().equals(userId)) {
            throw new UnauthorizedAccessException("댓글의 작성자가 아니에요.");
        }
    }

    /**
     * 특정 사용자에 대해 해당 Comment 엔티티의 삭제 가능 여부를 검증합니다.
     * @param user 사용자의 EatzUser 엔티티
     * @param recipeAuthorId 댓글이 달린 레시피의 작성자 ID.
     *                       댓글이 달린 레시피의 작성자는 자신의 레시피에 달린 댓글을 삭제할 수 있기 때문에,
     *                       삭제를 요청한 사용자가 레시피의 작성자인지 확인할 때 사용합니다.
     */
    public void validateDeletableByUser(EatzUser user, long recipeAuthorId) {
        if (!hasControlPermission(user, recipeAuthorId)) {
            throw new UnauthorizedEatzUserException("댓글을 삭제할 권한이 없어요.");
        }
    }

    /**
     * 특정 사용자에 대해 해당 Comment 엔티티의 숨김 처리 가능 여부를 검증합니다.
     * @param userId 댓글 작성자 또는 평가가 달려 있는 레시피 작성자의 ID
     * @param recipeAuthorId 댓글이 달린 레시피의 작성자 ID.
     *                       댓글이 달린 레시피의 작성자는 자신의 레시피에 달린 댓글을 삭제할 수 있기 때문에,
     *                       삭제를 요청한 사용자가 레시피의 작성자인지 확인할 때 사용합니다.
     */
    public void validateHidableByUser(long userId, long recipeAuthorId) {
        if (!hasControlPermission(userId, recipeAuthorId)) {
            throw new UnauthorizedAccessException("댓글을 숨김 처리할 권한이 없어요.");
        }
    }

    /**
     * 특정 사용자에 대해 해당 Comment 엔티티의 숨김 처리 가능 여부를 검증합니다.
     * @param user 사용자의 EatzUser 엔티티
     * @param recipeAuthorId 댓글이 달린 레시피의 작성자 ID.
     *                       댓글이 달린 레시피의 작성자는 자신의 레시피에 달린 댓글을 삭제할 수 있기 때문에,
     *                       삭제를 요청한 사용자가 레시피의 작성자인지 확인할 때 사용합니다.
     */
    public void validateHidableByUser(EatzUser user, long recipeAuthorId) {
        if (!hasControlPermission(user, recipeAuthorId)) {
            throw new UnauthorizedAccessException("댓글을 숨김 처리할 권한이 없어요.");
        }
    }

    /**
     * 유효성을 검증합니다.
     */
    private void validate() {
        validateNotDeleted();
    }

    /**
     * 사용자의 댓글을 제어할 수 있는 권한의 소유 여부를 확인합니다.
     * @param user 사용자의 EatzUser 엔티티
     * @param recipeAuthorId 댓글이 달린 레시피의 작성자 ID.
     *                       댓글이 달린 레시피의 작성자는 자신의 레시피에 달린 평가를 제어할 수 있기 때문에,
     *                       사용자가 레시피의 작성자인지 확인하기 위해 사용합니다.
     * @return 댓글을 제어할 수 있는 권한의 소유 여부
     */
    private boolean hasControlPermission(EatzUser user, long recipeAuthorId) {
        boolean isCommentAuthor = author.getId().equals(user.getId());
        boolean isRecipeAuthor = user.getId().equals(recipeAuthorId);
        boolean isUserAdmin = user.isAdmin();

        return isCommentAuthor ||  isRecipeAuthor || isUserAdmin;
    }

    /**
     * 사용자의 댓글을 제어할 수 있는 권한의 소유 여부를 확인합니다.
     * @param userId 사용자의 ID
     * @param recipeAuthorId 평가가 달린 레시피의 작성자 ID.
     *                       평가가 달린 레시피의 작성자는 자신의 레시피에 달린 평가를 제어할 수 있기 때문에,
     *                       사용자가 레시피의 작성자인지 확인하기 위해 사용합니다.
     * @return 평가를 제어할 수 있는 권한의 소유 여부
     */
    private boolean hasControlPermission(long userId, long recipeAuthorId) {
        boolean isCommentAuthor = author.getId().equals(userId);
        boolean isRecipeAuthor = userId == recipeAuthorId;

        return isCommentAuthor || isRecipeAuthor;
    }

    /**
     * 내용의 유효성을 검증합니다.
     * @param content 내용
     */
    public static void validateContent(String content) {
        if (content == null || content.isBlank()) { throw new IllegalArgumentException("필수 항목인 내용이 비어 있어요."); }
    }

    /**
     * Comment 엔티티 팩토리 메서드
     * <ul>
     *     <li> isHidden이 null일 경우, 생성자가 기본 값인 false로 자동 초기화합니다. </li>
     * </ul>
     * @param author 작성자의 EatzUser 엔티티. 필수 항목입니다.
     * @param recipe 레시피의 Recipe 엔티티
     * @param content 내용. 필수 항목입니다.
     * @param isHidden 숨김 처리 여부. 필수 항목입니다.
     * @return Comment 엔티티
     */
    public static Comment create(EatzUser author, Recipe recipe, String content, Boolean isHidden) {
        validateContent(content);
        return new Comment(author, recipe, content, isHidden);
    }

    /**
     * Comment 엔티티 팩토리 메서드
     * <ul>
     *     <li> isHidden이 false인(숨김 처리 상태가 아닌) Comment 엔티티를 생성합니다. </li>
     * </ul>
     * @param author 작성자의 EatzUser 엔티티. 필수 항목입니다.
     * @param recipe 레시피의 Recipe 엔티티
     * @param content 내용. 필수 항목입니다.
     * @return 숨김 처리 여부가 false인 Comment 엔티티
     */
    public static Comment create(EatzUser author, Recipe recipe, String content) {
        return create(author, recipe, content, Boolean.FALSE);
    }

}
