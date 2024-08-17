package imwhs.eatz_server.domain.user;

import imwhs.eatz_server.domain.recipe.Recipe;
import jakarta.persistence.*;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Table(name = "eatz_user")
@Entity
public class EatzUser {

    @Id @GeneratedValue
    @Column(name = "eatz_user_id")
    private Long id;

    @OneToMany(mappedBy = "eatzUser")
    private List<Recipe> recipes = new ArrayList<>();

    @Column(unique = true, nullable = false)
    private String username;

    @Enumerated(EnumType.STRING)
    private Role role;

    public EatzUser() {}

    public EatzUser(String username, Role role) {
        this.username = username;
        this.role = role;
    }
}
