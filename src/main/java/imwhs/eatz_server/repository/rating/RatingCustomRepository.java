package imwhs.eatz_server.repository.rating;

import imwhs.eatz_server.dto.rating.RatingWithUserDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface RatingCustomRepository {

    List<RatingWithUserDto> findRatingsByRecipe(Long id, Pageable pageable);

    Long countRatingsByRecipe(Long id);

    Long countRatingsByUser(Long id);

}
