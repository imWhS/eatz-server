package imwhs.eatz_server.service;

import imwhs.eatz_server.domain.EatzUser;
import imwhs.eatz_server.domain.Role;
import imwhs.eatz_server.dto.eatzuser.CreateEatzUserDto;
import imwhs.eatz_server.dto.eatzuser.UpdateEatzUserDto;
import imwhs.eatz_server.exception.EatzUserNotFoundException;
import imwhs.eatz_server.repository.eatzuser.EatzUserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@SpringBootTest
public class EatzUserServiceTest {

    @Autowired
    private EatzUserService userService;

    @Autowired
    private EatzUserRepository userRepository;

    @Test
    @DisplayName("새 사용자가 정상적으로 등록되는지 테스트합니다.")
    @Transactional
    void userRegisterTest() {
        // given
        CreateEatzUserDto dto = new CreateEatzUserDto(
                "heextory",
                "imwhs@icloud.com",
                "1q2w3e4r!",
                Role.MEMBER);

        Long userId = userService.registerUser(dto);

        // when
        EatzUser user = userRepository.findById(userId).orElseThrow(EatzUserNotFoundException::new);

        // then
        Assertions.assertThat(user).isNotNull();
        Assertions.assertThat(user.getUsername()).isEqualTo("heextory");
        Assertions.assertThat(user.getEmail()).isEqualTo("imwhs@icloud.com");
        Assertions.assertThat(user.getRole()).isEqualTo(Role.MEMBER);
        Assertions.assertThat(user.getCreatedAt()).isNotNull();
        Assertions.assertThat(user.getUpdatedAt()).isEqualTo(user.getCreatedAt());
        Assertions.assertThat(user.getDeletedAt()).isNull();
    }

    @Test
    @DisplayName("중복된 사용자 이름으로 새 사용자를 등록하려고 할 때 등록이 실패하는지 테스트합니다.")
    @Transactional
    void duplicatedNameUserRegisterTest() {
        String username = "heextory";

        // given
        CreateEatzUserDto user1Dto = new CreateEatzUserDto(username, "imwhs1@icloud.com", "1q2w3e4r!", Role.MEMBER);
        CreateEatzUserDto user2Dto = new CreateEatzUserDto(username, "imwhs2@icloud.com", "1q2w3e4r!", Role.MEMBER);

        // when, then
        userService.registerUser(user1Dto);
        Assertions.assertThatThrownBy(() -> userService.registerUser(user2Dto))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("중복된 이메일로 새 사용자를 등록하려고 할 때 등록이 실패하는지 테스트합니다.")
    @Transactional
    void duplicatedEmailUserRegisterTest() {
        // given
        String email = "imwhs@icloud.com";
        CreateEatzUserDto user1Dto = new CreateEatzUserDto("hee1xtory", email, "1q2w3e4r!", Role.MEMBER);
        CreateEatzUserDto user2Dto = new CreateEatzUserDto("hee2xtory", email, "1q2w3e4r!", Role.MEMBER);

        // when, then
        userService.registerUser(user1Dto);
        Assertions.assertThatThrownBy(() -> userService.registerUser(user2Dto))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("사용자의 정보가 정상적으로 수정되는지 테스트합니다.")
    @Transactional
    void userUpdateTest() {
        // given
        CreateEatzUserDto createDto = new CreateEatzUserDto(
                "heextory",
                "imwhs@icloud.com",
                "1q2w3e4r!",
                Role.MEMBER);
        Long userId = userService.registerUser(createDto);
        EatzUser user = userRepository.findById(userId).orElseThrow(EatzUserNotFoundException::new);

        UpdateEatzUserDto updateDto = new UpdateEatzUserDto();
        updateDto.setUsername("2heextory");
        updateDto.setEmail("2imwhs@icloud.com");
        updateDto.setPassword("21q2w3e4r!");

        // when
        userService.updateUser(user.getId(), updateDto);
        userRepository.flush();
        EatzUser updatedUser = userRepository.findById(userId).orElseThrow(EatzUserNotFoundException::new);

        // then
        Assertions.assertThat(updatedUser).isNotNull();
        Assertions.assertThat(updatedUser.getUsername()).isEqualTo("2heextory");
        Assertions.assertThat(updatedUser.getEmail()).isEqualTo("2imwhs@icloud.com");
        Assertions.assertThat(updatedUser.getPassword()).isEqualTo("21q2w3e4r!");
        Assertions.assertThat(updatedUser.getRole()).isEqualTo(Role.MEMBER);
        Assertions.assertThat(updatedUser.getCreatedAt()).isNotNull();
        Assertions.assertThat(updatedUser.getUpdatedAt()).isNotEqualTo(updatedUser.getCreatedAt());
        Assertions.assertThat(updatedUser.getUpdatedAt()).isNotNull();
        Assertions.assertThat(updatedUser.getDeletedAt()).isNull();
    }

    @Test
    @DisplayName("사용자의 정보 중 이메일 주소만 정상적으로 수정되는지 테스트합니다: 사용자 정보 일부 수정 테스트")
    @Transactional
    void emailUpdateTest() {
        // given
        String username = "heextory";
        String password = "1q2w3e4r!";
        CreateEatzUserDto createDto = new CreateEatzUserDto(
                username,
                "imwhs@icloud.com",
                password,
                Role.MEMBER);
        Long userId = userService.registerUser(createDto);
        EatzUser user = userRepository.findById(userId).orElseThrow(EatzUserNotFoundException::new);

        UpdateEatzUserDto updateDto = new UpdateEatzUserDto();
        String updatedEmail = "2imwhs@icloud.com";
        updateDto.setEmail(updatedEmail);

        // when
        userService.updateUser(user.getId(), updateDto);
        userRepository.flush();
        EatzUser updatedUser = userRepository.findById(userId).orElseThrow(EatzUserNotFoundException::new);

        // then
        Assertions.assertThat(updatedUser).isNotNull();
        Assertions.assertThat(updatedUser.getUsername()).isEqualTo(username);
        Assertions.assertThat(updatedUser.getEmail()).isEqualTo(updatedEmail);
        Assertions.assertThat(updatedUser.getPassword()).isEqualTo(password);
        Assertions.assertThat(updatedUser.getRole()).isEqualTo(Role.MEMBER);
        Assertions.assertThat(updatedUser.getCreatedAt()).isNotNull();
        Assertions.assertThat(updatedUser.getUpdatedAt()).isNotEqualTo(updatedUser.getCreatedAt());
        Assertions.assertThat(updatedUser.getUpdatedAt()).isNotNull();
        Assertions.assertThat(updatedUser.getDeletedAt()).isNull();
    }

    @Test
    @DisplayName("사용자의 정보 중 비밀 번호만 정상적으로 수정되는지 테스트합니다: 사용자 정보 일부 수정 테스트")
    @Transactional
    void passwordUpdateTest() {
        // given
        String username = "heextory";
        String email = "imwhs@icloud.com";
        CreateEatzUserDto createDto = new CreateEatzUserDto(
                username,
                email,
                "1q2w3e4r!",
                Role.MEMBER);
        Long userId = userService.registerUser(createDto);
        EatzUser user = userRepository.findById(userId).orElseThrow(EatzUserNotFoundException::new);

        UpdateEatzUserDto updateDto = new UpdateEatzUserDto();
        String updatedPassword = "2q2w3e4r!";
        updateDto.setPassword(updatedPassword);

        // when
        userService.updateUser(user.getId(), updateDto);
        userRepository.flush();
        EatzUser updatedUser = userRepository.findById(userId).orElseThrow(EatzUserNotFoundException::new);

        // then
        Assertions.assertThat(updatedUser).isNotNull();
        Assertions.assertThat(updatedUser.getUsername()).isEqualTo(username);
        Assertions.assertThat(updatedUser.getEmail()).isEqualTo(email);
        Assertions.assertThat(updatedUser.getPassword()).isEqualTo(updatedPassword);
        Assertions.assertThat(updatedUser.getRole()).isEqualTo(Role.MEMBER);
        Assertions.assertThat(updatedUser.getCreatedAt()).isNotNull();
        Assertions.assertThat(updatedUser.getUpdatedAt()).isNotEqualTo(updatedUser.getCreatedAt());
        Assertions.assertThat(updatedUser.getUpdatedAt()).isNotNull();
        Assertions.assertThat(updatedUser.getDeletedAt()).isNull();
    }

    @Test
    @DisplayName("사용자의 정보 중 사용자 이름만 정상적으로 수정되는지 테스트합니다: 사용자 정보 일부 수정 테스트")
    @Transactional
    void usernameUpdateTest() {
        // given
        String password = "1q2w3e4r!";
        String email = "imwhs@icloud.com";
        CreateEatzUserDto createDto = new CreateEatzUserDto(
                "heextory",
                email,
                password,
                Role.MEMBER);
        Long userId = userService.registerUser(createDto);
        EatzUser user = userRepository.findById(userId).orElseThrow(EatzUserNotFoundException::new);

        UpdateEatzUserDto updateDto = new UpdateEatzUserDto();
        String updatedUsername = "heextory2";
        updateDto.setUsername(updatedUsername);

        // when
        userService.updateUser(user.getId(), updateDto);
        userRepository.flush();
        EatzUser updatedUser = userRepository.findById(userId).orElseThrow(EatzUserNotFoundException::new);

        // then
        Assertions.assertThat(updatedUser).isNotNull();
        Assertions.assertThat(updatedUser.getUsername()).isEqualTo(updatedUsername);
        Assertions.assertThat(updatedUser.getEmail()).isEqualTo(email);
        Assertions.assertThat(updatedUser.getPassword()).isEqualTo(password);
        Assertions.assertThat(updatedUser.getRole()).isEqualTo(Role.MEMBER);
        Assertions.assertThat(updatedUser.getCreatedAt()).isNotNull();
        Assertions.assertThat(updatedUser.getUpdatedAt()).isNotEqualTo(updatedUser.getCreatedAt());
        Assertions.assertThat(updatedUser.getUpdatedAt()).isNotNull();
        Assertions.assertThat(updatedUser.getDeletedAt()).isNull();
    }

    @Test
    @DisplayName("유효하지 않은 사용자의 수정이 실패하는지 테스트합니다.")
    @Transactional
    void invalidUserUpdateTest() {
        // given
        CreateEatzUserDto createDto = new CreateEatzUserDto(
                "heextory",
                "imwhs@icloud.com",
                "1q2w3e4r!",
                Role.MEMBER);

        userService.registerUser(createDto);

        // when, then
        Assertions.assertThatThrownBy(() -> userService.updateUser(99999L, new UpdateEatzUserDto())).isInstanceOf(EatzUserNotFoundException.class);
    }

    @Test
    @DisplayName("사용자가 정상적으로 삭제되는지 테스트합니다.")
    @Transactional
    void userDeleteTest() {
        // given
        CreateEatzUserDto createDto = new CreateEatzUserDto(
                "heextory",
                "imwhs@icloud.com",
                "1q2w3e4r!",
                Role.MEMBER);

        Long userId = userService.registerUser(createDto);

        // when
        userService.deleteUser(userId);

        // then
        Assertions.assertThat(userRepository.findById(userId).isPresent()).isFalse();
    }

    @Test
    @DisplayName("유효하지 않은 사용자의 삭제가 실패하는지 테스트합니다.")
    @Transactional
    void invalidUserDeleteTest() {
        // given
        CreateEatzUserDto createDto = new CreateEatzUserDto(
                "heextory",
                "imwhs@icloud.com",
                "1q2w3e4r!",
                Role.MEMBER);

        userService.registerUser(createDto);

        // when, then
        Assertions.assertThatThrownBy(() -> userService.deleteUser(99999L)).isInstanceOf(EatzUserNotFoundException.class);
    }

}
