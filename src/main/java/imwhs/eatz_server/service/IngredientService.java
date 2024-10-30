package imwhs.eatz_server.service;

import imwhs.eatz_server.domain.Ingredient;
import imwhs.eatz_server.dto.ingredient.CreateIngredientDto;
import imwhs.eatz_server.dto.ingredient.UpdateIngredientDto;
import imwhs.eatz_server.exception.IngredientNotFoundException;
import imwhs.eatz_server.repository.ingredient.IngredientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class IngredientService {

    private final IngredientRepository ingredientRepository;

    /**
     * 새 재료 등록
     * <p>
     *     Ingredient 엔티티를 생성하고, 리포지토리를 통해 저장합니다.
     * </p>
     * @param dto 재료 생성 DTO
     * @return 생성된 재료의 ID
     * @throws IngredientNotFoundException 유효하지 않은 ID의 재료를 카테고리 또는 하위 재료로서 추가하려는 경우
     * TODO: 같은 이름을 가진 재료에 대한 처리
     */
    @Transactional
    public Long registerIngredient(CreateIngredientDto dto) {
        if (dto.getName() == null) {
            throw new IllegalArgumentException("재료의 이름은 필수 값입니다.");
        }

        Ingredient ingredient = new Ingredient(dto.getName());
        ingredientRepository.save(ingredient);

        Long categoryId = dto.getCategoryId();

        if (Objects.equals(categoryId, ingredient.getId())) {
            throw new IllegalArgumentException("재료 자신을 카테고리로 지정할 수 없습니다.");
        }

        // 카테고리를 지정합니다.
        if (categoryId != null) {
            Ingredient category = ingredientRepository.findById(categoryId)
                    .orElseThrow(() -> new IngredientNotFoundException("id가 " + categoryId + "인 카테고리를 찾을 수 없습니다."));
            ingredient.setCategory(category);

        }

        // 하위 재료를 추가합니다.
        if (dto.getChildIds() != null && dto.getChildIds().size() > 0) {
            List<Ingredient> children = ingredientRepository.findAllById(dto.getChildIds());
            if (children.size() != dto.getChildIds().size()) {
                throw new IllegalArgumentException("하위 재료 중 일부를 찾을 수 없습니다.");
            }

            for (Ingredient child : children) {
                ingredient.addChild(child);
            }
        }

        return ingredient.getId();
    }

    /**
     * 재료 수정
     * <p>
     *     Ingredient 엔티티 필드 별 값을 변경한 후, 리포지토리를 통해 변경 사항을 반영합니다.
     * </p>
     */
    @Transactional
    public void updateIngredient(UpdateIngredientDto dto) {
        if (dto.getId() == null) {
            throw new IllegalArgumentException("수정할 재료의 ID는 필수 값입니다.");
        }

        Long id = dto.getId();

        Ingredient ingredient = ingredientRepository.findById(id)
                .orElseThrow(() -> new IngredientNotFoundException("id가 " + id + "인 재료를 찾을 수 없습니다."));

        Ingredient category = null;
        if (dto.getCategoryId() != null) {
            Long categoryId = dto.getCategoryId();
            category = ingredientRepository.findById(categoryId)
                    .orElseThrow(() -> new IngredientNotFoundException("id가 " + id + "인 카테고리를 찾을 수 없습니다."));
        }

        List<Ingredient> children = new ArrayList<>();
        if (dto.getChildIds() != null && dto.getChildIds().size() > 0) {
            children = ingredientRepository.findAllById(dto.getChildIds());

            for (Ingredient child : children) {
                System.out.println("하위 재료가 다음으로 변경됩니다: " + child.getName());
            }
        }

        ingredient.update(dto.getName(), category, children);
    }

}
