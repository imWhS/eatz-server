package imwhs.eatz_server.repository.tag;

import imwhs.eatz_server.domain.Tag;
import imwhs.eatz_server.domain.ThemeTag;
import imwhs.eatz_server.domain.Theme;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ThemeTagRepository extends JpaRepository<ThemeTag, Long> {

    /**
     * 특정 ID의 테마에 속한 모든 Tag를 조회합니다.
     * @param id 테마의 ID
     */
    @Query("SELECT tt.tag " +
            "FROM ThemeTag tt " +
            "WHERE " +
            "   tt.theme.id = :id AND " +
            "   tt.deletedAt IS null")
    List<Tag> findAllTagsByThemeId(@Param("id") Long id);

    @Query("SELECT tg " +
            "FROM ThemeTag tt " +
            "INNER JOIN tt.tag tg ON tg.deletedAt IS null " +
            "WHERE " +
            "   tt.deletedAt IS null")
    Page<Tag> findAllThemedTags(Pageable pageable);

}
