package imwhs.eatz_server.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "eatz.storage.image")
public class ImageStorageProperties {

    private String baseDirectory;

}
