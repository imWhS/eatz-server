package imwhs.eatz_server.repository;

import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import imwhs.eatz_server.domain.QReport;
import imwhs.eatz_server.domain.ReportResourceType;
import imwhs.eatz_server.dto.report.ReportDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.List;

@RequiredArgsConstructor
@Repository
public class ReportQueryRepositoryImpl implements ReportQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<ReportDto> findAllByResource(ReportResourceType resourceType, boolean resolved, Pageable pageable) {
        QReport report = QReport.report;

        Predicate[] filters = {
                report.resourceType.eq(resourceType),
                createResolvedFilterExpression(report, resolved),
                report.deletedAt.isNull()
        };

        JPAQuery<ReportDto> mainQuery = queryFactory
                .select(Projections.constructor(ReportDto.class,
                        report.id,
                        report.reporter.id,
                        report.resourceId,
                        report.category.id,
                        report.resourceContent,
                        report.resolvedBy.id,
                        report.createdAt,
                        report.updatedAt))
                .from(report)
                .where(filters)
                .orderBy(report.createdAt.desc());

        List<ReportDto> content = mainQuery
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(report.count())
                .from(report)
                .where(filters);

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    private BooleanExpression createResolvedFilterExpression(QReport report, boolean resolved) {
        if (!resolved) { return report.resolvedBy.isNull(); }
        else { return report.resolvedBy.isNotNull(); }
    }
    
}
