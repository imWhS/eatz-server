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

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EatzUserService {

    private final EatzUserRepository userRepository;

    /**
     * 페이지 번호 및 크기 기본 값.
     * 응답 메시지에 포함시킬 시용자에 대해 페이징 처리를 하기 위해 정의합니다.
     */
    private static final int DEFAULT_PAGE_NUMBER = 0;
    private static final int DEFAULT_PAGE_SIZE = 10;

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
     * 식별자로 사용자 조회.
     * <p>
     * id로 특정 EatzUser를 조회합니다.
     */
    public EatzUserResponseDto findUserById(Long id) {
        EatzUser user = userRepository.findById(id).orElseThrow(
                () -> new EatzUserNotFoundException("id가 " + id + "인 사용자를 찾지 못했습니다.")
        );
        userRepository.save(user);

        return new EatzUserResponseDto(user);
    }

    /**
     * 사용자 이름으로 사용자 조회.
     * <p>
     * username으로 특정 EatzUser를 조회합니다.
     */
    public EatzUserResponseDto findUserByUsername(String username) {
        EatzUser user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EatzUserNotFoundException("사용자 이름이 " + username + "인 사용자를 찾을 수 없습니다."));
        userRepository.save(user);

        return new EatzUserResponseDto(user);
    }

    /**
     * 모든 사용자 목록 조회.
     */
    public Page<EatzUserResponseDto> findAllUsers(Integer pageNumber, Integer pageSize) {
        int number = (pageNumber == null || pageNumber < 0) ? DEFAULT_PAGE_NUMBER : pageNumber;
        int size = (pageSize == null || pageSize < 0) ? DEFAULT_PAGE_SIZE : pageSize;

        PageRequest pageRequest = PageRequest.of(number, size);
        Page<EatzUser> pagedUsers = userRepository.findAll(pageRequest);
        return pagedUsers.map(EatzUserResponseDto::new);
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
