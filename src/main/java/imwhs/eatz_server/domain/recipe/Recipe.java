package imwhs.eatz_server.domain.recipe;

import imwhs.eatz_server.domain.user.EatzUser;
import jakarta.persistence.*;
import lombok.Getter;

/**
 * 레시피 엔티티를 나타내는 클래스입니다.
 *
 * <p>
 * 이 클래스는 레시피에 대한 제목, 설명, 등록한 사용자, 이미지 URL 등을 저장합니다.
 * </p>
 */
@Getter
@Entity
public class Recipe {

    /**
     * 레시피의 고유 식별자입니다.
     * <p>
     *     레시피 엔티티의 기본 키(PK) 역할을 합니다.<br>
     *     레시피가 생성될 때 자동으로 값이 설정됩니다.
     * </p>
     */
    @Id @GeneratedValue
    @Column(name = "recipe_id")
    private Long id;

    /**
     * 레시피를 등록한 사용자입니다.
     *
     * <p>
     *     레시피와 사용자는 N:1 관계로, 나은 성능을 위해 지연 로딩을 설정합니다.
     * </p>
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "eatz_user_id")
    private EatzUser eatzUser;

    /**
     * 레시피의 제목입니다.
     */
    private String title;

    /**
     * 레시피의 설명입니다.
     */
    private String description;

    /**
     * 레시피의 이미지 URL입니다.
     */
    private String imageUrl;

}