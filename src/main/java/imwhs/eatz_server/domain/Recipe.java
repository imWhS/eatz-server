package imwhs.eatz_server.domain;

import imwhs.eatz_server.common.BaseEntity;
import imwhs.eatz_server.dto.UpdateRecipeDto;
import jakarta.persistence.*;
import lombok.Getter;

/**
 * Recipe 엔티티.<br/>
 * <p>
 * 레시피 정보를 저장, 관리하기 위한 클래스입니다.
 */
@Getter
@Entity
public class Recipe extends BaseEntity {

    @Id @GeneratedValue
    private Long id;

    /**
     * 레시피를 등록한 사용자.<br/>
     *
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "eatz_user_id")
    private EatzUser user;

    /**
     * 레시피 제목.<br/>
     * <p>
     * 최대 100자 길이의 문장까지 저장할 수 있습니다.
     */
    @Column(length = 100, nullable = false)
    private String title;

    /**
     * 레시피 URL.<br/>
     * <p>
     * 최대 1000자 길이의 문장까지 저장할 수 있습니다.
     */
    @Column(length = 1000)
    private String url;

    /**
     * 레시피 이미지 URL.
     */
    private String imageUrl;

    /**
     * 레시피 설명.<br/>
     */
    private String description;

    /**
     * Recipe, EatzUser의 양방향 연관 관계 설정 메서드.
     * @param user 레시피를 등록한 사용자
     */
    public void setUser(EatzUser user) {
        this.user = user;
        user.getRecipeList().add(this);
    }

    /**
     * Recipe 생성 메서드.
     * @param user 레시피를 등록하려는 사용자
     * @param title 레시피 제목
     * @param url 레시피 URL
     * @param imageUrl 레시피 대표 이미지 URL
     * @return Recipe
     */
    public static Recipe create(
            EatzUser user,
            String title,
            String url,
            String imageUrl,
            String description
    ) {
        Recipe recipe = new Recipe();
        recipe.user = user;
        recipe.title = title;
        recipe.url = url;
        recipe.imageUrl = imageUrl;
        recipe.description = description;

        return recipe;
    }

    /**
     * Recipe 수정 메서드.
     * @param dto 수정할 레시피 정보를 담고 있는 UpdateRecipeDto
     */
    public void update(UpdateRecipeDto dto) {
        this.title = dto.getTitle();
        this.url = dto.getUrl();
        this.imageUrl = dto.getImageUrl();
        this.description = dto.getDescription();
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
