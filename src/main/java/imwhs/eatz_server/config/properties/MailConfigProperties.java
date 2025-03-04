package imwhs.eatz_server.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "spring.mail")
public class MailConfigProperties {

    private String host;

    private int port;

    private String username;

    private String password;

    private Properties properties;

    private long authCodeExpirationTime;

    @Getter
    @Setter
    public static class Properties {

        private Smtp smtp;

    }

    @Getter
    @Setter
    public static class Smtp {

        private boolean auth;

        private Starttls starttls;

        private int connectiontimeout;

        private int timeout;

        private int writetimeout;

    }

    @Getter
    @Setter
    public static class Starttls {

        private boolean enable;

        private boolean required;

    }
}
