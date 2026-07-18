package imwhs.eatz_server.dto.report;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReportCategoryUpdateRequest {

    @NotNull
    private String code;

    @NotNull
    private String description;

}
