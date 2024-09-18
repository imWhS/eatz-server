package imwhs.eatz_server.domain.reaction;

import imwhs.eatz_server.common.BaseEntity;
import imwhs.eatz_server.domain.EatzUser;
import imwhs.eatz_server.domain.Recipe;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode(of = "id")
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "reaction_type")
public abstract class Reaction extends BaseEntity {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipe_id", nullable = false)
    private Recipe recipe;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private EatzUser user;

    private boolean isHidden = false;

    protected Reaction() {}

    protected Reaction(Recipe recipe, EatzUser user) {
        this.recipe = recipe;
        this.user = user;
    }

}
