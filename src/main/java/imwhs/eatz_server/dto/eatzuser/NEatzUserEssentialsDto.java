package imwhs.eatz_server.dto.eatzuser;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * EatzUserEssentialsDto 클래스입니다.<br/>
 * 사용자의 핵심 정보를 전달하기 위한 DTO입니다.
 */
@Data
@AllArgsConstructor
public class NEatzUserEssentialsDto {

    private Long id;

    private String username;

    private String imageUrl;

}
