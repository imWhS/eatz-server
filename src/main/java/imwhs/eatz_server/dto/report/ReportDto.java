package imwhs.eatz_server.dto.report;

import imwhs.eatz_server.domain.ReportResourceType;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@AllArgsConstructor
@Data
public class ReportDto {

    private Long id;

    private Long reporterId;

    private Long resourceId;

    private ReportResourceType resource;

    private Long categoryId;

    private String resourceContent;

    private Long resolvedBy;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}
