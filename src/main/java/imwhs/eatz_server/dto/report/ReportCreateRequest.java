package imwhs.eatz_server.dto.report;

import imwhs.eatz_server.domain.ReportResourceType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReportCreateRequest {

    @NotNull
    private Long resourceId;

    @NotNull
    private ReportResourceType resourceType;

    @NotNull
    private Long categoryId;

    private String resourceContent;

    private String description;

}

