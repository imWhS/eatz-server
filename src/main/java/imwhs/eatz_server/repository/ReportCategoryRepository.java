package imwhs.eatz_server.repository;

import imwhs.eatz_server.domain.ReportCategory;
import imwhs.eatz_server.dto.report.ReportCategoryDto;
import imwhs.eatz_server.exception.ReportReasonIsNotActiveException;
import imwhs.eatz_server.exception.ReportReasonNotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportCategoryRepository extends JpaRepository<ReportCategory, Long> {

    default ReportCategory get(Long id) {
        if (id == null) { throw new IllegalArgumentException("신고 카테고리 ID가 필요해요."); }
        ReportCategory reason = findById(id).orElseThrow(() -> new ReportReasonNotFoundException(id));
        if (!reason.isActive()) { throw new ReportReasonIsNotActiveException(reason.getCode()); }
        return reason;
    }

    @Query("SELECT new imwhs.eatz_server.dto.report.ReportCategoryDto(" +
            "r.id, " +
            "r.code, " +
            "r.description) " +
            "FROM ReportCategory r " +
            "WHERE r.isActive = true")
    List<ReportCategoryDto> findAllActiveCategories();

    boolean existsById(Long id);

}
