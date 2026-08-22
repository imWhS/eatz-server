package imwhs.eatz_server.service.eatzuser;

import imwhs.eatz_server.ImageCategory;
import imwhs.eatz_server.domain.eatzuser.EatzUser;
import imwhs.eatz_server.exception.InvalidPasswordException;
import imwhs.eatz_server.repository.EatzUserRepository;
import imwhs.eatz_server.service.ImageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;

@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class EatzUserService {

    private final EatzUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ImageService imageService;

    /**
     * 사용자의 암호를 업데이트합니다.
     * @param id 사용자의 ID
     * @param requesterId 업데이트를 요청한 사용자의 ID
     * @param existingPassword
     * @param newPassword
     */
    @Transactional(rollbackFor = Exception.class)
    public void updatePassword(
            Long id,
            Long requesterId,
            String existingPassword,
            String newPassword) {
        EatzUser.validateRawPassword(existingPassword);
        EatzUser.validateRawPassword(newPassword);

        EatzUser user = userRepository.get(id);
        EatzUser requester = getRequester(id, requesterId, user);

        // 암호 업데이트 여부와 관계 없이, 사용자가 업데이트를 요청하기 위해 입력한 기존 암호가 사용자의 현재 암호화된 암호와 일치하는지 확인합니다.
        validatePasswordMatch(existingPassword, user.getPassword());

        // 새 암호를 인코딩한 후 업데이트합니다.
        String newPasswordEncoded = passwordEncoder.encode(newPassword);
        user.updatePassword(requester, newPasswordEncoded);
    }

    /**
     * 사용자의 소개를 업데이트합니다.
     * @param id 사용자의 ID
     * @param requesterId 업데이트를 요청한 사용자의 ID
     * @param bio 소개
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateBio(Long id, Long requesterId, String bio) {
        EatzUser user = userRepository.get(id);
        EatzUser requester = getRequester(id, requesterId, user);
        user.updateBio(requester, bio);
    }

    /**
     * 사용자의 소개를 제거합니다.
     * @param id 사용자의 ID
     * @param requesterId 업데이트를 요청한 사용자의 ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void removeBio(Long id, Long requesterId) {
        EatzUser user = userRepository.get(id);
        EatzUser requester = getRequester(id, requesterId, user);
        user.deleteBio(requester);
    }

    /**
     * 사용자의 프로필 이미지를 업로드한 후, 이미지 URL을 업데이트합니다.
     * @param id 사용자의 ID
     * @param requesterId 업데이트를 요청한 사용자의 ID
     * @param image 프로필 이미지
     * @return 업로드 완료된 사용자의 프로필 이미지 URL
     */
    @Transactional(rollbackFor = Exception.class)
    public String updateImage(Long id, Long requesterId, MultipartFile image) {
        EatzUser user = userRepository.get(id);
        EatzUser requester = getRequester(id, requesterId, user);

        // 먼저 기존 프로필 이미지 URL을 미리 가져옵니다.
        String existingImageUrl = user.getImageUrl();

        // 새 프로필 이미지를 업로드합니다.
        String imageUrl = imageService.upload(image, ImageCategory.USER_PROFILE); // uploads/images/users/profiles/profile_123.png
        user.updateImageUrl(requester, imageUrl);
        log.info("사용자 {}의 프로필 이미지 URL을 업데이트했어요: {}", user.getUsername(), imageUrl);

        // 새 프로필 이미지를 업로드한 후, 사용자의 새 프로필 이미지 URL로 설정 완료한 경우 기존 프로필 이미지를 서버에서 삭제합니다.
        if (Objects.nonNull(existingImageUrl) && !existingImageUrl.isBlank()) {
            try {
                imageService.delete(existingImageUrl);
            } catch (Exception e) {
                log.error("사용자 {}의 기존 프로필 이미지를 삭제하지 못했어요.", user.getUsername());
            }
        }

        return imageUrl;
    }

    /**
     * 사용자의 프로필 이미지 URI(URL)를 제거하고, 이미지를 삭제합니다.
     * @param id 사용자의 ID
     * @param requesterId 삭제를 요청한 사용자의 ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void removeImage(Long id, Long requesterId) {
        EatzUser user = userRepository.get(id);
        EatzUser requester = getRequester(id, requesterId, user);
        String existingImageUrl = user.getImageUrl();
        user.deleteImageUrl(requester);
        if (Objects.nonNull(existingImageUrl) && !existingImageUrl.isBlank()) {
            try {
                imageService.delete(existingImageUrl);
                log.info("사용자 {}의 프로필 이미지 URL을 제거했어요.", user.getUsername());
            } catch (Exception e) {
                log.error("사용자 {}의 프로필 이미지를 삭제하지 못했어요.", user.getUsername());
            }
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateUsername(Long id, Long requesterId, String username) {
        EatzUser user = userRepository.getAdmin(id);
        EatzUser requester = getRequester(id, requesterId, user);
        user.updateUsername(requester, username);
    }

    /**
     * 사용자를 삭제 처리합니다.
     * @param id 사용자의 ID
     * @param requesterId 삭제를 요청한 사용자의 ID
     * @param existingPassword 사용자의 암호
     */
    @Transactional(rollbackFor = Exception.class)
    public void markAsDeleted(Long id, Long requesterId, String existingPassword) {
        EatzUser user = userRepository.get(id);
        EatzUser requester = getRequesterForDelete(id, requesterId, user);

        // 사용자가 삭제 처리를 요청하기 위해 입력한 기존 암호가 사용자의 현재 암호화된 암호와 일치하는지 확인합니다.
        validatePasswordMatch(existingPassword, user.getPassword());

        // 사용자를 삭제 처리합니다.
        user.validateDeletableByUser(requester);
        user.markAsDeleted();
    }

    /**
     * 사용자를 삭제합니다.
     * @param id 사용자의 ID
     * @param requesterId 삭제를 요청한 사용자의 ID
     * @param existingPassword 사용자의 암호
     */
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id, Long requesterId, String existingPassword) {
        EatzUser user = userRepository.get(id);
        EatzUser requester = getRequesterForDelete(id, requesterId, user);

        // 사용자가 삭제를 요청하기 위해 입력한 기존 암호가 사용자의 현재 암호화된 암호와 일치하는지 확인합니다.
        validatePasswordMatch(existingPassword, user.getPassword());

        // 사용자를 삭제합니다.
        user.validateDeletableByUser(requester);
        userRepository.deleteById(id);
    }

    /**
     * 평문 암호가 암호화된 암호의 일치 여부를 검증합니다.
     * @param raw 평문 암호
     * @param encoded 암호화된 암호
     * @throws InvalidPasswordException 평문 암호가 암호화된 암호와 일치하지 않을 경우
     */
    private void validatePasswordMatch(String raw, String encoded) {
        if (!passwordEncoder.matches(raw, encoded)) { throw new InvalidPasswordException("암호가 유효하지 않아요."); }
    }

    private EatzUser getRequester(Long id, Long requesterId, EatzUser user) {
        return Objects.equals(id, requesterId) ? user : userRepository.get(requesterId);
    }

    private EatzUser getRequesterForDelete(Long id, Long requesterId, EatzUser user) {
        return Objects.equals(id, requesterId) ? user : userRepository.getAdmin(requesterId);
    }
}
