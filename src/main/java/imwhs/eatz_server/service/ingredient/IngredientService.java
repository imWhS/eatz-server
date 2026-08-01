package imwhs.eatz_server.service.ingredient;

import imwhs.eatz_server.domain.Ingredient;
import imwhs.eatz_server.dto.ingredient.*;
import imwhs.eatz_server.repository.EatzUserRepository;
import imwhs.eatz_server.repository.ingredient.IngredientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * 재료(Ingredient) 상태를 변경할 수 있는 서비스를 제공합니다.
 * <ul>
 *     <li> Ingredient 생성 뿐 아니라, 수정, 삭제 등 엔티티 데이터를 변경하는 쓰기 전용 비즈니스 로직을 담당합니다. </li>
 *     <li> 읽기 전용 비즈니스 로직은 IngredientQueryService에서 처리합니다. </li>
 * </ul>
 */
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class IngredientService {

    private final IngredientRepository ingredientRepository;
    private final EatzUserRepository userRepository;

    /**
     * 새 재료를 등록합니다.
     * @param adminId 관리자의 ID
     * @param name 재료의 이름
     * @param parentId 재료를 포함시킬 상위 재료의 ID
     * @param isParentCoupled 상위 재료와의 커플링 여부. 상위 재료의 ID가 null이면 false로 설정됩니다.
     * @param childIds 하위에 포함시킬 재료들의 ID 목록
     * @return 등록 완료된 재료의 생성 정보를 담은 응답 DTO
     */
    @Transactional(rollbackFor = Exception.class)
    public IngredientCreationInfoResponse register(
            Long adminId,
            String name,
            Long parentId,
            Boolean isParentCoupled,
            List<Long> childIds) {
        userRepository.validateExistsAsAdmin(adminId);

        // 사용하려는 재료 이름의 유효성을 검증합니다.
        ingredientRepository.validateDuplicates(name, parentId);

        // DTO로 재료 엔티티를 생성하고 저장합니다.
        Ingredient ingredient = Ingredient.create(name);
        ingredientRepository.save(ingredient);

        // 재료에 설정할 상위 재료 정보가 DTO에 포함되어 있는 경우: 상위 재료를 설정합니다.
        if (parentId != null) {
            if (Objects.equals(parentId, ingredient.getId())) {
                throw new IllegalArgumentException("재료 자신을 상위 재료로 설정할 수 없어요.");
            }

            // 재료와 상위 재료 간 양방향 연관 관계를 설정합니다.
            Ingredient parent = ingredientRepository.getReference(parentId);
            ingredient.setParent(parent);

            // 재료와 상위 재료와의 커플링 여부를 설정합니다.
            ingredient.updateIsParentCoupled(isParentCoupled);
        }

        // 재료에 설정할 하위 재료 정보가 DTO에 포함되어 있는 경우: 하위 재료를 추가합니다.
        if (childIds != null && !childIds.isEmpty()) {
            List<Ingredient> children = getIngredients(childIds);

            // 재료와 하위 재료 간 양방향 연관 관계를 설정합니다.
            for (Ingredient child : children) {
                ingredient.addChild(child);
            }
        }

        return new IngredientCreationInfoResponse(ingredient);
    }

    /**
     * 재료를 업데이트합니다.
     * @param adminId 관리자의 ID
     * @param id 재료의 ID
     * @param name 재료의 새 이름. 필수 항목입니다.
     * @param parentId 재료를 포함시킬 새 상위 재료의 ID. null이면 아무 것도 업데이트하지 않습니다.
     * @param isParentCoupled 상위 재료와의 커플링 여부. null이면 아무 것도 업데이트하지 않습니다.
     *                        상위 재료의 ID가 null이면 false로 설정됩니다.
     * @param childIds 하위에 포함시킬 새 하위 재료 ID 목록.
     *                 빈 컬렉션이면 재료의 모든 기존 하위 재료가 지워집니다. null이면 아무 것도 업데이트하지 않습니다.
     * @return 업데이트 완료된 재료의 ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void update(
            Long adminId,
            long id,
            String name,
            Long parentId,
            Boolean isParentCoupled,
            List<Long> childIds) {
        userRepository.validateExistsAsAdmin(adminId);

        // ID로 업데이트할 재료의 엔티티를 조회합니다.
        Ingredient ingredient = ingredientRepository.get(id);

        // 새 이름으로 변경합니다.
        ingredient.updateName(name);

        if (parentId != null) {
            // 사용하려는 재료 이름의 유효성을 검증합니다.
            ingredientRepository.validateDuplicates(name, parentId);

            // 상위 재료로 설정할 재료의 엔티티를 조회합니다.
            Ingredient parent = ingredientRepository.getReference(parentId);

            // 새 상위 재료로 이동합니다.
            ingredient.setParent(parent);

            // 재료와 상위 재료와의 커플링 여부를 설정합니다.
            ingredient.updateIsParentCoupled(isParentCoupled);
        }

        // 하위로 포함시킬 모든 재료 목록을 조회합니다.
        if (childIds != null) {
            ingredient.clearChildren();

            if (!childIds.isEmpty()) {
                List<Ingredient> children = getIngredients(childIds);
                ingredient.addChildren(children);
            }
        }
    }

    /**
     * 상위 재료 설정을 해제합니다.
     * <ul>
     *     <li> 상위 재료와의 커플링 여부가 해제됩니다. </li>
     * </ul>
     * @param adminId 관리자의 ID
     * @param id 재료의 ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void removeParent(Long adminId, long id) {
        userRepository.validateExistsAsAdmin(adminId);

        // ID로 재료의 엔티티를 조회합니다.
        Ingredient ingredient = ingredientRepository.get(id);
        ingredient.removeParent();
        ingredient.updateIsParentCoupled(false);
    }

    /**
     * 재료를 삭제합니다.
     * <ul>
     *     <li> 하위 재료와의 커플링 여부가 해제됩니다. </li>
     * </ul>
     * @param adminId 관리자의 ID
     * @param id 재료의 ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long adminId, Long id) {
        userRepository.validateExistsAsAdmin(adminId);

        // 삭제할 재료의 ID가 전달되지 않은 경우, 더 이상 진행하지 않습니다.
        Ingredient ingredient = ingredientRepository.get(id);

        // 재료에 설정된 상위 재료를 해제합니다.
        ingredient.removeParent();

        // 하위 재료와의 의존성을 모두 제거합니다.
        ingredient.clearChildren();

        // 엔티티를 삭제합니다.
        ingredientRepository.deleteById(id);
    }

    /**
     * 재료 ID 목록을 재료 엔티티 목록으로 변환합니다.<br/>
     * @param ids 재료의 ID 목록
     * @return 재료의 엔티티 목록. 재료 ID 목록이 유효하지 않으면 null을 반환합니다.
     * @throws IllegalArgumentException 재료 ID 목록 내 유효하지 않은 ID가 1개 이상 존재해서,
     *         재료 ID 목록의 전체 항목을 재료 엔티티 목록으로 변환하지 못한 경우.
     */
    private List<Ingredient> getIngredients(List<Long> ids) {
        if (ids == null || ids.isEmpty()) return null;

        List<Ingredient> children = ingredientRepository.findAllById(ids);
        if (children.size() != ids.size()) {
            throw new IllegalArgumentException("추가하려는 하위 재료 중, " +
                    Math.abs(children.size() - ids.size()) + "개의 재료가 유효하지 않아요.");
        }
        return children;
    }

}
