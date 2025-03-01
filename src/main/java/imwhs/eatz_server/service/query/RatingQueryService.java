package imwhs.eatz_server.service.query;

import imwhs.eatz_server.dto.PagedApiResponse;
import imwhs.eatz_server.dto.rating.RatingByRecipeDto;
import imwhs.eatz_server.dto.rating.RatingByUserDto;
import imwhs.eatz_server.dto.rating.RatingDetailDtoOld;
import imwhs.eatz_server.dto.rating.RatingSummaryDto;
import imwhs.eatz_server.exception.RatingNotFoundException;
import imwhs.eatz_server.repository.rating.RatingCustomRepositoryImpl;
import imwhs.eatz_server.repository.rating.RatingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
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

    /**
     * 식별자로 평가와 관련된 상세 정보를 조회합니다.<br/>
     * 식별자에 해당하는 평가의 기본 정보와 평가를 등록한 사용자, 평가가 달린 레시피의 부가 정보를 조회합니다.
     * @param id 평가 식별자.
     */
    public RatingDetailDtoOld findRatingDetail(Long id) {
        return ratingRepository.findRatingDetailById(id)
                .orElseThrow(() -> new RatingNotFoundException("id " + id + "에 해당하는 평가가 존재하지 않습니다."));
    }

    public RatingSummaryDto findRatingSummaryByRecipeId(Long id) {
        return ratingRepository.findRatingSummaryByRecipeId(id);
    }

}
