package imwhs.eatz_server.repository.tag;

import imwhs.eatz_server.domain.recipe.RecipeTag;
import imwhs.eatz_server.dto.tag.TagByRecipeDto;
import imwhs.eatz_server.dto.tag.TagEssentialDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RecipeTagRepository extends JpaRepository<RecipeTag, Long> {

    @Query("SELECT new imwhs.eatz_server.dto.tag.TagByRecipeDto(" +
            "   rt.recipe.id, " +
            "   rt.tag.id, " +
            "   rt.tag.name) " +
            "FROM RecipeTag rt " +
            "JOIN rt.tag t " +
            "WHERE " +
            "   rt.recipe.id IN :ids AND " +
            "   t.deletedAt IS null")
    List<TagByRecipeDto> findTagsByRecipeIds(@Param("ids") List<Long> ids);

    /**
     * ID에 해당하는 레시피가 속한 모든 태그의 핵심 정보 목록을 조회합니다.
     * @param id 레시피의 id
     * @return id에 해당하는 레시피가 속한 모든 태그의 핵심 정보 목록
     */
    @Query("SELECT new imwhs.eatz_server.dto.tag.TagEssentialDto(" +
            "   t.id, " +
            "   t.name) " +
            "FROM RecipeTag rt " +
            "JOIN rt.tag t " +
            "WHERE " +
            "   rt.recipe.id = :id AND" +
            "   t.deletedAt IS null ")
    List<TagEssentialDto> findAllEssentialsByRecipeId(@Param("id") Long id);

}
