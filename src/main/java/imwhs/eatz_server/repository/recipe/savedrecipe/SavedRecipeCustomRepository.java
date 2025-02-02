package imwhs.eatz_server.repository.recipe.savedrecipe;

import imwhs.eatz_server.domain.eatzuser.EatzUser;
import imwhs.eatz_server.dto.recipe.savedrecipe.SavedRecipeDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface SavedRecipeCustomRepository {

    List<SavedRecipeDto> findByUser(EatzUser user, Pageable pageable);

}
