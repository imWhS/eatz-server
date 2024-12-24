package imwhs.eatz_server.service;

import imwhs.eatz_server.domain.EatzUser;
import imwhs.eatz_server.dto.eatzuser.EatzUserDeleteDto;
import imwhs.eatz_server.dto.eatzuser.EatzUserUpdateDto;
import imwhs.eatz_server.exception.EatzUserNotFoundException;
import imwhs.eatz_server.repository.eatzuser.EatzUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class EatzUserService {

    private final EatzUserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    /**
     * 사용자를 업데이트합니다.
     * <ul>
     *     <li>기존 EatzUser 엔티티의 필드 별 데이터를 업데이트합니다.</li>
     *     <li>EatzUser 엔티티에 업데이트 가능한 필드가 많기 때문에, 사용자 업데이트 DTO로 사용자 업데이트에 필요한 데이터를 전달 받습니다.</li>
     * </ul>
     *
     * @param id 업데이트할 사용자의 엔티티 ID.
     * @param dto 사용자 업데이트 DTO.
     */
    @Transactional
    public void updateUser(Long id, EatzUserUpdateDto dto) {
        EatzUser user = userRepository.findById(id).orElseThrow(() ->
                new EatzUserNotFoundException("ID가 " + id + "인 사용자를 찾을 수 없습니다."));
        validateExistingPassword(dto.getExistingPassword(), user);
        String newPassword = passwordEncoder.encode(dto.getNewPassword());
        user.update(dto.getUsername(), dto.getEmail(), newPassword);
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
    public void deleteUser(Long id, EatzUserDeleteDto dto) {
        EatzUser user = userRepository.findById(id).orElseThrow(() ->
                new EatzUserNotFoundException("ID가 " + id + "인 사용자를 찾을 수 없습니다."));
        validateExistingPassword(dto.getExistingPassword(), user);
        userRepository.deleteById(id);
    }

    private void validateExistingPassword(String existingPassword, EatzUser user) {
        if (!passwordEncoder.matches(existingPassword, user.getPassword())) {
            throw new IllegalArgumentException("기존 사용 중이던 비밀 번호가 올바르지 않습니다.");
        }
    }

}
