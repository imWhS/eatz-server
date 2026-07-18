package imwhs.eatz_server.controller.api;

import imwhs.eatz_server.dto.rating.RatingUpdateRequest;
import imwhs.eatz_server.resolver.AuthenticatedEatzUserId;
import imwhs.eatz_server.service.rating.RatingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/v0/ratings")
@RequiredArgsConstructor
@RestController
public class RatingController {

    private final RatingService ratingService;

    /**
     * 평가를 업데이트합니다.
     * @param id 평가의 ID
     * @param userId 업데이트를 요청한 사용자의 ID
     * @param request 평가 업데이트 요청에 필요한 정보를 담은 DTO
     */
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(
            @PathVariable Long id,
            @AuthenticatedEatzUserId Long userId,
            @RequestBody RatingUpdateRequest request) {
        ratingService.update(id, userId, request.getScore(), request.getContent());
    }

    /**
     * 평가를 삭제 처리합니다.
     * @param id 평가의 ID
     * @param userId 삭제 처리를 요청한 사용자의 ID
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void markAsDeleted(@PathVariable Long id, @AuthenticatedEatzUserId Long userId) {
        ratingService.markAsDeleted(id, userId);
    }

}
