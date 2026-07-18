package imwhs.eatz_server.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class EmailZonedVerificationCodeRequest {

    @Email @NotBlank
    private String email;

    @NotBlank
    private String timeZoneId;

    public String getTimeZoneId() {
        if (timeZoneId == null) { return "Asia/Seoul"; }
        else { return timeZoneId; }
    }

}

