package imwhs.eatz_server.repository.rating;

import imwhs.eatz_server.dto.rating.RatingByRecipeDto;
import imwhs.eatz_server.dto.rating.RatingByUserDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface RatingCustomRepository {

    List<RatingByRecipeDto> findRatingsByRecipe(Long id, Pageable pageable);

    Long countRatingsByRecipe(Long id);

    List<RatingByUserDto> findRatingsByUser(Long id, Pageable pageable);

    Long countRatingsByUser(Long id);

}
