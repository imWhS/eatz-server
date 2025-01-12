package imwhs.eatz_server.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Entity
public class RefreshToken {

    @Id @GeneratedValue
    private Long id;

    private String email;

    private LocalDateTime expiration;

    private String token;

    public RefreshToken(String email, LocalDateTime expiration, String token) {
        this.email = email;
        this.expiration = expiration;
        this.token = token;
    }

}
