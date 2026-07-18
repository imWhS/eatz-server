package imwhs.eatz_server;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ImageStorageCategory {

    USER_PROFILE("/images/users/profiles", "user_profile"),
    RECIPE("/images/recipes", "recipe"),
    KITCHENWARE("/images/kitchenwares", "kitchenware");

    private final String path;
    private final String fileNamePrefix;

}
