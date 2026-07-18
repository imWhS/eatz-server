package imwhs.eatz_server.service.ingredient;

import imwhs.eatz_server.domain.eatzuser.EatzUser;
import imwhs.eatz_server.domain.Ingredient;
import imwhs.eatz_server.dto.CountResponse;
import imwhs.eatz_server.dto.ingredient.*;
import imwhs.eatz_server.dto.recipe.ingredient.IngredientRequirementDto;
import imwhs.eatz_server.repository.EatzUserRepository;
import imwhs.eatz_server.repository.ingredient.IngredientRepository;
import imwhs.eatz_server.repository.recipe.ingredient.RecipeIngredientRepository;
import imwhs.eatz_server.repository.pantry.PantryIngredientRepository;
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
public class IngredientQueryService {

    private final IngredientRepository ingredientRepository;
    private final PantryIngredientRepository pantryIngredientRepository;
    private final RecipeIngredientRepository recipeIngredientRepository;
    private final EatzUserRepository userRepository;

    /**
     * 특정 상위 재료의 기본 정보와 해당 상위 재료에 포함되어 있는 모든 재료의 기본 정보 목록을 가져옵니다.
     * <ul>
     *     <li> 사용자의 ID를 전달받으면, 상위 재료에 포함되어 있는 모든 재료에 대한 해당 사용자의 context를 포함합니다. </li>
     *     <li> 조회 대상 상위 재료에 해당하는 Ingredient 및 이와 연관 관계인 Ingredient.parent를 페치 조인하여 조회합니다. </li>
     *     <li> Ingredient.children은 DTO 생성자에서 지연 로딩(Lazy Loading)으로 처리됩니다. </li>
     * </ul>
     * @param id 재료의 ID
     * @param userId 사용자의 ID (게스트일 경우 null)
     * @return 특정 상위 재료의 기본 정보와 해당 상위 재료에 포함되어 있는 모든 재료의 기본 정보 목록
     */
    public IngredientBasicsInParentDto getAllBasicsInParent(Long id, Long userId) {
        // 상위 재료(재료)와 상위 재료가 포함되어 있는 더 상위의 상위 재료만 먼저 페치 조인으로 함께 가져옵니다.
        Ingredient parent = ingredientRepository.getWithParent(id);

        // 상위 재료(재료) 및 상위 재료가 포함되어 있는 더 상위의 상위 재료만 DTO에 먼저 초기화합니다.
        IngredientBasicsInParentDto dto = new IngredientBasicsInParentDto(parent);

        // 상위 재료(재료)에 포함되어 있는 모든 재료 목록을 가져온 후, DTO의 해당 필드에 초기화합니다.
        List<IngredientBasicDto> ingredients = ingredientRepository.findAllBasicsByParentId(id, userId);
        dto.setIngredients(ingredients);

        return dto;
    }

    /**
     * 특정 사용자가 좋아하는 모든 재료의 기본 정보 목록을 가져옵니다.
     * @param id 사용자의 ID
     * @param pageable 페이징 정보
     * @return 특정 사용자가 좋아하는 모든 재료의 기본 정보 목록
     */
    public Page<IngredientBasicDto> getAllLikedBasicsByUserId(Long id, Pageable pageable) {
        userRepository.validateExists(id);
        return ingredientRepository.findAllIngredientsByLikedUserId(id, pageable);
    }

    /**
     * 상위 재료에 속하지 않은 최상위 계층(root) 재료의 기본 정보 목록을 가져옵니다.
     * <ul>
     *     <li> 사용자의 ID를 전달받으면, 조회하려는 재료에 대한 해당 사용자의 context를 포함합니다. </li>
     * </ul>
     * @param userId 사용자의 ID (게스트일 경우 null)
     * @param pageable 페이징 정보
     * @return 상위 재료에 속하지 않은 최상위 계층(root) 재료의 기본 정보 목록
     */
    public Page<IngredientBasicDto> getAllRootBasics(Long userId, Pageable pageable) {
        return ingredientRepository.findAllRootBasics(userId, pageable);
    }

//    /**
//     * 이름으로 재료의 핵심 정보와 하위 재료 목록을 함께 가져옵니다.
//     * @param name 재료의 이름
//     * @return 조회된 재료의 핵심 정보와 하위 재료 목록
//     */
//    public IngredientEssentialWithChildrenDto getEssentialWithChildrenByName(String name) {
//        Ingredient ingredient = ingredientRepository.get(name);
//        return new IngredientEssentialWithChildrenDto(ingredient);
//    }

    /**
     * 검색어(이름)에 해당하는 재료의 기본 정보 목록을 검색합니다.
     * <ul>
     *     <li> 검색어에 포함된 모든 공백은 제거됩니다. </li>
     *     <li> 사용자의 ID를 전달받으면, 기본 정보에 해당 사용자의 context를 포함합니다. </li>
     * </ul>
     * @param keyword 검색어
     * @param userId 사용자의 ID (게스트일 경우 null)
     * @param pageable 페이징 정보
     * @return 검색된 재료의 기본 정보 목록과 페이징 정보
     */
    public Page<IngredientBasicDto> searchBasics(String keyword, Long userId, Pageable pageable) {
        if (keyword == null || keyword.isBlank()) {
            return new PageImpl<>(Collections.emptyList());
        }

        String keywordIgnoredBlanks = keyword.replaceAll("\\s+", "");

//        if (userID == null) {
//            return ingredientRepository.searchIngredientItems(keywordIgnoredBlanks, pageable);
//        } else {
//            userRepository.validateExists(userID);
//            return ingredientRepository.searchIngredientItemsForUser(keywordIgnoredBlanks, keyword, pageable);
//        }

        return ingredientRepository.searchBasics(keywordIgnoredBlanks, userId, pageable);
    }

    /**
     * ID에 해당하는 재료와 모든 하위 계층(hierarchy)의 재료 정보를 트리 구조로 가져옵니다.
     * @param id 트리 구조의 최상위(root)가 될 재료의 ID
     * @return 계층 구조가 반영된 재료 트리
     */
    public IngredientHierarchyDto getHierarchy(Long id) {
        List<Ingredient> ingredients = ingredientRepository.findAllAsHierarchy(id);
        return assembleIngredientsIntoHierarchy(ingredients);
    }

    public Page<IngredientBasicDto> getAllIngredientBasicsByUserId(Long id, Pageable pageable) {
        EatzUser user = userRepository.getReference(id);
        return pantryIngredientRepository.findAllByUser(user, pageable);
    }

    public CountResponse getAllIngredientCountByUserId(Long id) {
        EatzUser user = userRepository.getReference(id);
        return new CountResponse(pantryIngredientRepository.countIngredientUserByUser(user));
    }

    /**
     * 레시피를 요리하기 위해 준비해야 할 재료 정보 목록을 가져옵니다.
     * <ul>
     *     <li> 사용자의 ID를 전달받으면, 재료에 대한 해당 사용자의 context를 포함합니다. </li>
     * </ul>
     * @param id 레시피의 ID
     * @param userId 사용자의 ID
     * @return 레시피를 요리하기 위해 준비해야 할 재료 정보 목록
     */
    @Transactional(rollbackFor = Exception.class)
    public List<IngredientRequirementDto> getIngredientRequirementsByRecipeId(Long id, Long userId) {
        return recipeIngredientRepository.findAllIngredientRequirementsByRecipeId(id, userId);
    }

    /**
     * 하위 재료가 단순 목록(flat) 형태로 구성되어 있는 Ingredient 엔티티 목록을 계층 구조가 반영된 트리 형태로 변환합니다.
     * <ul>
     *     <li> 최상위 계층(root)에 해당하는 재료는 상위 재료에 포함되어 있지 않은 것으로 간주합니다. </li>
     * </ul>
     * @param ingredients 트리 형태로 변환할 재료 목록
     * @return 트리 형태로 변환된 하위 재료 목록(tree)을 가지는 IngredientTreeDTO
     */
    private IngredientHierarchyDto assembleIngredientsIntoHierarchy(List<Ingredient> ingredients) {
        if (ingredients == null || ingredients.isEmpty()) return null;

        // 파라미터로 받은 List<Ingredient> 타입의 ingredients를 반환 타입인 IngredientTreeDto로 변환할 때 사용할 Map을 선언합니다.
        // 재료의 ID를 key로 사용해, 하위 재료 또는 상위 재료로 추가할 재료를 빠르게 찾을 수 있게 하며,
        // 하위 재료 목록(IngredientTreeDto.tree)에 바로 사용할 수 있도록 value로 IngredientTreeDto 타입을 사용합니다.
        Map<Long, IngredientHierarchyDto> ingredientHierarchyByIdMap = new HashMap<>();

        // ingredients의 각 Ingredient를 Map<Long, IngredientTreeDto>에 맞게 ingredientMap에 채웁니다.
        for (Ingredient ingredient : ingredients) {
            ingredientHierarchyByIdMap.put(
                    ingredient.getId(),
                    new IngredientHierarchyDto(
                            ingredient.getId(),
                            ingredient.getName(),
                            IngredientEssentialDto.create(ingredient.getParent())
                    )
            );
        }

        IngredientHierarchyDto root = null;
        for (Ingredient ingredient : ingredients) {
            IngredientHierarchyDto current = ingredientHierarchyByIdMap.get(ingredient.getId());

            if (current.getParent() != null) {
                IngredientHierarchyDto parent = ingredientHierarchyByIdMap.get(current.getParent().getId());
                parent.getTree().add(current);
            } else if (root == null) {
                root = current;
            } else {
                throw new IllegalArgumentException("최상위 계층에 해당하는 재료가 2개 이상 있어요.");
            }
        }

        return root;
    }

    private IngredientHierarchyDto toIngredientTreeDtoOld(List<Ingredient> ingredients) {
        if (ingredients == null || ingredients.isEmpty()) return null;

        // 각 재료의 ID를 key로, 재료의 IngredientHierarchyDto를 value로 가지는 Map을 생성합니다.
        // 재료 별 상위 재료 또는 하위 재료 정보를 빠르게 참조하기 위해 사용됩니다.
        Map<Long, IngredientHierarchyDto> ingredientsMap = new HashMap<>();
        ingredients.forEach(ingredient ->
                ingredientsMap.put(
                        ingredient.getId(),
                        new IngredientHierarchyDto(
                                ingredient.getId(),
                                ingredient.getName(),
                                (ingredient.getParent() != null)
                                        ? IngredientEssentialDto.create(ingredient.getParent())
                                        : null,
                                new ArrayList<>()
                        )
                )
        );

        // 재료 별 상위 재료, 하위 재료(부모 - 자식) 관계를 설정합니다.
        // 재료의 정보를 담고 있는 DTO를 가져와서, 상위 재료에 해당하는 재료의 IngredientTreeResponseDto.children 목록에 추가합니다.

        // 재료 목록(ingredients)에서 최상위 계층인 루트에 해당하는 재료가 1개 존재하는지에 대한 여부를 저장합니다.
        boolean existsRoot = false;

        for (Ingredient ingredient : ingredients) {
            if (ingredient.getParent() == null) {
                // 현재 재료가 루트에 해당하는 재료인 경우: 먼저 최상위 계층에 해당하는 재료가 이미 존재하는지 확인합니다.
                if (existsRoot) {
                    // 루트에 해당하는 재료가 2개 이상이면, 단일 트리로 변환할 수 없어 실행을 중단합니다.
                    throw new IllegalArgumentException("루트에 해당하는 재료가 2개 이상이에요.");
                } else {
                    // 유일하게 최상위 계층에 해당하는 재료인 경우, 상위 재료가 지정돼있지 않기에 바로 목록의 다음 재료를 순회합니다.
                    existsRoot = true;
                    continue;
                }
            }

            // 현재 재료와 현재 재료가 속한 상위 재료 정보를 담고 있는 DTO를 가져옵니다.
            IngredientHierarchyDto current = ingredientsMap.get(ingredient.getId());
            IngredientHierarchyDto parent = ingredientsMap.get(current.getParent().getId());

            // 상위 재료 DTO의 children 필드에 현재 재료의 DTO를 설정합니다.
            parent.getTree().add(current);
        }

        // 최상위 계층(Root)에 해당하는 재료를 반환합니다.
        // 재료 목록에서 첫 번째 요소는 트리의 root에 해당하기에, 모든 하위 노드를 포함하는 트리 구조를 가집니다.
        return ingredientsMap.get(ingredients.get(0).getId());
    }

}
