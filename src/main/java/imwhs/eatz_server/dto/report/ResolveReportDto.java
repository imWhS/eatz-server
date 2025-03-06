package imwhs.eatz_server.dto.report;

import imwhs.eatz_server.domain.liked.EntityType;
import lombok.Data;

@Data
public class ResolveReportDto {

    private Long entityId;

    private EntityType type;

}
