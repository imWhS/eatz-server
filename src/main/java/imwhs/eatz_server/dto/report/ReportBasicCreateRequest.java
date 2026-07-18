package imwhs.eatz_server.dto.report;

import lombok.Data;

@Data
public class ReportBasicCreateRequest {

    private String resourceContent;

    private Long categoryId;

    private String description;

}
