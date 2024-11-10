package imwhs.eatz_server.service;

import imwhs.eatz_server.domain.Ingredient;
import imwhs.eatz_server.dto.ingredient.IngredientCreateDto;
import imwhs.eatz_server.dto.ingredient.IngredientUpdateDto;
import imwhs.eatz_server.exception.IngredientNotFoundException;
import imwhs.eatz_server.repository.ingredient.IngredientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class IngredientService {

    private final IngredientRepository ingredientRepository;

    /**
     * 새 재료를 등록합니다.
     * <p>
     *     Ingredient 엔티티를 생성하고, 리포지토리를 통해 저장합니다.
     * </p>
     * @param dto 재료 생성 DTO.
     * @return 생성된 재료의 식별자.
     * @throws IngredientNotFoundException 유효하지 않은 식별자의 재료를 카테고리 또는 하위 재료로서 추가하려는 경우.
     * TODO: 같은 이름을 가진 재료에 대한 처리
     */
    @Transactional
    public Long registerIngredient(IngredientCreateDto dto) {
        // 재료 이름의 유효성을 검증합니다.
        validateIngredientName(dto.getName());

        // DTO로 재료 엔티티를 생성하고 저장합니다.
        Ingredient ingredient = new Ingredient(dto.getName());
        ingredientRepository.save(ingredient);

        // 재료에 설정할 카테고리 정보가 DTO에 포함되어 있는 경우: 카테고리를 설정합니다.
        if (dto.getCategoryId() != null) {
            Long categoryId = dto.getCategoryId();

            if (Objects.equals(categoryId, ingredient.getId())) {
                throw new IllegalArgumentException("재료 자신을 카테고리로 설정할 수 없습니다.");
            }

            // 재료와 카테고리 간 양방향 연관 관계를 설정합니다.
            Ingredient category = getIngredient(categoryId);
            ingredient.setCategory(category);
        }

        // 재료에 설정할 하위 재료 정보가 DTO에 포함되어 있는 경우: 하위 재료를 추가합니다.
        if (dto.getChildIds() != null && !dto.getChildIds().isEmpty()) {
            List<Long> childIds = dto.getChildIds();
            List<Ingredient> children = convertToEntities(childIds);

            // 재료와 하위 재료 간 양방향 연관 관계를 설정합니다.
            for (Ingredient child : children) {
                ingredient.addChild(child);
            }
        }

        return ingredient.getId();
    }

    /**
     * 재료를 업데이트합니다.
     * <p>
     *     Ingredient 엔티티 필드 별 값을 변경한 후, 리포지토리를 통해 변경 사항을 반영합니다.
     * </p>
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateIngredient(IngredientUpdateDto dto) {
        // 수정할 재료의 식별자가 전달되지 않은 경우, 더 이상 진행하지 않습니다.
        validateIngredientId(dto.getId());

        // 재료 이름의 유효성을 검증합니다.
        validateIngredientName(dto.getName());

        // 식별자로 수정할 재료의 엔티티를 조회합니다.
        Ingredient ingredient = getIngredient(dto.getId());

        // 카테고리로 설정할 재료의 엔티티를 조회합니다.
        Long categoryId = dto.getCategoryId();
        Ingredient category = getIngredient(categoryId);

        // 하위로 포함시킬 모든 재료 목록을 조회합니다.
        List<Long> childIds = dto.getChildIds();
        List<Ingredient> children = convertToEntities(childIds);

        // 엔티티를 통해 재료 정보 업데이트를 반영합니다.
        ingredient.update(dto.getName(), category, children);
    }

    /**
     * 재료를 삭제합니다.
     * @param id 삭제하려는 재료의 식별자.
     * @throws IngredientNotFoundException 식별자에 해당하는 재료가 존재하지 않는 경우
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteIngredient(Long id) {
        // 삭제할 재료의 식별자가 전달되지 않은 경우, 더 이상 진행하지 않습니다.
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

    // TODO: 다른 엔티티 삭제 시, 연관 관계인 엔티티에 대한 처리?


    public IngredientResponseDto findIngredient(Long id) {

    }

    /**
     * 식별자로 재료 엔티티를 가져옵니다.<br/>
     * IngredientService 내부에서만 사용하는 메서드로, 식별자가 null이면 그대로 null을 반환합니다.
     * @param id 조회하려는 재료의 식별자.
     * @return 재료 엔티티.
     * @throws IngredientNotFoundException 식별자에 해당하는 재료 엔티티가 존재하지 않을 경우.
     */
    private Ingredient getIngredient(Long id) {
        if (id == null) return null;
        else return ingredientRepository.findById(id)
                .orElseThrow(() -> new IngredientNotFoundException("id가 " + id + "인 재료 엔티티를 찾을 수 없습니다."));
    }

    /**
     * 재료 식별자의 유효성을 검증합니다.
     * @throws IllegalArgumentException 식별자가 유효하지 않은 경우
     */
    private void validateIngredientId(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("재료의 id가 유효하지 않습니다.");
        }
    }

    /**
     * 리포지토리를 통해 재료 식별자 목록을 재료 엔티티 목록으로 변환합니다.<br/>
     * 재료 식별자 목록이 null이거나 비어있으면 null을 반환합니다.
     * @param ids 재료 식별자 목록.
     * @return 재료 엔티티 목록.
     * @throws IllegalArgumentException 재료 식별자 목록 내 유효하지 않은 식별자가 1개 이상 존재해서,
     * 재료 식별자 목록의 전체 항목을 재료 엔티티 목록으로 변환하지 못한 경우.
     */
    private List<Ingredient> convertToEntities(List<Long> ids) {
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
     * @param name 검증하려는 재료의 이름
     */
    private void validateIngredientName(String name) {
        if (name == null) throw new IllegalArgumentException("재료의 이름은 필수 값입니다.");
    }

}
