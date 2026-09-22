package imwhs.eatz_server.domain;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ReportResourceType {

    RECIPE("recipe"),
    COMMENT("comment"),
    RATING("rating");

    private final String code;

    @JsonValue
    public String getCode() {
        return code;
    }

}