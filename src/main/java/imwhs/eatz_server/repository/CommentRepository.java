package imwhs.eatz_server.repository;

import imwhs.eatz_server.domain.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    Optional<Comment> findByIdAndDeletedAtIsNull(Long id);

    @Query("select c from Comment c " +
            "join fetch c.recipe r " +
            "join fetch c.user u " +
            "where c.id = :commentId")
    Optional<Comment> findJoinUserRecipeById(@Param("commentId") Long id);

    @Query("select c from Comment c " +
            "join fetch c.user u " +
            "join fetch c.recipe r " +
            "where u.id = :userId and r.id = :recipeId")
    Page<Comment> findJoinUserRecipeByUserIdAndRecipeId(
            @Param("userId") Long userId,
            @Param("recipeId") Long recipeId,
            Pageable pageable
    );

    @Query("select c from Comment c " +
            "join fetch c.user u " +
            "join fetch c.recipe r " +
            "where r.id = :recipeId")
    Page<Comment> findJoinUserRecipeByRecipeId(@Param("recipeId") Long recipeId, Pageable pageable);

    @Query("select c from Comment c " +
            "join fetch c.user u " +
            "join fetch c.recipe r " +
            "where u.id = :userId")
    Page<Comment> findJoinUserRecipeByUserId(@Param("userId") Long userId, Pageable pageable);

}
