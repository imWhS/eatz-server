package imwhs.eatz_server.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ClientLaunchNoticeResponse {

    private Long id;

    private String title;

    private String markdownContent;

    /**
     * 강제 공지 여부
     * <p> 클라이언트에서 '다시 보지 않기' 기능이 비활성화되고, 클라이언트가 foreground 상태로 전환될 때마다 launch notice가 표시됩니다. </p>
     */
    private boolean isForce;

}
