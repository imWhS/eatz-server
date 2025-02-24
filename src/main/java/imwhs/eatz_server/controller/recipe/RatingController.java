package imwhs.eatz_server.controller.recipe;

import imwhs.eatz_server.auth.EatzUserAuthUtil;
import imwhs.eatz_server.dto.ApiResponse;
import imwhs.eatz_server.dto.Paged;
import imwhs.eatz_server.dto.rating.RatingCreateDto;
import imwhs.eatz_server.dto.rating.RatingWithUserResponseDto;
import imwhs.eatz_server.service.RatingService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/v0/recipes/{id}/ratings")
@RequiredArgsConstructor
@RestController
public class RatingController {

    private final RatingService ratingService;

    @PostMapping
    public ResponseEntity<ApiResponse<Long>> addRating(@PathVariable Long id, @RequestBody RatingCreateDto dto) {
        Long ratingId = ratingService.registerRating(id, EatzUserAuthUtil.getUsername(), dto.getScore(), dto.getContent());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(ratingId));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Paged<RatingWithUserResponseDto>>> findRatings(
            @PathVariable Long id,
            @PageableDefault(page = 0, size = 10) Pageable pageable) {
        Page<RatingWithUserResponseDto> ratings = ratingService.findRatingsByRecipe(id, pageable);
        return ResponseEntity.ok((ApiResponse.success(ratings)));
    }

}
