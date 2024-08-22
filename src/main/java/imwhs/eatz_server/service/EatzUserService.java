package imwhs.eatz_server.service;

import imwhs.eatz_server.domain.EatzUser;
import imwhs.eatz_server.dto.UpdateEatzUserDTO;
import imwhs.eatz_server.exception.EatzUserNotFoundException;
import imwhs.eatz_server.repository.EatzUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EatzUserService {

    private final EatzUserRepository userRepository;

    /**
     * 새 사용자 등록.<br/>
     * TODO: 비밀번호 암호화 처리
     */
    public EatzUser registerUser(EatzUser user) {
        return userRepository.save(user);
    }

    /**
     * 식별자로 사용자 조회.
     * <p>
     * id로 특정 EatzUser를 조회합니다.
     */
    public EatzUser findUserById(Long id) {
        return userRepository.findById(id).orElseThrow(
                () -> new EatzUserNotFoundException("id가 " + id + "인 사용자를 찾을 수 없습니다.")
        );
    }

    /**
     * 사용자 이름으로 사용자 조회.
     * <p>
     * username으로 특정 EatzUser를 조회합니다.
     */
    public EatzUser findUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new EatzUserNotFoundException("사용자 이름이 " + username + "인 사용자를 찾을 수 없습니다."));
    }

    /**
     * 모든 사용자 목록 조회.
     */
    public List<EatzUser> findAllUsers() {
        return userRepository.findAll();
    }

    /**
     * 사용자 정보 수정.
     */
    @Transactional(readOnly = false)
    public void updateUser(Long id, UpdateEatzUserDTO dto) {
        EatzUser user = userRepository.findById(id).orElseThrow(() -> new EatzUserNotFoundException("id가 " + id + "인 사용자를 찾을 수 없습니다."));
        user.update(dto.getUsername(), dto.getEmail(), dto.getPassword());
    }

    /**
     * 사용자 삭제.
     */
    @Transactional(readOnly = false)
    public void deleteUser(Long id) {
        EatzUser user = userRepository.findById(id).orElseThrow(() -> new EatzUserNotFoundException("id가 " + id + "인 사용자를 찾을 수 없습니다."));
        userRepository.delete(user);
    }

}
