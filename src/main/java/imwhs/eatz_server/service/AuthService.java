package imwhs.eatz_server.service;

import imwhs.eatz_server.domain.EatzUser;
import imwhs.eatz_server.dto.eatzuser.EatzUserCreateDto;
import imwhs.eatz_server.exception.DuplicatedEatzUserException;
import imwhs.eatz_server.repository.eatzuser.EatzUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class AuthService {

    private final EatzUserRepository userRepository;

    private final PasswordEncoder passwordEncoder;


    /**
     * 회원 권한을 가진 새 사용자를 등록합니다.
     * <ul>
     *     <li>EatzUser 엔티티를 생성하고 리포지토리를 통해 저장합니다.</li>
     *     <li>EatzUser 엔티티 생성에 필요한 데이터가 많기 때문에, 사용자 생성 DTO로 사용자 생성에 필요한 데이터를 전달 받습니다.</li>
     * </ul>
     * @param dto 사용자 생성 DTO.
     * @return 등록 완료된 사용자 엔티티 EatzUser의 ID.
     */
    @Transactional
    public Long signUp(EatzUserCreateDto dto) {
        // 기존 등록된 사용자에 의해 사용 중인 사용자 이름 또는 이메일이 아닌지 확인합니다.
        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new DuplicatedEatzUserException("이미 " + dto.getUsername() + "를 사용자 이름으로 사용 중인 사용자가 존재합니다.");
        }
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new DuplicatedEatzUserException("이미 " + dto.getEmail() + "를 이메일 주소로 사용 중인 사용자가 존재합니다.");
        }

        String requestedPassword = dto.getPassword();
        String password = passwordEncoder.encode(requestedPassword);

        EatzUser member = EatzUser.createMember(dto.getUsername(), dto.getEmail(), password);
        userRepository.save(member);

        return member.getId();
    }

}
