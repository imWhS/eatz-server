package imwhs.eatz_server.repository.comment;

import imwhs.eatz_server.domain.Comment;
import imwhs.eatz_server.dto.comment.CommentBasicDto;
import imwhs.eatz_server.dto.comment.CommentEssentialWithRecipeDto;
import imwhs.eatz_server.exception.CommentNotFoundException;
import imwhs.eatz_server.exception.RecipeNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Comment 엔티티의 기본적인 조회를 포함한 CRUD 쿼리 작업을 처리하는 Spring Data JPA 리포지토리입니다.
 */
@Repository
public interface CommentRepository extends JpaRepository<Comment, Long>, CommentQueryRepository {

    default Comment get(Long id) {
        return findByIdAndDeletedAtIsNull(id).orElseThrow(() -> new CommentNotFoundException(id));
    }

    default Comment getWithRecipe(Long id) {
        return findWithRecipeByCommentId(id).orElseThrow(() -> new CommentNotFoundException(id));
    }

    default void validateExists(Long id) {
        if (id == null) { throw new IllegalArgumentException("댓글의 ID가 필요해요."); }
        if (!existsByIdAndDeletedAtIsNull(id)) { throw new CommentNotFoundException(id); }
    }

    Optional<Comment> findByIdAndDeletedAtIsNull(Long id);

    boolean existsByIdAndDeletedAtIsNull(Long id);

    @Query("SELECT c " +
            "FROM Comment c " +
            "JOIN FETCH c.recipe r " +
            "WHERE " +
            "   c.id = :id AND " +
            "   c.deletedAt IS null")
    Optional<Comment> findWithRecipeByCommentId(@Param("id") Long id);

    /**
     * 레시피에 달린 모든 댓글의 기본 정보 목록을 조회합니다.
     * <ul>
     *     <li> 회원 탈퇴한 사용자의 댓글도 목록에 포함합니다. </li>
     *     <li> 숨김 처리된 댓글, 차단한 사용자의 댓글은 조회 대상에서 제외합니다. </li>
     * </ul>
     * @param id 레시피의 ID
     * @return 조회된 댓글의 기본 정보 목록 및 페이징 정보
     */
    @Query("SELECT new imwhs.eatz_server.dto.comment.CommentBasicDto(" +
            "   c.id, " +
            "   c.content, " +
            "   new imwhs.eatz_server.dto.eatzuser.EatzUserEssentialDto(" +
            "       a.id, " +
            "       a.username, " +
            "       a.imageUrl)," +
            "   c.isHidden," +
            "   c.createdAt," +
            "   c.updatedAt)" +
            "FROM Comment c " +
            "INNER JOIN c.author a " +
            "WHERE " +
            "   c.recipe.id = :id AND " +
            "   c.deletedAt IS null " +
            "ORDER BY c.createdAt DESC")
    Page<CommentBasicDto> findAllBasicsByRecipeIdOld(@Param("id") Long id, Pageable pageable);

    /**
     * 특정 사용자가 작성한 모든 댓글의 기본 정보 목록을 조회합니다.
     * <ul>
     *     <li> 회원 탈퇴한 사용자가 작성한 레시피에 달린 댓글도 목록에 포함합니다. </li>
     * </ul>
     * @param id 작성자의 ID
     * @return 조회된 댓글의 기본 정보 목록 및 페이징 정보
     */
    @Query("""
    SELECT new imwhs.eatz_server.dto.comment.CommentEssentialWithRecipeDto(
        c.id,
        new imwhs.eatz_server.dto.recipe.RecipeEssentialDto(
            r.id, 
            r.title, 
            r.imageUrl),
        c.content,
        c.isHidden,
        c.createdAt,
        c.updatedAt)
    FROM Comment c
    JOIN c.recipe r
    WHERE 
        c.author.id = :id AND 
        c.deletedAt IS null
    """)
    Page<CommentEssentialWithRecipeDto> findAllEssentialsWithRecipeByAuthorId(
            @Param("id") Long id,
            Pageable pageable
    );

}
