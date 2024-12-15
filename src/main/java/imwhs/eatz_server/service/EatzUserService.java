package imwhs.eatz_server.service;

import imwhs.eatz_server.domain.EatzUser;
import imwhs.eatz_server.dto.eatzuser.EatzUserCreateDto;
import imwhs.eatz_server.dto.eatzuser.EatzUserUpdateDto;
import imwhs.eatz_server.exception.DuplicatedEatzUserException;
import imwhs.eatz_server.exception.EatzUserNotFoundException;
import imwhs.eatz_server.repository.eatzuser.EatzUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class EatzUserService {

    /**
     * 페이지 번호 및 크기 기본 값.
     * <p>
     *     컬렉션 조회 결과에 대한 페이징 처리 시 기본으로 설정, 사용되는 값입니다.
     * </p>
     */
    private static final int DEFAULT_CURRENT_PAGE = 0;
    private static final int DEFAULT_PAGING_SIZE = 10;

    private final EatzUserRepository userRepository;

    /**
     * 새 사용자를 등록합니다.
     * <ul>
     *     <li>EatzUser 엔티티를 생성하고 리포지토리를 통해 저장합니다.</li>
     *     <li>EatzUser 엔티티 생성에 필요한 데이터가 많기 때문에, 사용자 생성 DTO로 사용자 생성에 필요한 데이터를 전달 받습니다.</li>
     * </ul>
     * @param dto 사용자 생성 DTO.
     * @return 등록 완료된 사용자 엔티티 EatzUser의 식별자.
     */
    @Transactional
    public Long registerUser(EatzUserCreateDto dto) {
        // 기존 등록된 사용자에 의해 사용 중인 사용자 이름 또는 이메일이 아닌지 확인합니다.
        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new DuplicatedEatzUserException("이미 " + dto.getUsername() + "를 사용자 이름으로 사용 중인 사용자가 존재합니다.");
        }
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new DuplicatedEatzUserException("이미 " + dto.getEmail() + "를 이메일 주소로 사용 중인 사용자가 존재합니다.");
        }

        EatzUser user = dto.toMemberEntity();
        userRepository.save(user);
        return user.getId();
    }

    /**
     * 사용자를 업데이트합니다.
     * <ul>
     *     <li>기존 EatzUser 엔티티의 필드 별 데이터를 업데이트합니다.</li>
     *     <li>EatzUser 엔티티에 업데이트 가능한 필드가 많기 때문에, 사용자 업데이트 DTO로 사용자 업데이트에 필요한 데이터를 전달 받습니다.</li>
     * </ul>
     *
     * @param id 업데이트할 사용자의 엔티티 ID 값.
     * @param dto 사용자 업데이트 DTO.
     * @return 업데이트 완료된 사용자 엔티티 EatzUser의 식별자.
     */
    @Transactional
    public void updateUser(Long id, EatzUserUpdateDto dto) {
        EatzUser user = userRepository.findById(id).orElseThrow(() ->
                new EatzUserNotFoundException("id가 " + id + "인 사용자를 찾을 수 없습니다."));
        user.update(dto.getUsername(), dto.getEmail(), dto.getPassword());
    }

    /**
     * 사용자를 삭제합니다.
     * <p>
     * 식별자에 해당하는 사용자 엔티티를 삭제합니다.
     * </p>
     * @param id 삭제할 사용자의 엔티티 ID 값.
     */
    // TODO: 삭제 처리 여부 결정
    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) throw new EatzUserNotFoundException("id가 " + id + "인 사용자를 찾을 수 없습니다.");
        else userRepository.deleteById(id);
    }

}
