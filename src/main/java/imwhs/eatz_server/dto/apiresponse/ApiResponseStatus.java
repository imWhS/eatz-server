package imwhs.eatz_server.dto.apiresponse;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum ApiResponseStatus {

    SUCCESS("success"),
    ERROR("error");

    private final String value;

    @JsonValue
    public String getValue() {
        return value;
    }

}