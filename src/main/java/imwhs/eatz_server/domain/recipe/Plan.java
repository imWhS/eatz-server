package imwhs.eatz_server.domain.recipe;

import imwhs.eatz_server.domain.eatzuser.EatzUser;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

/**
 * Plan 클래스입니다.<br/>
 */
@Getter
@AllArgsConstructor
@Entity
public class Plan {

    @Id @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipe_id")
    private Recipe recipe;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private EatzUser user;

    private LocalDate scheduledAt;

    private Integer priority;

    protected Plan() {}

    public static Plan create(Recipe recipe, EatzUser user, LocalDate scheduledAt, Integer priority) {
        Plan plan = new Plan(null, recipe, user, scheduledAt, priority);
        return plan;
    }

    public void update(LocalDate scheduledAt, Integer priority) {
        this.scheduledAt = scheduledAt;
        this.priority = priority;
    }

}
