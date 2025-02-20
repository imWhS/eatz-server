package imwhs.eatz_server.repository.comment;

import imwhs.eatz_server.domain.recipe.Comment;
import imwhs.eatz_server.dto.comment.CommentResponseDto;
import imwhs.eatz_server.dto.comment.CommentCountByRecipeDto;
import imwhs.eatz_server.dto.comment.CommentWithRecipeResponseDto;
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

    @Query("""
    select new imwhs.eatz_server.dto.comment.CommentResponseDto(
        c.id,
        new imwhs.eatz_server.dto.comment.CommentUserDto(u.id, u.username, u.imageUrl),
        c.content,
        c.isHidden,
        c.createdAt, 
        c.updatedAt, 
        c.deletedAt
    ) 
    from Comment c
    join c.user u
    where c.recipe.id = :recipeId and c.deletedAt is null
    """)
    Page<CommentResponseDto> findWithUserByRecipeId(@Param("recipeId") Long recipeId, Pageable pageable);

    @Query("""
    select new imwhs.eatz_server.dto.comment.CommentWithRecipeResponseDto(
        c.id,
        new imwhs.eatz_server.dto.comment.CommentRecipeDto(r.id, r.title, r.imageUrl),
        c.content, 
        c.isHidden, 
        c.createdAt, 
        c.updatedAt, 
        c.deletedAt
    ) 
    from Comment c
    join c.recipe r
    join c.user u
    where u.username = :username
    """)
    Page<CommentWithRecipeResponseDto> findWithRecipeByUserUsernameAndRecipeId(
            @Param("username") String username,
            Pageable pageable
    );

    @Query("select c from Comment c " +
            "join fetch c.user u " +
            "join fetch c.recipe r " +
            "where u.username = :username and r.id = :recipeId and c.deletedAt is null")
    Page<Comment> findWithUserRecipeByRecipeId(@Param("username") String username, @Param("recipeId") Long recipeId, Pageable pageable);

    @Query("select c from Comment c " +
            "join fetch c.user u " +
            "join fetch c.recipe r " +
            "where u.id = :userId and c.deletedAt is null")
    Page<Comment> findWithUserRecipeByUserId(@Param("userId") Long userId, Pageable pageable);

    long countAllById(Long id);

    @Query("select new imwhs.eatz_server.dto.comment.CommentCountByRecipeDto(c.recipe.id, COUNT(c)) " +
            "from Comment c " +
            "where c.recipe.id in :recipeIds and c.deletedAt is null " +
            "group by c.recipe.id")
    List<CommentCountByRecipeDto> countByRecipeIds(@Param("recipeIds") List<Long> recipeIds);

}
