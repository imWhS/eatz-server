package imwhs.eatz_server.repository.tag;

import com.querydsl.core.group.GroupBy;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import imwhs.eatz_server.domain.*;
import imwhs.eatz_server.dto.tag.TagDetailDto;
import imwhs.eatz_server.dto.tag.theme.ThemeNamedWithTagsDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Repository
public class ThemeQueryRepositoryImpl implements ThemeQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<ThemeNamedWithTagsDto> findAllNamedWithTags() {
        QTheme theme = QTheme.theme;
        QThemeTag themeTag = QThemeTag.themeTag;
        QTag tag = QTag.tag;

        Map<Long, ThemeNamedWithTagsDto> content = queryFactory
                .from(theme)
                .innerJoin(theme.tags, themeTag).on(themeTag.deletedAt.isNull())
                .innerJoin(themeTag.tag, tag).on(tag.deletedAt.isNull())
                .where(
                        theme.name.isNotEmpty(),
                        theme.deletedAt.isNull()
                )
                .orderBy(theme.name.asc(), tag.name.asc())
                .transform(
                        GroupBy.groupBy(theme.id).as(
                                Projections.constructor(ThemeNamedWithTagsDto.class,
                                        theme.id,
                                        theme.name,
                                        theme.description,
                                        GroupBy.list(Projections.constructor(TagDetailDto.class, tag)))));

        return new ArrayList<>(content.values());
    }

}
