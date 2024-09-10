package imwhs.eatz_server.service;

import imwhs.eatz_server.domain.EatzUser;
import imwhs.eatz_server.dto.CreateEatzUserDto;
import imwhs.eatz_server.dto.EatzUserResponseDto;
import imwhs.eatz_server.dto.UpdateEatzUserDto;
import imwhs.eatz_server.exception.EatzUserNotFoundException;
import imwhs.eatz_server.repository.EatzUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EatzUserService {

    private final EatzUserRepository userRepository;

    /**
     * 새 사용자 등록.<br/>
     * TODO: 비밀번호 암호화 처리
     */
    @Transactional
    public EatzUserResponseDto registerUser(CreateEatzUserDto dto) {
        validateDuplicates(dto.getUsername());

        EatzUser user = EatzUser.create(dto.getUsername(), dto.getEmail(), dto.getPassword(), dto.getRole());
        userRepository.save(user);

        return new EatzUserResponseDto(user);
    }

    /**
     * 사용자 수정.
     */
    @Transactional
    public void updateUser(Long id, UpdateEatzUserDto dto) {
        EatzUser user = userRepository.findById(id).orElseThrow(() -> new EatzUserNotFoundException("id가 " + id + "인 사용자를 찾을 수 없습니다."));
        user.update(dto.getUsername(), dto.getEmail(), dto.getPassword());
    }

    /**
     * 사용자 삭제.
     */
    @Transactional
    public void deleteUser(Long id) {
        EatzUser user = userRepository.findById(id).orElseThrow(() -> new EatzUserNotFoundException("id가 " + id + "인 사용자를 찾을 수 없습니다."));
        userRepository.delete(user);
    }

    private void validateDuplicates(String username) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new IllegalStateException("이미 같은 이름인 사용자(" + username + ")가 존재합니다.");
        }
    }

}
