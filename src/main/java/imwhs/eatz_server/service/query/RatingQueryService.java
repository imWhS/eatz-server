package imwhs.eatz_server.service.query;

import imwhs.eatz_server.dto.PagedResponse;
import imwhs.eatz_server.dto.rating.RatingByRecipeResponseDto;
import imwhs.eatz_server.dto.rating.RatingByUserResponseDto;
import imwhs.eatz_server.dto.rating.RatingDetailResponseDto;
import imwhs.eatz_server.exception.RatingNotFoundException;
import imwhs.eatz_server.repository.rating.RatingQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class RatingQueryService {

    /**
     * 페이지 번호 및 크기 기본 값.
     * <p>
     *     응답 메시지에 포함시킬 시용자에 대해 페이징 처리를 하기 위해 정의합니다.
     * </p>
     */
    private static final int DEFAULT_CURRENT_PAGE = 0;
    private static final int DEFAULT_PAGING_SIZE = 10;

    private final RatingQueryRepository ratingQueryRepository;

    /**
     * 식별자로 평가와 관련된 상세 정보를 조회합니다.<br/>
     * 식별자에 해당하는 평가의 기본 정보와 평가를 등록한 사용자, 평가가 달린 레시피의 부가 정보를 조회합니다.
     * @param id 평가 식별자
     */
    public RatingDetailResponseDto findRatingDetail(Long id) {
        return ratingQueryRepository.findRatingDetailById(id)
                .orElseThrow(() -> new RatingNotFoundException("id " + id + "에 해당하는 평가가 존재하지 않습니다."));
    }

    /**
     * 특정 레시피에 달린 모든 평가와 작성자 정보를 조회합니다.<br/>
     * 레시피에 달린 모든 평가 별 기본 정보와 해당 평가를 등록한 사용자의 부가 정보를 조회합니다.
     * @param id 레시피 식별자
     */
    public PagedResponse<RatingByRecipeResponseDto> findRatingsByRecipe(Long id, Integer currentPage, Integer pagingSize) {
        int page = currentPage == null ? DEFAULT_CURRENT_PAGE : currentPage;
        int size = pagingSize == null ? DEFAULT_PAGING_SIZE : pagingSize;

        List<RatingByRecipeResponseDto> data = ratingQueryRepository.findRatingsByRecipe(id, page, size);
        Long totalItems = ratingQueryRepository.countRatingsByRecipe(id);
        return PagedResponse.of(data, totalItems, (int) Math.ceil((double) totalItems / size), page, size);
    }

    /**
     * 특정 사용자가 등록한 모든 댓글과 레시피 정보를 조회합니다.<br/>
     * 사용자가 등록한 모든 댓글 별 기본 정보와 해당 댓글이 달린 레시피의 부가 정보를 조회합니다.
     * @param id 레시피 식별자
     */
    public PagedResponse<RatingByUserResponseDto> findRatingsByUser(Long id, Integer currentPage, Integer pagingSize) {
        int page = currentPage == null ? DEFAULT_CURRENT_PAGE : currentPage;
        int size = pagingSize == null ? DEFAULT_PAGING_SIZE : pagingSize;

        List<RatingByUserResponseDto> data = ratingQueryRepository.findRatingsByUser(id, page, size);
        Long totalItems = ratingQueryRepository.countRatingsByUser(id);
        return PagedResponse.of(data, totalItems, (int) Math.ceil((double) totalItems / size), page, size);
    }


}
