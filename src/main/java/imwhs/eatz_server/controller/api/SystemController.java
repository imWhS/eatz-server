package imwhs.eatz_server.controller.api;

import imwhs.eatz_server.dto.ClientVersionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/v0/system")
@RestController
public class SystemController {

    @GetMapping("/version/ios")
    @ResponseStatus(HttpStatus.OK)
    public ClientVersionResponse getIOSClientVersion() {
        return new ClientVersionResponse("1.0.0", "1.0.0", null);
    }

}
