package imwhs.eatz_server.service;

import imwhs.eatz_server.domain.eatzuser.EatzUser;
import imwhs.eatz_server.dto.eatzuser.EatzUserDetailDto;
import imwhs.eatz_server.exception.EatzUserNotFoundException;
import imwhs.eatz_server.repository.EatzUserRepository;
import imwhs.eatz_server.service.auth.AuthService;
import imwhs.eatz_server.service.eatzuser.EatzUserQueryService;
import imwhs.eatz_server.service.eatzuser.EatzUserService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@SpringBootTest
public class EatzUserServiceTest {

    @Autowired
    private EatzUserService userService;

    @Autowired
    private EatzUserRepository userRepository;

    @Autowired
    private EatzUserQueryService userQueryService;

    @Autowired
    private AuthService authService;
    @Autowired
    private PasswordEncoder passwordEncoder;

//    @Test
//    @DisplayName("사용자의 정보가 정상적으로 수정되는지 테스트합니다.")
//    @Transactional
//    void userUpdateTest() {
//        // given
//        String originalPassword = "1q2w3e4r!";
//        EatzUser user = EatzUser.createMember(
//                "heextory",
//                "imwhs@icloud.com",
//                passwordEncoder.encode(originalPassword));
//        userRepository.save(user);
//        Long userId = user.getId();
//
//        String newPassword = "21q2w3e4r!";
//        String newUsername = "2heextory";
//        String newEmail = "2imwhs@icloud.com";
//        EatzUserUpdateDto updateDto = new EatzUserUpdateDto(
//                newUsername,
//                newEmail,
//                originalPassword,
//                newPassword);
//        String encodedNewPassword = passwordEncoder.encode(newPassword);
//
//        // when
//        userService.updateUser(userId, updateDto);
//        userRepository.flush();
//
//        // then
//        EatzUser updatedUser = userRepository.findById(userId).orElseThrow(EatzUserNotFoundException::new);
//        Assertions.assertThat(updatedUser).isNotNull();
//        Assertions.assertThat(updatedUser.getUsername()).isEqualTo(newUsername);
//        Assertions.assertThat(updatedUser.getEmail()).isEqualTo(newEmail);
//        Assertions.assertThat(passwordEncoder.matches(newPassword, updatedUser.getPassword())).isTrue();
//
//        Assertions.assertThat(updatedUser.getCreatedAt()).isNotNull();
//        Assertions.assertThat(updatedUser.getUpdatedAt()).isNotEqualTo(updatedUser.getCreatedAt());
//        Assertions.assertThat(updatedUser.getUpdatedAt()).isNotNull();
//        Assertions.assertThat(updatedUser.getDeletedAt()).isNull();
//    }
//
//    @Test
//    @DisplayName("사용자의 정보 중 이메일 주소만 정상적으로 수정되는지 테스트합니다: 사용자 정보 일부 수정 테스트")
//    @Transactional
//    void emailUpdateTest() {
//        // given
//        String username = "heextory";
//        String password = "1q2w3e4r!";
//        String encodedPassword = passwordEncoder.encode(password);
//        EatzUser user = EatzUser.createMember(
//                username,
//                "imwhs@icloud.com",
//                passwordEncoder.encode(password));
//        userRepository.save(user);
//        Long userId = user.getId();
//
//        String newEmail = "2imwhs@icloud.com";
//        EatzUserUpdateDto updateDto = new EatzUserUpdateDto(username, newEmail, password, null);
//
//        // when
//        userService.updateUser(user.getId(), updateDto);
//        userRepository.flush();
//
//        // then
//        EatzUser updatedUser = userRepository.findById(userId).orElseThrow(EatzUserNotFoundException::new);
//        Assertions.assertThat(updatedUser).isNotNull();
//        Assertions.assertThat(updatedUser.getUsername()).isEqualTo(username);
//        Assertions.assertThat(updatedUser.getEmail()).isEqualTo(newEmail);
//        Assertions.assertThat(passwordEncoder.matches(password, updatedUser.getPassword())).isTrue();
//
//        Assertions.assertThat(updatedUser.getCreatedAt()).isNotNull();
//        Assertions.assertThat(updatedUser.getUpdatedAt()).isNotEqualTo(updatedUser.getCreatedAt());
//        Assertions.assertThat(updatedUser.getUpdatedAt()).isNotNull();
//        Assertions.assertThat(updatedUser.getDeletedAt()).isNull();
//    }
//
//    @Test
//    @DisplayName("사용자의 정보 중 암호만 정상적으로 수정되는지 테스트합니다: 사용자 정보 일부 수정 테스트")
//    @Transactional
//    void passwordUpdateTest() {
//        // given
//        String username = "heextory";
//        String originalPassword = "1q2w3e4r!";
//        String email = "imwhs@icloud.com";
//        EatzUser user = EatzUser.createMember(
//                username,
//                email,
//                passwordEncoder.encode(originalPassword));
//        userRepository.save(user);
//        Long userId = user.getId();
//
//        String newPassword = "21q2w3e4r!";
//        EatzUserUpdateDto updateDto = new EatzUserUpdateDto(username, email, originalPassword, newPassword);
//
//        // when
//        userService.updateUser(user.getId(), updateDto);
//        userRepository.flush();
//
//        // then
//        EatzUser updatedUser = userRepository.findById(userId).orElseThrow(EatzUserNotFoundException::new);
//        Assertions.assertThat(updatedUser).isNotNull();
//        Assertions.assertThat(updatedUser.getUsername()).isEqualTo(username);
//        Assertions.assertThat(updatedUser.getEmail()).isEqualTo(email);
//        Assertions.assertThat(passwordEncoder.matches(newPassword, updatedUser.getPassword())).isTrue();
//
//        Assertions.assertThat(updatedUser.getCreatedAt()).isNotNull();
//        Assertions.assertThat(updatedUser.getUpdatedAt()).isNotEqualTo(updatedUser.getCreatedAt());
//        Assertions.assertThat(updatedUser.getUpdatedAt()).isNotNull();
//        Assertions.assertThat(updatedUser.getDeletedAt()).isNull();
//    }
//
//    @Test
//    @DisplayName("사용자의 정보 중 사용자 이름만 정상적으로 수정되는지 테스트합니다: 사용자 정보 일부 수정 테스트")
//    @Transactional
//    void usernameUpdateTest() {
//        // given
//        String username = "heextory";
//        String password = "1q2w3e4r!";
//        String email = "imwhs@icloud.com";
//        String encodedPassword = passwordEncoder.encode(password);
//        EatzUser user = EatzUser.createMember(username, email, encodedPassword);
//        userRepository.save(user);
//        Long userId = user.getId();
//
//        String newUsername = "2heextory";
//        EatzUserUpdateDto updateDto = new EatzUserUpdateDto(newUsername, email, password, null);
//
//        // when
//        userService.updateUser(user.getId(), updateDto);
//        userRepository.flush();
//        EatzUser updatedUser = userRepository.findById(userId).orElseThrow(EatzUserNotFoundException::new);
//
//        // then
//        Assertions.assertThat(updatedUser).isNotNull();
//        Assertions.assertThat(updatedUser.getUsername()).isEqualTo(newUsername);
//        Assertions.assertThat(updatedUser.getEmail()).isEqualTo(email);
//        Assertions.assertThat(passwordEncoder.matches(password, updatedUser.getPassword())).isTrue();
//
//        Assertions.assertThat(updatedUser.getCreatedAt()).isNotNull();
//        Assertions.assertThat(updatedUser.getUpdatedAt()).isNotEqualTo(updatedUser.getCreatedAt());
//        Assertions.assertThat(updatedUser.getUpdatedAt()).isNotNull();
//        Assertions.assertThat(updatedUser.getDeletedAt()).isNull();
//    }
//
//    @Test
//    @DisplayName("유효하지 않은 사용자의 수정이 실패하는지 테스트합니다.")
//    @Transactional
//    void invalidUserUpdateTest() {
//        // given
//        SignUpRequestDto createDto = new SignUpRequestDto(
//                "heextory",
//                "imwhs@icloud.com",
//                "1q2w3e4r!");
//
//        authService.signUp(createDto);
//
//        // when, then
//        Assertions.assertThatThrownBy(() ->
//                userService.updateUser(
//                        99999L,
//                        new EatzUserUpdateDto(
//                                "test",
//                                "test@com",
//                                "1q",
//                                null))
//        ).isInstanceOf(EatzUserNotFoundException.class);
//    }

//    @Test
//    @DisplayName("사용자가 정상적으로 삭제되는지 테스트합니다.")
//    @Transactional
//    void userDeleteTest() {
//        // given
//        String rawPassword = "1q2w3e4r!";
//        String encodedPassword = passwordEncoder.encode(rawPassword);
//        EatzUser user = EatzUser.createMember("heextory", "heextory@icloud.com", encodedPassword);
//        userRepository.save(user);
//        DeleteEatzUserRequest deleteDto = new DeleteEatzUserRequest(rawPassword);
//
//        // when
//        userService.delete(user.getId(), deleteDto);
//
//        // then
//        Assertions.assertThat(userRepository.findById(user.getId()).isPresent()).isFalse();
//    }

//    @Test
//    @DisplayName("유효하지 않은 사용자의 삭제가 실패하는지 테스트합니다.")
//    @Transactional
//    void invalidUserDeleteTest() {
//        // given
//        SignUpRequestDto createDto = new SignUpRequestDto(
//                "heextory",
//                "imwhs@icloud.com",
//                "1q2w3e4r!");
//
//        authService.signUp(createDto);
//
//        // when, then
//        Assertions.assertThatThrownBy(() ->
//                userService.deleteUser(
//                        99999L,
//                        new DeleteEatzUserDto("test"))
//        ).isInstanceOf(EatzUserNotFoundException.class);
//    }

    @Test
    @DisplayName("등록된 사용자가 식별자로 정상적으로 조회되는지 테스트합니다.")
    @Transactional
    void findUserByIdTest() {
        // given
        EatzUser user = EatzUser.createMember("heextory", "heextory@icloud.com", "1q2w3e4r!");
        userRepository.save(user);

        // when
        EatzUserDetailDto dto = userQueryService.getDetail(user.getId());

        // then
        Assertions.assertThat(dto.getUsername()).isEqualTo(user.getUsername());
        Assertions.assertThat(dto.getEmail()).isEqualTo(user.getEmail());
        Assertions.assertThat(dto.getRole()).isEqualTo(user.getEatzUserRole());
    }

    @Test
    @DisplayName("등록되지 않은 사용자가 식별자로 조회되지 않는지 테스트합니다.")
    @Transactional
    void findInvalidUserByIdTest() {
        // given
        EatzUser user = EatzUser.createMember("heextory", "heextory@icloud.com", "1q2w3e4r!");
        userRepository.save(user);

        // when, then
        Assertions.assertThatThrownBy(() ->
                userQueryService.getDetail(99999L)
        ).isInstanceOf(EatzUserNotFoundException.class);
    }

    @Test
    @DisplayName("등록된 사용자가 이메일로 정상적으로 조회되는지 테스트합니다.")
    @Transactional
    void findUserByEmailTest() {
        // given
        String email = "heextory@icloud.com";
        EatzUser user = EatzUser.createMember("heextory", email, "1q2w3e4r!");
        userRepository.save(user);

        // when
        EatzUserDetailDto dto = userQueryService.getDetailByEmail(email);

        // then
        Assertions.assertThat(dto.getUsername()).isEqualTo(user.getUsername());
        Assertions.assertThat(dto.getEmail()).isEqualTo(user.getEmail());
        Assertions.assertThat(dto.getRole()).isEqualTo(user.getEatzUserRole());
    }

    @Test
    @DisplayName("등록된 모든 사용자가 정상적으로 조회되는지 테스트합니다.")
    @Transactional
    void findAllUsersTest() {
        // given
        EatzUser user1 = EatzUser.createMember(
                "1heextory",
                "1heextory@icloud.com",
                "1q2w3e4r!");
        EatzUser user2 = EatzUser.createMember(
                "2heextory",
                "2heextory@icloud.com",
                "1q2w3e4r!");
        EatzUser user3 = EatzUser.createMember(
                "3heextory",
                "3heextory@icloud.com",
                "1q2w3e4r!");
        userRepository.save(user1);
        userRepository.save(user2);
        userRepository.save(user3);

        // when
        Page<EatzUserDetailDto> users = userQueryService.getAllDetails(PageRequest.of(0, 10));

        // then
        Assertions.assertThat(users.getTotalElements()).isEqualTo(3);
        Assertions.assertThat(users.getContent().get(0).getUsername()).isEqualTo(user1.getUsername());
        Assertions.assertThat(users.getContent().get(1).getUsername()).isEqualTo(user2.getUsername());
        Assertions.assertThat(users.getContent().get(2).getUsername()).isEqualTo(user3.getUsername());
    }

}
