package imwhs.eatz_server;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ImageCategory {

    USER_PROFILE("images/users/profiles/"),
    RECIPE("images/recipes/"),
    KITCHENWARE("images/kitchenwares/");

    private final String path;

}
