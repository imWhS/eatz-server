package imwhs.eatz_server.dto.report;

import imwhs.eatz_server.domain.Report;
import imwhs.eatz_server.dto.CreationInfoResponse;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
public class ReportCreationInfoResponse extends CreationInfoResponse {

    public ReportCreationInfoResponse(Long id, LocalDateTime createdAt) {
        super(id, createdAt);
    }

    public ReportCreationInfoResponse(Report report) {
        this(report.getId(), report.getCreatedAt());
    }

}
