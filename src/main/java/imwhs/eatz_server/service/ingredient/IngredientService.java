package imwhs.eatz_server.service.ingredient;

import imwhs.eatz_server.domain.eatzuser.EatzUserRole;
import imwhs.eatz_server.domain.ingredient.Ingredient;
import imwhs.eatz_server.dto.ingredient.*;
import imwhs.eatz_server.exception.EatzUserNotFoundException;
import imwhs.eatz_server.exception.IngredientNotFoundException;
import imwhs.eatz_server.repository.eatzuser.EatzUserRepository;
import imwhs.eatz_server.repository.ingredient.IngredientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class IngredientService {

    private final IngredientRepository ingredientRepository;

    private final EatzUserRepository userRepository;

    /**
     * 새 재료를 등록합니다.
     * @param adminId 요청한 관리자 ID.
     * @param name 재료 이름.
     * @param categoryId 재료를 포함시킬 카테고리의 ID.
     * @param childIds 하위에 포함시킬 재료들의 ID 목록.
     * @return 등록 완료된 재료의 ID.
     * @throws IngredientNotFoundException 유효하지 않은 ID의 재료를 카테고리 또는 하위 재료로서 추가하려는 경우.
     */
    @Transactional
    public Long register(Long adminId, String name, Long categoryId, List<Long> childIds) {
        verifyAdminRole(adminId);

        // 사용하려는 재료 이름의 유효성을 검증합니다.
        validateIngredientName(name, categoryId);

        // DTO로 재료 엔티티를 생성하고 저장합니다.
        Ingredient ingredient = new Ingredient(name);
        ingredientRepository.save(ingredient);

        // 재료에 설정할 카테고리 정보가 DTO에 포함되어 있는 경우: 카테고리를 설정합니다.
        if (categoryId != null) {
            if (Objects.equals(categoryId, ingredient.getId())) {
                throw new IllegalArgumentException("재료 자신을 카테고리로 설정할 수 없습니다.");
            }

            // 재료와 카테고리 간 양방향 연관 관계를 설정합니다.
            Ingredient category = getIngredient(categoryId);
            ingredient.setCategory(category);
        }

        // 재료에 설정할 하위 재료 정보가 DTO에 포함되어 있는 경우: 하위 재료를 추가합니다.
        if (childIds != null && !childIds.isEmpty()) {
            List<Ingredient> children = toEntities(childIds);

            // 재료와 하위 재료 간 양방향 연관 관계를 설정합니다.
            for (Ingredient child : children) {
                ingredient.addChild(child);
            }
        }

        return ingredient.getId();
    }

    private void verifyAdminRole(Long adminId) {
        if (!userRepository.existsAdminById(adminId)) {
            throw new EatzUserNotFoundException(adminId, EatzUserRole.ROLE_ADMIN);
        }
    }

    /**
     * 재료를 업데이트합니다.
     * <p>
     *     Ingredient 엔티티 필드 별 값을 변경한 후, 리포지토리를 통해 변경 사항을 반영합니다.
     * </p>
     */
    @Transactional(rollbackFor = Exception.class)
    public void update(IngredientUpdateDto dto) {
        // 업데이트할 재료의 ID가 전달되지 않은 경우, 더 이상 진행하지 않습니다.
        validateIngredientId(dto.getId());

        // 사용하려는 재료 이름의 유효성을 검증합니다.
        validateIngredientName(dto.getName(), dto.getCategoryId());

        // ID로 업데이트할 재료의 엔티티를 조회합니다.
        Ingredient ingredient = getIngredient(dto.getId());

        // 카테고리로 설정할 재료의 엔티티를 조회합니다.
        Long categoryId = dto.getCategoryId();
        Ingredient category = getIngredient(categoryId);

        // 하위로 포함시킬 모든 재료 목록을 조회합니다.
        List<Long> childIds = dto.getChildIds();
        List<Ingredient> children = toEntities(childIds);

        // 엔티티를 통해 재료 정보 업데이트를 반영합니다.
        ingredient.update(dto.getName(), category, children);
    }

    /**
     * 재료를 삭제합니다.
     * @param id 삭제하려는 재료의 ID.
     * @throws IngredientNotFoundException ID에 해당하는 재료가 존재하지 않는 경우.
     */
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        // 삭제할 재료의 ID가 전달되지 않은 경우, 더 이상 진행하지 않습니다.
        validateIngredientId(id);

        // 재료 엔티티를 조회합니다.
        Ingredient ingredient = ingredientRepository.findById(id)
                .orElseThrow(() -> new IngredientNotFoundException("id가 " + id + "인 재료를 찾지 못했습니다."));

        // 재료에 설정된 카테고리를 해제합니다.
        ingredient.removeCategory();

        // 재료의 하위 재료를 모두 제거합니다.
        ingredient.removeChildren();

        // 엔티티를 삭제합니다.
        ingredientRepository.deleteById(id);
    }

    /**
     * 재료와 이와 연관 관계인 카테고리를 함께 조회합니다.
     * <ul>
     *     <li>Ingredient와 이와 연관 관계인 Ingredient.category를 페치 조인한 데이터를 조회합니다.</li>
     *     <li>Ingredient.children은 IngredientDto 생성자에서 지연 로딩됩니다.</li>
     * </ul>
     * @param id 조회하려는 재료의 ID.
     * @return 조회된 재료의 정보를 담고 있는 IngredientResponseDto.
     */
    public IngredientWithCategoryChildDto findById(Long id) {
        Ingredient ingredient = ingredientRepository.findWithCategoryById(id)
                .orElseThrow(() -> new IngredientNotFoundException(id));
        return new IngredientWithCategoryChildDto(ingredient);
    }

    /**
     * 이름으로 재료와 재료의 하위 재료를 함께 조회합니다.<br/>
     * @param name 조회하려는 재료의 이름.
     * @return 조회된 재료의 정보를 담고 있는 IngredientWithChildDto.
     */
    public IngredientWithChildDto findByName(String name) {
        Ingredient ingredient = ingredientRepository.findByName(name)
                .orElseThrow(() -> new IngredientNotFoundException(name));

        return new IngredientWithChildDto(ingredient);
    }

    /**
     * ID에 해당하는 재료와 하위 재료 계층을 구성하는 모든 재료 엔티티를 함께 조회합니다.
     * @param id 조회하려는 재료의 ID.
     * @return IngredientTreeResponseDto.
     */
    public IngredientTreeDto getTreeById(Long id) {
        List<Ingredient> ingredientWithAllChildren = ingredientRepository.findIngredientTree(id);
        return toTreeResponseDto(ingredientWithAllChildren);
    }

    /**
     * ID로 재료 엔티티를 가져옵니다.<br/>
     * IngredientService 내부에서만 사용하는 메서드로, ID가 null이면 그대로 null을 반환합니다.
     * @param id 조회하려는 재료의 ID.
     * @return 재료 엔티티.
     * @throws IngredientNotFoundException ID에 해당하는 재료 엔티티가 존재하지 않을 경우.
     */
    private Ingredient getIngredient(Long id) {
        if (id == null) return null;
        else return ingredientRepository.findById(id)
                .orElseThrow(() -> new IngredientNotFoundException("id가 " + id + "인 재료를 찾을 수 없습니다."));
    }

    /**
     * 재료 ID의 유효성을 검증합니다.
     * @throws IllegalArgumentException ID가 유효하지 않은 경우
     */
    private void validateIngredientId(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("재료의 id가 유효하지 않습니다.");
        }
    }

    /**
     * 리포지토리를 통해 재료 ID 목록을 재료 엔티티 목록으로 변환합니다.<br/>
     * 재료 ID 목록이 null이거나 비어있으면 null을 반환합니다.
     * @param ids 재료 ID 목록.
     * @return 재료 엔티티 목록.
     * @throws IllegalArgumentException 재료 ID 목록 내 유효하지 않은 ID가 1개 이상 존재해서,
     * 재료 ID 목록의 전체 항목을 재료 엔티티 목록으로 변환하지 못한 경우.
     */
    private List<Ingredient> toEntities(List<Long> ids) {
        if (ids == null || ids.isEmpty()) return null;

        List<Ingredient> children = ingredientRepository.findAllById(ids);
        if (children.size() != ids.size()) {
            throw new IllegalArgumentException("추가하려는 하위 재료 중, " +
                    Math.abs(children.size() - ids.size()) + "개의 재료가 유효하지 않습니다.");
        }
        return children;
    }

    /**
     * 재료의 이름이 유효한지 검증합니다.
     * @param name 검증하려는 재료의 이름.
     * @param categoryId 검증하려는 재료의 카테고리 ID.
     * @throws IllegalArgumentException 재료의 이름이 null이거나, 해당 카테고리에 동일한 이름이 존재하는 경우.
     */
    private void validateIngredientName(String name, Long categoryId) {
        if (name == null) throw new IllegalArgumentException("재료의 이름은 필수 값입니다.");
        if (categoryId != null && (ingredientRepository.existsByNameAndCategoryId(name, categoryId)))
            throw new IllegalArgumentException("이미 해당 카테고리에 동일한 이름의 재료가 존재합니다.");
    }

    /**
     * 하위 재료가 flat 형태로 저장되어 있는 재료 목록을 게층 구조의 트리 형태가 반영된 DTO로 변환합니다.
     * 최상위 계층(Root)에 해당하는 재료는 카테고리에 포함되지 않은 것으로 간주합니다.
     * @param ingredients 트리 형태로 변환할 재료 목록
     * @return 트리 형태로 변환된 IngredientTreeResponseDto.
     */
    private IngredientTreeDto toTreeResponseDto(List<Ingredient> ingredients) {
        if (ingredients == null || ingredients.isEmpty()) return null;

        /**
         * 각 재료의 ID를 key로, 재료를 IngredientTreeResponseDto로 변환한 객체를 value로 가지는 Map을 생성합니다.
         * 재료 별 카테고리 또는 하위 재료 정보를 빠르게 참조하기 위해 사용됩니다.
         */
        Map<Long, IngredientTreeDto> ingredientsMap = new HashMap<>();
        ingredients.forEach(ingredient ->
                ingredientsMap.put(
                        ingredient.getId(),
                        new IngredientTreeDto(
                                ingredient.getId(),
                                ingredient.getName(),
                                (ingredient.getCategory() != null)
                                        ? new IngredientCategoryDto(ingredient.getCategory())
                                        : null,
                                new ArrayList<>()
                        )
                )
        );

        /**
         * 재료 별 카테고리, 하위 재료(부모 - 자식) 관계를 설정합니다.
         * 재료의 정보를 담고 있는 DTO를 가져와서, 카테고리에 해당하는 재료의 IngredientTreeResponseDto.children 목록에 추가합니다.
         */
        // 재료 목록(ingredients)에서 최상위 계층인 루트에 해당하는 재료가 1개 존재하는지에 대한 여부를 저장합니다.
        boolean existsRoot = false;

        for (Ingredient ingredient : ingredients) {
            if (ingredient.getCategory() == null) {
                // 현재 재료가 루트에 해당하는 재료인 경우: 먼저 최상위 계층에 해당하는 재료가 이미 존재하는지 확인합니다.
                if (existsRoot) {
                    // 루트에 해당하는 재료가 2개 이상이면, 단일 트리로 변환할 수 없어 실행을 중단합니다.
                    throw new IllegalArgumentException("루트에 해당하는 재료가 2개 이상입니다.");
                } else {
                    // 유일하게 최상위 계층에 해당하는 재료인 경우, 카테고리가 지정돼있지 않기에 바로 목록의 다음 재료를 순회합니다.
                    existsRoot = true;
                    continue;
                }
            }

            // 현재 재료와 현재 재료가 속한 카테고리 정보를 담고 있는 DTO를 가져옵니다.
            IngredientTreeDto current = ingredientsMap.get(ingredient.getId());
            IngredientTreeDto category = ingredientsMap.get(current.getCategory().getId());

            // 카테고리 DTO의 children 필드에 현재 재료의 DTO를 설정합니다.
            category.getChildren().add(current);
        }

        /**
         * 최상위 계층(Root)에 해당하는 재료를 반환합니다.
         * 재료 목록에서 첫 번째 요소는 트리의 root에 해당하기에, 모든 하위 노드를 포함하는 트리 구조를 가집니다.
         */
        return ingredientsMap.get(ingredients.get(0).getId());
    }


}
