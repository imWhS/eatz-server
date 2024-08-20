package imwhs.eatz_server.domain;

import jakarta.persistence.*;
import lombok.Getter;

/**
 * Recipe 엔티티입니다.<br/>
 * 레시피 정보를 담고 있습니다.
 */
@Getter
@Entity
public class Recipe {

    @Id @GeneratedValue
    private Long id;

    @Column(length = 100, nullable = false)
    private String title;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "eatz_user_id")
    private EatzUser user;

    private String description;

    @Column(length = 1000)
    private String url;

    private String imageUrl;

    public void setUser(EatzUser user) {
        this.user = user;
        user.getRecipeList().add(this);
    }

}
