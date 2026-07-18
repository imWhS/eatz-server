package imwhs.eatz_server.repository.recipe;

import imwhs.eatz_server.domain.recipe.Recipe;
import imwhs.eatz_server.dto.recipe.*;
import imwhs.eatz_server.exception.RecipeNotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Recipe 엔티티의 기본적인 조회를 포함한 CRUD 쿼리 작업을 처리하는 Spring Data JPA 리포지토리입니다.
 */
@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Long>, RecipeQueryRepository {

    default Recipe get(Long id) {
        if (id == null) { throw new IllegalArgumentException("레시피의 ID가 필요해요."); }
        return findByIdAndDeletedAtIsNull(id).orElseThrow(() -> new RecipeNotFoundException(id));
    }

    /**
     * 연관관계 매핑(외래 키 설정)을 위한 proxy 객체를 반환합니다.
     * <p> Recipe의 id 필드만 참조할 수 있기 때문에, 연관 관계로서의 엔티티를 새로 매핑할 때에만 사용해야 합니다. </p>
     * @param id 레시피의 ID
     * @return Recipe의 proxy 객체
     */
    default Recipe getReference(Long id) {
        if (id == null) { throw new IllegalArgumentException("레시피의 ID가 필요해요."); }
        return getReferenceByIdAndDeletedAtIsNull(id);
    }

    default void validateExists(Long id) {
        if (id == null) { throw new IllegalArgumentException("레시피의 ID가 필요해요."); }
        if (!existsByIdAndDeletedAtIsNull(id)) { throw new RecipeNotFoundException(id); }
    }

    /**
     * ID에 해당하는 레시피의 핵심 정보와 작성자 정보를 조회합니다.
     * @param id 레시피의 ID
     * @return 레시피의 핵심 정보와 작성자 정보를 담고 있는 Optional
     */
    @Query("SELECT new imwhs.eatz_server.dto.recipe.RecipeEssentialWithAuthorDto(" +
            "   r.id, " +
            "   r.title, " +
            "   r.imageUrl, " +
            "   r.isCommentEnabled, " +
            "   a.id, " +
            "   a.username, " +
            "   a.imageUrl) " +
            "FROM Recipe r " +
            "JOIN r.author a " +
            "WHERE " +
            "   r.id = :id AND " +
            "   r.deletedAt IS null ")
    Optional<RecipeEssentialWithAuthorDto> findEssentialByIdWithAuthor(@Param("id") Long id);

    Optional<Recipe> findByIdAndDeletedAtIsNull(Long id);

    Recipe getReferenceByIdAndDeletedAtIsNull(Long id);

    boolean existsByIdAndDeletedAtIsNull(Long id);

    /**
     * 제목으로 레시피를 조회합니다.
     * @param title 검색할 레시피의 제목
     * @return 제목이 일치하는 레시피 엔티티를 포함한 Optional
     */
    Optional<Recipe> findByTitle(String title);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE Recipe r SET r.viewCount = r.viewCount + :count WHERE r.id = :id AND r.deletedAt IS null")
    void addViewCount(@Param("id") Long id, @Param("count") int count);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE Recipe r SET r.outboundCount = r.outboundCount + :count WHERE r.id = :id AND r.deletedAt IS null")
    void addOutboundCount(@Param("id") Long id, @Param("count") int count);

}
