package imwhs.eatz_server.dto.report;

import imwhs.eatz_server.domain.ReportCategory;
import imwhs.eatz_server.dto.CreationInfoResponse;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
public class ReportCategoryCreationInfoResponse extends CreationInfoResponse {

    public ReportCategoryCreationInfoResponse(Long id, LocalDateTime createdAt) {
        super(id, createdAt);
    }

    public ReportCategoryCreationInfoResponse(ReportCategory reason) {
        this(reason.getId(), reason.getCreatedAt());
    }

}
