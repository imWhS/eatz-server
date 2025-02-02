package imwhs.eatz_server.service.query;

import imwhs.eatz_server.domain.eatzuser.EatzUser;
import imwhs.eatz_server.domain.recipe.SavedRecipe;
import imwhs.eatz_server.dto.recipe.savedrecipe.SavedRecipeDto;
import imwhs.eatz_server.exception.EatzUserNotFoundException;
import imwhs.eatz_server.repository.eatzuser.EatzUserRepository;
import imwhs.eatz_server.repository.recipe.savedrecipe.SavedRecipeCustomRepositoryImpl;
import imwhs.eatz_server.repository.recipe.savedrecipe.SavedRecipeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 저장된 레시피(SavedRecipe)와 관련된 조회 기능을 제공하는 SavedRecipeQueryService 클래스입니다.
 * SavedRecipe에 대한 읽기 전용 쿼리 메서드를 제공합니다.
 */
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class SavedRecipeQueryService {

    private final EatzUserRepository userRepository;

    private final SavedRecipeRepository savedRecipeRepository;

    @Transactional
    public List<SavedRecipeDto> findSavedRecipesByUser(Long userId, Pageable pageable) {
        EatzUser user = userRepository.findById(userId)
                .orElseThrow(() -> new EatzUserNotFoundException("id " + userId + "에 해당하는 사용자가 존재하지 않습니다."));
        return savedRecipeRepository.findByUser(user, pageable);
    }

}
