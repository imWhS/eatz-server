package imwhs.eatz_server.service;

import imwhs.eatz_server.domain.EatzUser;
import imwhs.eatz_server.dto.eatzuser.CreateEatzUserDto;
import imwhs.eatz_server.dto.eatzuser.UpdateEatzUserDto;
import imwhs.eatz_server.exception.EatzUserNotFoundException;
import imwhs.eatz_server.repository.eatzuser.EatzUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class EatzUserService {

    private final EatzUserRepository userRepository;

    /**
     * 새 사용자 등록.
     * <p>
     * EatzUser 엔티티를 생성하고 리포지토리를 통해 저장합니다.
     * username, email, password, role 등 EatzUser 엔티티 생성에 필요한 데이터가 많기 때문에
     * DTO인 CreateEatzUserDto로 데이터를 전달받습니다.
     *
     * @param dto 새 사용자를 등록하기 위한 데이터를 전달받기 위한 DTO
     * @return 등록 완료된 사용자 엔티티 EatzUser의 id
     */
    @Transactional
    public Long registerUser(CreateEatzUserDto dto) {
        EatzUser user = dto.toEntity();

        // 기존 등록된 사용자에 의해 사용 중인 사용자 이름 또는 이메일이 아닌지 확인합니다.)
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new IllegalStateException("이미 " + user.getUsername() + " 사용자 이름을 사용 중인 사용자가 존재합니다.");
        }

        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalStateException("이미 " + user.getEmail() + " 이메일 주소를 사용 중인 사용자가 존재합니다.");
        }

        userRepository.save(user);
        return user.getId();
    }

    /**
     * 사용자 수정.
     * <p>
     * 특정 id의 EatzUser 엔티티를 수정합니다.
     * username, email, password 등 EatzUser 엔티티의 수정 가능한 필드가 많기 때문에
     * DTO인 UpdateEatzUserDto로 수정할 데이터를 전달받습니다.
     *
     * @param id
     * @param dto
     * @return 수정 완료된 사용자 엔티티 EatzUser의 id
     */
    @Transactional
    public Long updateUser(Long id, UpdateEatzUserDto dto) {
        EatzUser user = userRepository.findById(id).orElseThrow(() ->
                new EatzUserNotFoundException("id가 " + id + "인 사용자를 찾을 수 없습니다."));
        user.update(dto.getUsername(), dto.getEmail(), dto.getPassword());
        return user.getId();
    }

    /**
     * 사용자 삭제.
     * <p>
     * 특정 id의 사용자를 삭제합니다.
     * TODO: 삭제 처리 여부 결정
     * </p>
     */
    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) throw new EatzUserNotFoundException("id가 " + id + "인 사용자를 찾을 수 없습니다.");
        else userRepository.deleteById(id);
    }

}
