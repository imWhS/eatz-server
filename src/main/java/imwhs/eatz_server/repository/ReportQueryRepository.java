package imwhs.eatz_server.repository;

import imwhs.eatz_server.domain.ReportResourceType;
import imwhs.eatz_server.dto.report.ReportDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

public interface ReportQueryRepository {

    Page<ReportDto> findAllByResource(ReportResourceType resourceType, boolean resolved, Pageable pageable);

}
