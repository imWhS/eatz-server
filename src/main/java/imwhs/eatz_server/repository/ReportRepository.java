package imwhs.eatz_server.repository;

import imwhs.eatz_server.domain.Report;
import imwhs.eatz_server.exception.ReportNotFoundException;
import imwhs.eatz_server.exception.ReportReasonNotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long>, ReportQueryRepository {

    default Report get(Long id) {
        if (id == null) { throw new IllegalArgumentException("신고의 ID가 필요해요."); }
        return findById(id).orElseThrow(() -> new ReportNotFoundException(id));
    }

    default void validateExists(Long id) {
        if (id == null) { throw new IllegalArgumentException("신고의 ID가 필요해요."); }
        if (existsById(id)) { throw new ReportReasonNotFoundException(id); }
    }

    Optional<Report> findById(@Param("id") Long id);

}
