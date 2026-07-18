package imwhs.eatz_server.dto.rating;

import imwhs.eatz_server.dto.eatzuser.EatzUserEssentialDto;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 평가(Rating)의 기본 정보를 전달할 때 사용하는 DTO입니다.
 * <ul>
 *      <li> Rating의 핵심 및 대부분의 정보와 EatzUser 등의 연관 관계 엔티티의 정보를 포함합니다. </li>
 * </ul>
 */
@Data
@AllArgsConstructor
public class RatingBasicDto {

    private Long id;

    private EatzUserEssentialDto author;

    private Integer score;

    private String content;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}
