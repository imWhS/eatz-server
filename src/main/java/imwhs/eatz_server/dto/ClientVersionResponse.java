package imwhs.eatz_server.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ClientVersionResponse {

    private String latestVersion;

    private String requiredVersion;

    private String message;

}
