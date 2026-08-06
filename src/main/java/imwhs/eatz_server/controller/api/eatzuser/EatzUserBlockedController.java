package imwhs.eatz_server.controller.api.eatzuser;

import imwhs.eatz_server.dto.eatzuser.EatzUserEssentialDto;
import imwhs.eatz_server.resolver.AuthenticatedEatzUserId;
import imwhs.eatz_server.service.BlockedService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v0/users/me/blockeds")
@RestController
public class EatzUserBlockedController {

    private final BlockedService blockedService;

    @PostMapping("/{targetId}")
    @ResponseStatus(HttpStatus.CREATED)
    public void blockUser(@AuthenticatedEatzUserId Long userId, @PathVariable Long targetId) {
        blockedService.block(userId, targetId);
    }

    @DeleteMapping("/{targetId}")
    public void deleteBlocker(@AuthenticatedEatzUserId Long userId, @PathVariable Long targetId) {
        blockedService.unblock(userId, targetId);
    }

    @GetMapping
    public Page<EatzUserEssentialDto> getBlocklist(@AuthenticatedEatzUserId Long userId, Pageable pageable) {
        return blockedService.getAllBlockedUsers(userId, pageable);
    }

}
