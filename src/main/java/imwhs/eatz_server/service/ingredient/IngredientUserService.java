package imwhs.eatz_server.service.ingredient;

import imwhs.eatz_server.domain.Ingredient;
import imwhs.eatz_server.domain.IngredientRecipe;
import imwhs.eatz_server.domain.IngredientUser;
import imwhs.eatz_server.domain.eatzuser.EatzUser;
import imwhs.eatz_server.dto.ingredient.IngredientDto;
import imwhs.eatz_server.exception.EatzUserNotFoundException;
import imwhs.eatz_server.repository.eatzuser.EatzUserRepository;
import imwhs.eatz_server.repository.ingredient.IngredientRepository;
import imwhs.eatz_server.repository.ingredient.IngredientUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class IngredientUserService {

    private final IngredientUserRepository ingredientUserRepository;

    private final IngredientRepository ingredientRepository;

    private final EatzUserRepository userRepository;

    @Transactional
    public Long addIngredientToUser(Long ingredientId, Long userId) {
        Ingredient ingredient = ingredientRepository.findById(ingredientId).orElseThrow(IllegalArgumentException::new);
        EatzUser user = userRepository.findById(userId).orElseThrow(() -> new EatzUserNotFoundException(userId));

        if (!ingredientUserRepository.existsByIngredientAndUser(ingredient, user)) {
            throw new IllegalArgumentException("사용자(" + user.getUsername() + ")애 " +
                    "이미 재료(" + ingredient.getName() + ")가 추가되어 있어요.");
        }

        IngredientUser ingredientUser = IngredientUser.create(ingredient, user);
        return ingredientUserRepository.save(ingredientUser).getId();
    }

    @Transactional
    public List<Long> addIngredientsToUser(String username, List<Long> ingredientIds) {
        EatzUser user = userRepository.findByUsername(username).orElseThrow(() ->
                new EatzUserNotFoundException(username));
        List<Long> existingIngredientIdsByUser = ingredientUserRepository.findIngredientIdsByUser(user);

        List<Long> ingredientIdsToAdd = new ArrayList<>();
        for (Long ingredientId : ingredientIds) {
            if (!existingIngredientIdsByUser.contains(ingredientId)) {
                ingredientIdsToAdd.add(ingredientId);
            }
        }

        if (ingredientIdsToAdd.isEmpty()) {
            return List.of();
        }

        List<Ingredient> ingredients = ingredientRepository.findAllById(ingredientIdsToAdd);
        if (ingredients.isEmpty()) return List.of();

        List<IngredientUser> ingredientUser = ingredients.stream().map(ingredient -> {
            return IngredientUser.create(ingredient, user);
        }).toList();

        ingredientUserRepository.saveAll(ingredientUser);
        return ingredientIdsToAdd;
    }

    @Transactional
    public void removeIngredientsFromUser(String username, List<Long> ingredientIds) {
        EatzUser user = userRepository.findByUsername(username).orElseThrow(() ->
                new EatzUserNotFoundException(username));

        ingredientUserRepository.deleteByUserAndIngredientIds(user, ingredientIds);
    }

    public List<IngredientDto> getIngredients(String username) {
        EatzUser user = userRepository.findByUsername(username).orElseThrow(() ->
                new EatzUserNotFoundException(username));

        return ingredientUserRepository.findIngredientsByUser(user);
    }

}
