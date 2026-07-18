package imwhs.eatz_server.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 이메일와 연관된 계정 상태를 전달할 때 사용하는 DTO입니다.<br/>
 */
@AllArgsConstructor
@Data
public class EmailAvailabilityResponse {

    private EmailAvailability availability;
    private String message;

}