package imwhs.eatz_server.dto.report;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class ReportCategoryDto {

    private Long id;

    private String code;

    private String description;

}
