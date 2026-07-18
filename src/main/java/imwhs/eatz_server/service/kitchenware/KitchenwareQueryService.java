package imwhs.eatz_server.service.kitchenware;

import imwhs.eatz_server.domain.eatzuser.EatzUser;
import imwhs.eatz_server.domain.Kitchenware;
import imwhs.eatz_server.dto.CountResponse;
import imwhs.eatz_server.dto.kitchenware.KitchenwareBasicDto;
import imwhs.eatz_server.dto.recipe.kitchenware.KitchenwareRequirementDto;
import imwhs.eatz_server.exception.EatzUserNotFoundException;
import imwhs.eatz_server.repository.EatzUserRepository;
import imwhs.eatz_server.repository.kitchenware.KitchenwareRepository;
import imwhs.eatz_server.repository.pantry.PantryKitchenwareRepository;
import imwhs.eatz_server.repository.recipe.kitchenware.RecipeKitchenwareRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * 재료(Ingredient) 관련 정보를 조회하기 위한 서비스입니다.
 * Ingredient에 대한 읽기 전용 쿼리 메서드를 제공합니다.
 */
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class KitchenwareQueryService {

    private final KitchenwareRepository kitchenwareRepository;
    private final PantryKitchenwareRepository pantryKitchenwareRepository;
    private final RecipeKitchenwareRepository kitchenwareRecipeRepository;
    private final EatzUserRepository userRepository;

    /**
     * 모든 도구의 기본 정보 목록을 가져옵니다.
     * <ul>
     *     <li> 사용자의 ID를 전달받으면, 모든 도구에 대한 해당 사용자의 context를 포함합니다. </li>
     * </ul>
     * @param userId 사용자의 ID (게스트일 경우 null)
     * @param pageable 페이징 정보
     * @return 도구의 기본 정보 목록
     */
    public Page<KitchenwareBasicDto> getAllBasics(Long userId, Pageable pageable) {
        if (userId == null) {
            return kitchenwareRepository.findAllForGuest(pageable);
        } else {
            userRepository.existsById(userId);
            return kitchenwareRepository.findAllForUser(userId, pageable);
        }
    }

    /**
     * 도구의 ID로 도구의 기본 정보를 가져옵니다.
     * <ul>
     *     <li> 사용자의 ID를 전달받으면, 조회하려는 도구에 대한 해당 사용자의 context를 포함합니다. </li>
     * </ul>
     * @param id 도구의 ID
     * @return 조회된 도구의 기본 정보
     */
    public KitchenwareBasicDto get(Long id) {
        Kitchenware kitchenware = kitchenwareRepository.get(id);
        return new KitchenwareBasicDto(kitchenware);
    }

    /**
     * 도구의 이름으로 도구의 기본 정보를 가져옵니다.
     * <ul>
     *     <li> 사용자의 ID를 전달받으면, 조회하려는 도구에 대한 해당 사용자의 context를 포함합니다. </li>
     * </ul>
     * @param name 도구의 이름
     * @return 조회된 도구의 기본 정보
     */
    public KitchenwareBasicDto get(String name) {
        Kitchenware kitchenware = kitchenwareRepository.get(name);
        return new KitchenwareBasicDto(kitchenware);
    }

    /**
     * 검색어(이름)에 해당하는 도구의 기본 정보 목록을 검색합니다.
     * <ul>
     *     <li> 검색어에 포함된 모든 공백은 제거됩니다. </li>
     *     <li> 사용자의 ID를 전달받으면, 기본 정보에 해당 사용자의 context를 포함합니다. </li>
     * </ul>
     * @param keyword 검색어
     * @param userId 사용자의 ID (게스트일 경우 null)
     * @param pageable 페이징 정보
     * @return 검색된 재료의 기본 정보 목록과 페이징 정보
     */
    public Page<KitchenwareBasicDto> searchBasics(String keyword, Long userId, Pageable pageable) {
        if (keyword == null || keyword.isBlank()) {
            return new PageImpl<>(Collections.emptyList());
        }

        String keywordIgnoredBlanks = keyword.replaceAll("\\s+", "");

        return kitchenwareRepository.searchBasics(keywordIgnoredBlanks, userId, pageable);
    }

    public Page<KitchenwareBasicDto> getAllKitchenwares(Long id, Pageable pageable) {
        EatzUser user = userRepository.findById(id).orElseThrow(() ->
                new EatzUserNotFoundException(id));

        return pantryKitchenwareRepository.findAllByUser(user, pageable);
    }

    public CountResponse getAllKitchenwareCount(Long id) {
        EatzUser user = userRepository.findById(id).orElseThrow(() ->
                new EatzUserNotFoundException(id));

        return new CountResponse(pantryKitchenwareRepository.countKitchenwareUserByUser(user));
    }

    /**
     * 레시피를 요리하기 위해 준비해야 할 도구 정보 목록을 가져옵니다.
     * <ul>
     *     <li> 사용자의 ID를 전달받으면, 도구에 대한 해당 사용자의 context를 포함합니다. </li>
     * </ul>
     * @param id 레시피의 ID
     * @param userId 사용자의 ID
     * @return 레시피를 요리하기 위해 준비해야 할 도구 정보 목록
     */
    @Transactional(rollbackFor = Exception.class)
    public List<KitchenwareRequirementDto> getKitchenwareRequirementsByRecipeId(Long id, Long userId) {
        return kitchenwareRecipeRepository.findAllKitchenwareRequirementsByRecipeId(id, userId);
    }

}
