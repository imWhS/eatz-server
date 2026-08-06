package imwhs.eatz_server.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ClientLaunchNoticeResponse {

    private Long id;

    private String title;

    private String markdownContent;

}
