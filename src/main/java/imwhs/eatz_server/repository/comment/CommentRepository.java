package imwhs.eatz_server.repository.comment;

import imwhs.eatz_server.domain.recipe.Comment;
import imwhs.eatz_server.dto.comment.CommentCountByRecipeDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * CommentRepository 클래스입니다.<br/>
 * Comment 엔티티의 기본적인 조회를 포함한 CRUD 쿼리 작업을 처리하는 Spring Data JPA 리포지토리입니다.
 */
@Repository
public interface CommentRepository extends JpaRepository<Comment, Long>, CommentCustomRepository {

    Optional<Comment> findByIdAndDeletedAtIsNull(Long id);

    long countByRecipeIdAndDeletedAtIsNull(Long id);

    @Query("select c from Comment c " +
            "join fetch c.recipe r " +
            "join fetch c.user u " +
            "where c.id = :commentId")
    Optional<Comment> findWithUserRecipeById(@Param("commentId") Long id);

    @Query("select c from Comment c " +
            "join fetch c.user u " +
            "join fetch c.recipe r " +
            "where u.id = :userId and r.id = :recipeId")
    Page<Comment> findWithUserRecipeByUserIdAndRecipeId(
            @Param("userId") Long userId,
            @Param("recipeId") Long recipeId,
            Pageable pageable
    );

    @Query("select c from Comment c " +
            "join fetch c.user u " +
            "join fetch c.recipe r " +
            "where r.id = :recipeId")
    Page<Comment> findWithUserRecipeByRecipeId(@Param("recipeId") Long recipeId, Pageable pageable);

    @Query("select c from Comment c " +
            "join fetch c.user u " +
            "join fetch c.recipe r " +
            "where u.id = :userId")
    Page<Comment> findWithUserRecipeByUserId(@Param("userId") Long userId, Pageable pageable);

    long countAllById(Long id);

    @Query("select new imwhs.eatz_server.dto.comment.CommentCountByRecipeDto(c.recipe.id, COUNT(c)) " +
            "from Comment c " +
            "where c.recipe.id in :recipeIds " +
            "group by c.recipe.id")
    List<CommentCountByRecipeDto> countByRecipeIds(@Param("recipeIds") List<Long> recipeIds);

}
