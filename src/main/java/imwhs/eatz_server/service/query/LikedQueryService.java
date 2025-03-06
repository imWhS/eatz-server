package imwhs.eatz_server.service.query;

import imwhs.eatz_server.domain.liked.EntityType;
import imwhs.eatz_server.repository.liked.LikedRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class LikedQueryService {

    private final LikedRepository likedRepository;

    public long countLikeOfRecipe(Long recipeId) {
        return likedRepository.countAllLikeds(recipeId, EntityType.RECIPE);
    }

}
