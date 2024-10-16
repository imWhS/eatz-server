package imwhs.eatz_server.service;

import imwhs.eatz_server.domain.Ingredient;
import imwhs.eatz_server.dto.ingredient.CreateIngredientDto;
import imwhs.eatz_server.exception.IngredientNotFoundException;
import imwhs.eatz_server.exception.RecipeNotFoundException;
import imwhs.eatz_server.repository.ingredient.IngredientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class IngredientService {

    private final IngredientRepository ingredientRepository;

    public void registerIngredient(CreateIngredientDto dto) {
        Long parentId = dto.getParentId();
        Ingredient parent = null;

        if (parentId != null) {
            parent = ingredientRepository.findById(parentId)
                    .orElseThrow(() -> new IngredientNotFoundException("id가 " + parentId + "인 카테고리를 찾을 수 업습니다."));
        }

        Ingredient ingredient = new Ingredient(dto.getName(), parent, null);

        if (dto.getChildIds() != null) {
            for (Long childId : dto.getChildIds()) {
                Ingredient child = ingredientRepository.findById(childId)
                        .orElseThrow(() -> new IngredientNotFoundException("id가 " + childId + "인 재료를 찾을 수 없습니다."));
                ingredient.addChild(child);
            }
        }

        ingredientRepository.save(ingredient);
    }

}
