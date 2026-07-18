package imwhs.eatz_server.dto.report;

import imwhs.eatz_server.domain.ReportResourceType;
import lombok.Data;

@Data
public class ReportResolveRequest {

    private Long entityId;

    private ReportResourceType type;

}
