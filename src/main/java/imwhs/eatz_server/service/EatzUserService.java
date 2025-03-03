package imwhs.eatz_server.service;

import imwhs.eatz_server.auth.EatzUserAuthUtil;
import imwhs.eatz_server.domain.eatzuser.EatzUser;
import imwhs.eatz_server.dto.eatzuser.DeleteEatzUserDto;
import imwhs.eatz_server.dto.eatzuser.UpdateEatzUserDto;
import imwhs.eatz_server.exception.EatzUserNotFoundException;
import imwhs.eatz_server.exception.InvalidPasswordException;
import imwhs.eatz_server.exception.UnauthorizedEatzUserException;
import imwhs.eatz_server.repository.eatzuser.EatzUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class EatzUserService {

    private final EatzUserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final ImageService imageService;

    /**
     * 사용자를 업데이트합니다.
     * <ul>
     *     <li>기존 EatzUser 엔티티의 필드 별 데이터를 업데이트합니다.</li>
     *     <li>EatzUser 엔티티에 업데이트 가능한 필드가 많기 때문에, 사용자 업데이트 DTO로 사용자 업데이트에 필요한 데이터를 전달 받습니다.</li>
     * </ul>
     *
     * @param username 업데이트를 요청한 사용자의 username.
     * @param id 업데이트할 사용자의 엔티티 ID.
     * @param dto 사용자 업데이트 DTO.
     */
    @Transactional
    public void updateUser(String username, Long id, UpdateEatzUserDto dto) {
        EatzUser user = userRepository.findById(id).orElseThrow(() ->
                new EatzUserNotFoundException(id));

        if (!user.getUsername().equals(username)) {
            throw new UnauthorizedEatzUserException("사용자 본인 계정의 정보만 수정할 수 있어요.");
        }

        validateExistingPassword(dto.getExistingPassword(), user);

        String password = dto.getNewPassword() == null ? null : passwordEncoder.encode(dto.getNewPassword());
        user.update(dto.getUsername(), password);
    }

    /**
     * 사용자의 프로필 이미지를 업데이트합니다.
     */
    @Transactional
    public void updateImage(String username, MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("업로드하려는 이미지 파일이 유효하지 않습니다.");
        }

        EatzUser user = userRepository.findByUsername(username).orElseThrow(() ->
                new EatzUserNotFoundException("사용자 이름이 " + username + "인 사용자를 찾을 수 없습니다."));
        String imageUrl = imageService.uploadProfileImage(user.getUsername(), file);
        user.updateImageUrl(imageUrl);
    }

    @Transactional
    public void removeImage() {
        String username = EatzUserAuthUtil.getUsername();
        EatzUser user = userRepository.findByUsername(username).orElseThrow(() ->
                new EatzUserNotFoundException("사용자 이름이 " + username + "인 사용자를 찾을 수 없습니다."));
        imageService.deleteImage(user.getImageUrl());
    }

    /**
     * 사용자를 삭제합니다.
     * <p>
     * ID에 해당하는 사용자 엔티티를 삭제합니다.
     * </p>
     * @param id 삭제할 사용자의 엔티티 ID 값.
     */
    // TODO: 삭제 처리 여부 결정
    @Transactional
    public void deleteUser(Long id, DeleteEatzUserDto dto) {
        EatzUser user = userRepository.findById(id).orElseThrow(() ->
                new EatzUserNotFoundException("ID가 " + id + "인 사용자를 찾을 수 없습니다."));
        validateExistingPassword(dto.getExistingPassword(), user);
        userRepository.deleteById(id);
    }

    private void validateExistingPassword(String existingPassword, EatzUser user) {
        if (!passwordEncoder.matches(existingPassword, user.getPassword())) {
            throw new InvalidPasswordException("기존 사용 중이던 비밀 번호가 올바르지 않습니다.");
        }
    }

}
