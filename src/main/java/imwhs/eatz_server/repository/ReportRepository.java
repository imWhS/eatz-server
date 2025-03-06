package imwhs.eatz_server.repository;

import imwhs.eatz_server.domain.Report;
import imwhs.eatz_server.domain.liked.EntityType;
import imwhs.eatz_server.dto.ReportDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {

    @Query("select r from Report r where r.id = :id")
    Optional<Report> findById(@Param("id") Long id);

    @Query("select new imwhs.eatz_server.dto.ReportDto(" +
            "r.id, " +
            "r.entityId, " +
            "r.type, " +
            "r.content, " +
            "r.resolvedBy.id, " +
            "r.createdAt, " +
            "r.updatedAt)" +
            "from Report r where r.type = :type")
    Page<ReportDto> findByType(@Param("type") EntityType type, Pageable pageable);

}
