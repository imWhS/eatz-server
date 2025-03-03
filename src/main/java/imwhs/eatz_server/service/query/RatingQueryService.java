package imwhs.eatz_server.service.query;

import imwhs.eatz_server.dto.rating.RatingSummaryDto;
import imwhs.eatz_server.repository.rating.RatingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 평가(Rating) 관련 정보를 조회하는 RatingQueryService 클래스입니다.
 * Rating에 대한 읽기 전용 쿼리 메서드를 제공합니다.
 */
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class RatingQueryService {

    private final RatingRepository ratingRepository;

    public RatingSummaryDto findRatingSummaryByRecipeId(Long id) {
        return ratingRepository.findRatingSummaryByRecipeId(id);
    }

}
