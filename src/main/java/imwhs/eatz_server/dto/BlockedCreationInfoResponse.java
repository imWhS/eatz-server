package imwhs.eatz_server.dto;

import imwhs.eatz_server.domain.Report;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
public class BlockedCreationInfoResponse extends CreationInfoResponse {

    public BlockedCreationInfoResponse(Long id, LocalDateTime createdAt) {
        super(id, createdAt);
    }

    public BlockedCreationInfoResponse(Report report) {
        this(report.getId(), report.getCreatedAt());
    }

}
