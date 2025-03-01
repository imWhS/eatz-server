package imwhs.eatz_server.repository.rating;

import imwhs.eatz_server.dto.rating.RatingByRecipeDto;
import imwhs.eatz_server.dto.rating.RatingByUserDto;
import imwhs.eatz_server.dto.rating.RatingDetailDtoOld;
import imwhs.eatz_server.dto.rating.RatingsDetailDto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RatingCustomRepository {

    Optional<RatingDetailDtoOld> findRatingDetailById(Long id);

    List<RatingByRecipeDto> findRatingsByRecipe(Long id, Pageable pageable);

    Long countRatingsByRecipe(Long id);

    List<RatingByUserDto> findRatingsByUser(Long id, Pageable pageable);

    Long countRatingsByUser(Long id);

}
