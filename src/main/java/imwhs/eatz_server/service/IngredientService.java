package imwhs.eatz_server.service;

import imwhs.eatz_server.domain.Ingredient;
import imwhs.eatz_server.dto.ingredient.CreateIngredientDto;
import imwhs.eatz_server.exception.IngredientNotFoundException;
import imwhs.eatz_server.repository.ingredient.IngredientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class IngredientService {

    private final IngredientRepository ingredientRepository;

    /**
     * 새 재료 등록.
     * <p>
     *     Ingredient 엔티티를 생성하고 리포지토리를 통해 저장합니다.
     * </p>
     * @param dto 재료 생성 DTO
     */
    public void registerIngredient(CreateIngredientDto dto) {
//        Long parentId = dto.getParentId();
//        Ingredient parent = null;
//
//        if (parentId != null) {
//            // 생성할 재료의 카테고리를 지정한 경우, 해당 카테고리에 대한 엔티티를 조회합니다.
//            parent = ingredientRepository.findById(parentId)
//                    .orElseThrow(() -> new IngredientNotFoundException("id가 " + parentId + "인 카테고리를 찾을 수 업습니다."));
//        }
//
//        // 엔티티를 생성합니다.
//        Ingredient ingredient = new Ingredient(dto.getName(), parent, null);
//
//        if (dto.getChildIds() != null) {
//            // 1개 이상의 하위 재료를 지정한 경우, 각 재료에 대한 엔티티를 조회합니다.
//            for (Long childId : dto.getChildIds()) {
//                Ingredient child = ingredientRepository.findById(childId)
//                        .orElseThrow(() -> new IngredientNotFoundException("id가 " + childId + "인 재료를 찾을 수 없습니다."));
//
//                // 재료를 현재 생성하고자 하는 엔티티의 하위 재료로 등록합니다.
//                ingredient.addChild(child);
//            }
//        }
//
//        // 엔티티를 저장합니다.
//        ingredientRepository.save(ingredient);
    }

}
