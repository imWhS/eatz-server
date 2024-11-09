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
        validateIngredientName(dto.getName());

        Ingredient ingredient = new Ingredient(dto.getName());
        ingredientRepository.save(ingredient);

        Long categoryId = dto.getCategoryId();
        List<Long> childIds = dto.getChildIds();

        if (Objects.equals(categoryId, ingredient.getId())) {
            throw new IllegalArgumentException("재료 자신을 카테고리로 지정할 수 없습니다.");
        }

        // 카테고리를 지정합니다.
        Ingredient category = findIngredient(categoryId);
        ingredient.setCategory(category);

        // 하위 재료를 추가합니다.
        List<Ingredient> children = findChildren(childIds);

        for (Ingredient child : children) {
            ingredient.addChild(child);
        }

        return ingredient.getId();
    }

    /**
     * 재료를 수정합니다.
     * <p>
     *     Ingredient 엔티티 필드 별 값을 변경한 후, 리포지토리를 통해 변경 사항을 반영합니다.
     * </p>
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateIngredient(IngredientUpdateDto dto) {
        if (dto.getId() == null) {
            throw new IllegalArgumentException("수정할 재료의 ID는 필수 값입니다.");
        }

        validateIngredientName(dto.getName());

        Long id = dto.getId();
        Long categoryId = dto.getCategoryId();
        List<Long> childIds = dto.getChildIds();

        Ingredient ingredient = findIngredient(id);
        Ingredient category = findIngredient(categoryId);
        List<Ingredient> children = findChildren(childIds);

        ingredient.update(dto.getName(), category, children);
    }

    private Ingredient findIngredient(Long categoryId) {
        if (categoryId == null) return null;
        else return ingredientRepository.findById(categoryId)
                .orElseThrow(() -> new IngredientNotFoundException("id가 " + categoryId + "인 재료 엔티티를 찾을 수 없습니다."));
    }

    private List<Ingredient> findChildren(List<Long> childIds) {
        if (childIds == null) return null;

        List<Ingredient> children = ingredientRepository.findAllById(childIds);
        if (children.size() != childIds.size()) {
            throw new IllegalArgumentException("추가하려는 하위 재료 중, " +
                    Math.abs(children.size() - childIds.size()) + "개의 재료가 유효하지 않습니다.");
        }
        return children;
    }

    private void validateIngredientName(String name) {
        if (name == null) throw new IllegalArgumentException("재료의 이름은 필수 값입니다.");
    }

}
