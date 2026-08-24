package imwhs.eatz_server.service;

import imwhs.eatz_server.domain.Blocked;
import imwhs.eatz_server.domain.eatzuser.EatzUser;
import imwhs.eatz_server.dto.eatzuser.EatzUserEssentialDto;
import imwhs.eatz_server.exception.EatzInvalidRequestArgumentException;
import imwhs.eatz_server.exception.EatzInvalidResourceStateException;
import imwhs.eatz_server.repository.blocked.BlockedRepository;
import imwhs.eatz_server.repository.EatzUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class BlockedService {

    private final BlockedRepository blockedRepository;
    private final EatzUserRepository userRepository;

    @Transactional(rollbackFor = Exception.class)
    public void block(Long blockerId, Long blockedUserId) {
        if (blockerId.equals(blockedUserId)) {
            throw new EatzInvalidResourceStateException("차단을 요청한 사용자와 차단하려는 사용자의 ID가 동일해요.");
        }

        if (blockedRepository.existsByBlockerIdAndBlockedUserId(blockerId, blockedUserId)) {
            throw new EatzInvalidResourceStateException("이미 차단한 사용자예요.");
        }

        EatzUser blocker = userRepository.getReference(blockerId);
        EatzUser blockedUser = userRepository.getReference(blockedUserId);

        Blocked blocked = Blocked.create(blocker, blockedUser);
        blockedRepository.save(blocked);
    }

    @Transactional(rollbackFor = Exception.class)
    public void unblock(Long blockerId, Long blockedUserId) {
        if (blockerId.equals(blockedUserId)) {
            throw new EatzInvalidResourceStateException("차단 취소를 요청한 사용자와 차단 취소하려는 사용자의 ID가 동일해요.");
        }

        int deletedBlocksCount = blockedRepository.deleteByBlockerIdAndBlockedUserId(blockerId, blockedUserId);
        if (deletedBlocksCount == 0) {
            throw new EatzInvalidResourceStateException("차단하지 않은 사용자예요.");
        }
    }

    public Page<EatzUserEssentialDto> getAllBlockedUsers(Long blockerId, Pageable pageable) {
        return blockedRepository.findAllBlockedUserEssentialsByBlockerIdAndDeletedAtIsNull(blockerId, pageable);
    }
}
