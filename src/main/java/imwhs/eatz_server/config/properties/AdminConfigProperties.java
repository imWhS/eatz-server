package imwhs.eatz_server.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@PropertySource("classpath:admin-config.yml")
@ConfigurationProperties(prefix = "admin")
public class AdminConfigProperties {

    private String email;

    private String password;

}