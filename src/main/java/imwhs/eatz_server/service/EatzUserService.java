package imwhs.eatz_server.service;

import imwhs.eatz_server.domain.EatzUser;
import imwhs.eatz_server.dto.eatzuser.EatzUserCreateDto;
import imwhs.eatz_server.dto.eatzuser.EatzUserUpdateDto;
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
     * 새 사용자 등록
     * <ul>
     *     <li>EatzUser 엔티티를 생성하고 리포지토리를 통해 저장합니다.</li>
     *     <li>EatzUser 엔티티 생성에 필요한 데이터가 많기 때문에, 사용자 생성 DTO로 사용자 생성에 필요한 데이터를 전달 받습니다.</li>
     * </ul>
     * @param eatzUserCreateDto 사용자 생성 DTO
     * @return 등록 완료된 사용자 엔티티 EatzUser의 id
     */
    @Transactional
    public Long registerUser(EatzUserCreateDto eatzUserCreateDto) {
        // 기존 등록된 사용자에 의해 사용 중인 사용자 이름 또는 이메일이 아닌지 확인합니다.
        if (userRepository.existsByUsername(eatzUserCreateDto.getUsername())) {
            throw new IllegalStateException("이미 " + eatzUserCreateDto.getUsername() + " 사용자 이름을 사용 중인 사용자가 존재합니다.");
        }
        if (userRepository.existsByEmail(eatzUserCreateDto.getEmail())) {
            throw new IllegalStateException("이미 " + eatzUserCreateDto.getEmail() + " 이메일 주소를 사용 중인 사용자가 존재합니다.");
        }

        EatzUser user = eatzUserCreateDto.toEntity();
        userRepository.save(user);
        return user.getId();
    }

    /**
     * 사용자 수정
     * <ul>
     *     <li>기존 EatzUser 엔티티의 필드 별 데이터를 수정합니다.</li>
     *     <li>EatzUser 엔티티에 수정 가능한 필드가 많기 때문에, 사용자 수정 DTO로 사용자 수정에 필요한 데이터를 전달 받습니다.</li>
     * </ul>
     *
     * @param id 수정할 사용자의 엔티티 ID 값
     * @param eatzUserUpdateDto 사용자 수정 DTO
     * @return 수정 완료된 사용자 엔티티 EatzUser의 id
     */
    @Transactional
    public Long updateUser(Long id, EatzUserUpdateDto eatzUserUpdateDto) {
        EatzUser user = userRepository.findById(id).orElseThrow(() ->
                new EatzUserNotFoundException("id가 " + id + "인 사용자를 찾을 수 없습니다."));
        user.update(eatzUserUpdateDto.getUsername(), eatzUserUpdateDto.getEmail(), eatzUserUpdateDto.getPassword());
        return user.getId();
    }

    /**
     * 사용자 삭제
     * <p>
     * 특정 사용자를 삭제합니다.
     * </p>
     * @param id 삭제할 사용자의 엔티티 ID 값
     */
    // TODO: 삭제 처리 여부 결정
    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) throw new EatzUserNotFoundException("id가 " + id + "인 사용자를 찾을 수 없습니다.");
        else userRepository.deleteById(id);
    }

}
