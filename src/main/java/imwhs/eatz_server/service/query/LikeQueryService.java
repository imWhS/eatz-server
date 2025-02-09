package imwhs.eatz_server.service.query;

import imwhs.eatz_server.domain.likes.LikesType;
import imwhs.eatz_server.repository.like.LikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class LikeQueryService {

    private final LikeRepository likeRepository;

    public long countLikeOfRecipe(Long recipeId) {
        return likeRepository.countAllLikes(recipeId, LikesType.RECIPE);
    }

}
