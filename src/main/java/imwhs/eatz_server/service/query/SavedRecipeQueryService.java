package imwhs.eatz_server.service.query;

import imwhs.eatz_server.domain.EatzUser;
import imwhs.eatz_server.domain.SavedRecipe;
import imwhs.eatz_server.dto.savedrecipe.SavedRecipeResponseDto;
import imwhs.eatz_server.exception.EatzUserNotFoundException;
import imwhs.eatz_server.repository.eatzuser.EatzUserRepository;
import imwhs.eatz_server.repository.savedrecipe.SavedRecipeQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * 저장된 레시피(SavedRecipe)와 관련된 조회 기능을 제공하는 SavedRecipeQueryService 클래스입니다.
 * SavedRecipe에 대한 읽기 전용 쿼리 메서드를 제공합니다.
 */
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class SavedRecipeQueryService {

    private final SavedRecipeQueryRepository savedRecipeQueryRepository;

    private final EatzUserRepository userRepository;

    /**
     * 페이지 번호 및 크기 기본 값.
     * 응답 메시지에 포함시킬 레시피에 대해 페이징 처리를 하기 위해 정의합니다.
     */
    private static final int DEFAULT_PAGE_NUMBER = 0;
    private static final int DEFAULT_PAGE_SIZE = 10;

    @Transactional
    public List<SavedRecipeResponseDto> findSavedRecipesByUser(Long userId, Pageable pageable) {
        EatzUser user = userRepository.findById(userId)
                .orElseThrow(() -> new EatzUserNotFoundException("id " + userId + "에 해당하는 사용자가 존재하지 않습니다."));

        pageable = Optional.ofNullable(pageable).orElse(PageRequest.of(DEFAULT_PAGE_NUMBER, DEFAULT_PAGE_SIZE));

        List<SavedRecipe> foundSavedRecipes = savedRecipeQueryRepository.findByUserOrderByCreatedAtDesc(user, pageable);
        return foundSavedRecipes.stream()
                .map(savedRecipe -> new SavedRecipeResponseDto(savedRecipe)).toList();
    }

}
