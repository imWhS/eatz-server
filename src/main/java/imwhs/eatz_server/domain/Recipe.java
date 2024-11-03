package imwhs.eatz_server.domain;

import imwhs.eatz_server.common.BaseEntity;
import imwhs.eatz_server.dto.recipe.UpdateRecipeDto;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Recipe 엔티티 클래스입니다.
 * <p>
 *     레시피 정보를 저장, 관리하기 위한 클래스입니다.
 * </p>
 */
@Getter
@EqualsAndHashCode(of = "id")
@Entity
public class Recipe extends BaseEntity {

    @Id @GeneratedValue
    @Column(name = "recipe_id")
    private Long id;

    /**
     * 레시피를 등록한 사용자.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "eatz_user_id", nullable = false)
    private EatzUser user;

    /**
     * 제목.
     * <p>
     * 최대 100자 길이의 문장까지 저장할 수 있습니다.
     * </p>
     */
    @Column(length = 100, nullable = false)
    private String title;

    /**
     * URL.
     * <p>
     * 최대 1000자 길이의 문장까지 저장할 수 있습니다.
     * </p>
     */
    @Column(length = 1000)
    private String url;

    /**
     * 대표 이미지 URL.
     */
    private String imageUrl;

    /**
     * 설명.
     */
    private String description;

    /**
     * 모든 댓글.
     */
    @OneToMany(mappedBy = "recipe")
    private List<Comment> comments = new ArrayList<>();

    /**
     * 모든 평가.
     */
    @OneToMany(mappedBy = "recipe")
    private List<Rating> ratings = new ArrayList<>();

    /**
     * Recipe, EatzUser의 양방향 연관 관계 설정 메서드.
     * @param user 레시피를 등록한 사용자
     */
    public void setUser(EatzUser user) {
        this.user = user;
        user.getRecipes().add(this);
    }

    protected Recipe() {}

    /**
     * Recipe 팩토리 메서드.
     * <p>
     * Recipe 엔티티 객체를 생성합니다.
     * </p>
     * @param user 레시피를 등록하려는 사용자. 필수 항목입니다.
     * @param title 레시피 제목. 필수 항목입니다.
     * @param url 레시피 URL. 필수 항목입니다.
     * @param imageUrl 레시피 대표 이미지 URL.
     * @param description 레시피 설명.
     * @return Recipe 엔티티 객체.
     * @throws IllegalArgumentException Recipe 엔티티의 필수 항목인 제목 또는 URL이 null이거나 빈 값일 경우.
     */
    public static Recipe of(
            EatzUser user,
            String title,
            String url,
            String imageUrl,
            String description
    ) {
        if (Objects.isNull(user)) {
            throw new IllegalArgumentException("레시피를 등록하려는 사용자 정보가 없습니다.");
        }
        if (Objects.isNull(title) || title.isEmpty()) {
            throw new IllegalArgumentException("레시피 제목은 필수 항목입니다.");
        }
        if (Objects.isNull(url) || url.isEmpty()) {
            throw new IllegalArgumentException("레시피 URL은 필수 항목입니다.");
        }

        Recipe recipe = new Recipe();
        recipe.setUser(user);
        recipe.title = title;
        recipe.url = url;
        recipe.imageUrl = imageUrl;
        recipe.description = description;

        return recipe;
    }

    /**
     * Recipe 통합 수정 메서드.
     * @param dto 수정할 레시피 정보를 담고 있는 UpdateRecipeDto
     */
    public void update(UpdateRecipeDto dto) {
        // 레시피 제목은 필수 항목이기에, null이거나 빈 값으로 수정 요청한 경우 예외를 발생시켜 수정을 진행하지 않습니다.
        if (Objects.isNull(dto.getTitle()) || dto.getTitle().isEmpty()) {
            throw new IllegalArgumentException("레시피 제목은 필수 항목입니다.");
        } else {
            this.title = dto.getTitle();
        }

        // 레시피 URL은 필수 항목이기에, null이거나 빈 값으로 수정 요청한 경우 예외를 발생시켜 수정을 진행하지 않습니다.
        if (Objects.isNull(dto.getUrl()) || dto.getUrl().isEmpty()) {
            throw new IllegalArgumentException("레시피 URL은 필수 항목입니다.");
        } else {
            this.url = dto.getUrl();
        }

        // 레시피 대표 이미지 URL, 레시피 설명은 선택 항목이기에, 빈 값으로 수정을 요청한 경우에 이를 반영합니다.
        // 단, null로 수정 요청한 경우, 이전 값을 유지합니다.
        this.imageUrl = dto.getImageUrl() != null ? dto.getImageUrl() : this.imageUrl;
        this.description = dto.getDescription() != null ? dto.getDescription() : this.description;
    }

}
