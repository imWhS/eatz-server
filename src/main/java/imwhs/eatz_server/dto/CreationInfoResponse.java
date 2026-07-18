package imwhs.eatz_server.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreationInfoResponse {

    private Long id;
    private LocalDateTime createdAt;

}
