package imwhs.eatz_server.domain.pantry;

import imwhs.eatz_server.common.BaseEntity;
import imwhs.eatz_server.domain.Kitchenware;
import imwhs.eatz_server.domain.eatzuser.EatzUser;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * 사용자가 보관함에 추가한 도구의 정보를 정의하는 PantryKitchenware 엔티티입니다.
 * <ul>
 *     <li> 사용자와 도구의 N:M 연관 관계를 N:1로 풀어서 매핑하는 엔티티입니다. </li>
 * </ul>
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@Table(name = "pantry_kitchenware", uniqueConstraints = {
        @UniqueConstraint(name = "uk_kitchenware_user", columnNames = {"kitchenware_id", "user_id"})
})
@Entity
public class PantryKitchenware extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 도구
     * <ul>
     *     <li> 필수 항목입니다. </li>
     *     <li> 사용자가 보관함에 추가한 도구 정보입니다. </li>
     *     <li> 도구에서의 역방향 조회가 필요하지 않으므로, PantryKitchenware -> Kitchenware 단방향 연관 관계로 설정합니다. </li>
     *     <li> 연관 관계인 Kitchenware 레코드가 삭제되면,
     *          데이터베이스를 통해 해당 Kitchenware의 ID가 외래 키인 PantryKitchenware 레코드도 일괄 삭제합니다. </li>
     * </ul>
     */
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "kitchenware_id", nullable = false)
    @EqualsAndHashCode.Include
    private Kitchenware kitchenware;

    /**
     * 사용자
     * <ul>
     *     <li> 필수 항목입니다. </li>
     *     <li> 도구를 보관함에 추가한 사용자 정보입니다. </li>
     *     <li> EatzUser 엔티티가 비대해지는 것을 막기 위해, PantryKitchenware -> EatzUser 단방향 연관 관계로 설정합니다. </li>e
     *     <li> 연관 관계인 EatzUser 레코드가 삭제되면, 데이터베이스를 통해 해당 EatzUser의 ID가 외래 키인
     *          PantryKitchenware 레코드도 일괄 삭제합니다. </li>
     * </ul>
     */
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @EqualsAndHashCode.Include
    private EatzUser user;

    /**
     * PantryKitchenware 엔티티 팩토리 메서드
     * <ul>
     *     <li> Kitchenware와 EatzUser로 PantryKitchenware를 생성합니다. </li>
     *     <li> PantryKitchenware는 EatzUser와의 연관 관계 주인이 되지만, EatzUser 엔티티가 비대해지는 것을 막기 위해
     *          EatzUser -> PantryKitchenware 양방향 연관 관계를 설정하지 않습니다. </li>
     * </ul>
     * @param kitchenware 도구의 Kitchenware 엔티티
     * @param user 사용자의 EatzUser 엔티티
     * @return PantryKitchenware 엔티티
     */
    public static PantryKitchenware create(Kitchenware kitchenware, EatzUser user) {
        PantryKitchenware pantryKitchenware = new PantryKitchenware(null, kitchenware, user);
        return pantryKitchenware;
    }

}
