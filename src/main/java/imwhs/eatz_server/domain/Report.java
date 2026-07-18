package imwhs.eatz_server.domain;

import imwhs.eatz_server.common.BaseEntity;
import imwhs.eatz_server.domain.eatzuser.EatzUser;
import imwhs.eatz_server.exception.UnauthorizedAccessException;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * 신고 정보를 정의하고, 연관 데이터를 관리하는 Report 엔티티입니다.
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@Entity
public class Report extends BaseEntity {

    @EqualsAndHashCode.Include
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 신고를 접수한 사용자 (신고자)
     */
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporter_id", nullable = false)
    private EatzUser reporter;

    /**
     * 신고가 접수된 자원의 ID
     */
    @NotNull
    @Column(nullable = false)
    private Long resourceId;

    /**
     * 신고가 접수된 자원의 유형
     */
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReportResourceType resourceType;

    /**
     * 신고 사유 카테고리
     */
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "report_category_id", nullable = false)
    private ReportCategory category;

    /**
     * 신고 대상 자원의 주요 내용
     */
    private String resourceContent;

    /**
     * 신고자의 설명
     */
    private String description;

    /**
     * 처리를 완료한 관리자
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resolved_by", nullable = true)
    private EatzUser resolvedBy;

    public static Report of(
            EatzUser reporter,
            Long resourceId,
            ReportResourceType resourceType,
            ReportCategory reason,
            String resourceContent,
            String description
    ) {
        validateResourceId(resourceId);
        return new Report(null, reporter, resourceId, resourceType, reason, resourceContent, description, null);
    }

    private static void validateResourceId(Long entityId) {
        if (entityId == null) {
            throw new IllegalArgumentException("필수 항목인 자원의 ID가 비어 있어요.");
        }
    }

    public void resolve(EatzUser admin) {
        if (isResolved()) { throw new IllegalStateException("이미 처리 완료된 신고예요."); }
        if (!admin.isAdmin()) { throw new UnauthorizedAccessException("신고를 처리할 수 있는 권한이 없어요."); }

        this.resolvedBy = admin;
    }

    public boolean isResolved() {
        return resolvedBy != null;
    }

}
