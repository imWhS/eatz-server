package imwhs.eatz_server.repository.ingredient;

import imwhs.eatz_server.domain.Ingredient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IngredientRepository extends JpaRepository<Ingredient, Long> {

    Boolean existsByNameAndCategoryId(String name, Long categoryId);

    /**
     * 식별자로 Ingredient 엔티티를 조회하고, 연관 관계인 Ingredient.category의 데이터도 페치 조인으로 함께 조회합니다.<br/>
     * <ul>
     *     <li>Ingredient와 Ingredient.category 데이터가 함께 로드됩니다.</li>
     *     <li>카테고리에 속하지 않은 재료도 조회하기 위해 Left Join을 사용합니다.</li>
     * </ul>
     * @param id 조회할 Ingredient 엔티티의 식별자.
     * @return Optional로 wrapping된 Ingredient 엔티티 객체.
     */
    @Query("select i from Ingredient i left join fetch i.category c where i.id = :id")
    Optional<Ingredient> findWithCategoryById(@Param("id") Long id);

    /**
     * 식별자에 해당하는 재료를 기준으로 계층 구조에 속하는 모든 재료를 조회합니다.<br/>
     * 식별자에 해당하는 재료의 기본 정보(식별자, 이름, 카테고리 식별자)를 시작으로, 하위 계층 구조에 속한 모든 재료까지 함께 재귀적으로 조회합니다.
     * @param id 계층 구조의 기준이 될 Ingredient 엔티티의 식별자.
     * @return 계층 구조의 기준이 된 Ingredient 엔티티 및 이의 하위 재료로서 계층 구조에 속해 있는 모든 Ingredient 엔티티 객체 목록.
     */
    @Query(value = """
        WITH RECURSIVE INGREDIENT_TREE(INGREDIENT_ID, NAME, CATEGORY_ID) AS (
            -- 식별자에 해당하는 타겟 재료를 조회합니다.
            SELECT I.INGREDIENT_ID, I.NAME, I.CATEGORY_ID FROM INGREDIENT I WHERE I.INGREDIENT_ID = :id
            UNION ALL
            -- 타겟 재료를 시작으로, 하위 계층을 구성하는 모든 재료를 재귀적으로 조회합니다.
            SELECT I.INGREDIENT_ID, I.NAME, I.CATEGORY_ID
            FROM INGREDIENT I
            JOIN INGREDIENT_TREE IT ON I.CATEGORY_ID = IT.INGREDIENT_ID
        )
        SELECT INGREDIENT_ID, NAME, CATEGORY_ID FROM INGREDIENT_TREE
        """, nativeQuery = true)
    List<Ingredient> findIngredientTree(@Param("id") Long id);

}
