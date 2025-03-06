package imwhs.eatz_server.dto;

import imwhs.eatz_server.domain.liked.EntityType;
import lombok.Data;

@Data
public class CreateReportDto {

    private Long entityId;

    private EntityType type;

    private String content;

}
