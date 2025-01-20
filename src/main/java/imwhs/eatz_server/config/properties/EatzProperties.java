package imwhs.eatz_server.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "eatz")
public class EatzProperties {

    private String baseDirectory;

    private ImageProperties imageProperties;

    @Getter
    @Setter
    public static class StoragePropeties {

    }



    @Getter
    @Setter
    public static class ImageProperties {
        private String baseDirectory;
    }

}
