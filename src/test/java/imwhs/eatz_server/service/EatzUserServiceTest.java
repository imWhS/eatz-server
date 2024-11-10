package imwhs.eatz_server.service;

import imwhs.eatz_server.domain.EatzUser;
import imwhs.eatz_server.domain.Role;
import imwhs.eatz_server.dto.eatzuser.EatzUserCreateDto;
import imwhs.eatz_server.dto.eatzuser.EatzUserResponseDto;
import imwhs.eatz_server.dto.eatzuser.EatzUserUpdateDto;
import imwhs.eatz_server.exception.EatzUserNotFoundException;
import imwhs.eatz_server.repository.eatzuser.EatzUserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
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
        EatzUserCreateDto dto = new EatzUserCreateDto(
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
        EatzUserCreateDto user1Dto = new EatzUserCreateDto(username, "imwhs1@icloud.com", "1q2w3e4r!", Role.MEMBER);
        EatzUserCreateDto user2Dto = new EatzUserCreateDto(username, "imwhs2@icloud.com", "1q2w3e4r!", Role.MEMBER);

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
        EatzUserCreateDto user1Dto = new EatzUserCreateDto("hee1xtory", email, "1q2w3e4r!", Role.MEMBER);
        EatzUserCreateDto user2Dto = new EatzUserCreateDto("hee2xtory", email, "1q2w3e4r!", Role.MEMBER);

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
        EatzUserCreateDto createDto = new EatzUserCreateDto(
                "heextory",
                "imwhs@icloud.com",
                "1q2w3e4r!",
                Role.MEMBER);
        Long userId = userService.registerUser(createDto);
        EatzUser user = userRepository.findById(userId).orElseThrow(EatzUserNotFoundException::new);

        EatzUserUpdateDto updateDto = new EatzUserUpdateDto();
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
        EatzUserCreateDto createDto = new EatzUserCreateDto(
                username,
                "imwhs@icloud.com",
                password,
                Role.MEMBER);
        Long userId = userService.registerUser(createDto);
        EatzUser user = userRepository.findById(userId).orElseThrow(EatzUserNotFoundException::new);

        EatzUserUpdateDto updateDto = new EatzUserUpdateDto();
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
        EatzUserCreateDto createDto = new EatzUserCreateDto(
                username,
                email,
                "1q2w3e4r!",
                Role.MEMBER);
        Long userId = userService.registerUser(createDto);
        EatzUser user = userRepository.findById(userId).orElseThrow(EatzUserNotFoundException::new);

        EatzUserUpdateDto updateDto = new EatzUserUpdateDto();
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
        EatzUserCreateDto createDto = new EatzUserCreateDto(
                "heextory",
                email,
                password,
                Role.MEMBER);
        Long userId = userService.registerUser(createDto);
        EatzUser user = userRepository.findById(userId).orElseThrow(EatzUserNotFoundException::new);

        EatzUserUpdateDto updateDto = new EatzUserUpdateDto();
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
        EatzUserCreateDto createDto = new EatzUserCreateDto(
                "heextory",
                "imwhs@icloud.com",
                "1q2w3e4r!",
                Role.MEMBER);

        userService.registerUser(createDto);

        // when, then
        Assertions.assertThatThrownBy(() -> userService.updateUser(99999L, new EatzUserUpdateDto())).isInstanceOf(EatzUserNotFoundException.class);
    }

    @Test
    @DisplayName("사용자가 정상적으로 삭제되는지 테스트합니다.")
    @Transactional
    void userDeleteTest() {
        // given
        EatzUserCreateDto createDto = new EatzUserCreateDto(
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
        EatzUserCreateDto createDto = new EatzUserCreateDto(
                "heextory",
                "imwhs@icloud.com",
                "1q2w3e4r!",
                Role.MEMBER);

        userService.registerUser(createDto);

        // when, then
        Assertions.assertThatThrownBy(() -> userService.deleteUser(99999L)).isInstanceOf(EatzUserNotFoundException.class);
    }


    @Test
    @DisplayName("등록된 사용자가 식별자로 정상적으로 조회되는지 테스트합니다.")
    @Transactional
    void findUserByIdTest() {
        // given
        EatzUser user = EatzUser.create("heextory", "heextory@icloud.com", "1q2w3e4r!", Role.MEMBER);
        userRepository.save(user);

        // when
        EatzUserResponseDto dto = userService.findUserById(user.getId());

        // then
        Assertions.assertThat(dto.getUsername()).isEqualTo(user.getUsername());
        Assertions.assertThat(dto.getEmail()).isEqualTo(user.getEmail());
        Assertions.assertThat(dto.getRole()).isEqualTo(user.getRole());
    }

    @Test
    @DisplayName("등록되지 않은 사용자가 식별자로 조회되지 않는지 테스트합니다.")
    @Transactional
    void findInvalidUserByIdTest() {
        // given
        EatzUser user = EatzUser.create("heextory", "heextory@icloud.com", "1q2w3e4r!", Role.MEMBER);
        userRepository.save(user);

        // when, then
        Assertions.assertThatThrownBy(() -> userService.findUserById(99999L)).isInstanceOf(EatzUserNotFoundException.class);
    }

    @Test
    @DisplayName("등록된 사용자가 이메일로 정상적으로 조회되는지 테스트합니다.")
    @Transactional
    void findUserByEmailTest() {
        // given
        String email = "heextory@icloud.com";
        EatzUser user = EatzUser.create("heextory", email, "1q2w3e4r!", Role.MEMBER);
        userRepository.save(user);

        // when
        EatzUserResponseDto dto = userService.findUserByEmail(email);

        // then
        Assertions.assertThat(dto.getUsername()).isEqualTo(user.getUsername());
        Assertions.assertThat(dto.getEmail()).isEqualTo(user.getEmail());
        Assertions.assertThat(dto.getRole()).isEqualTo(user.getRole());
    }

    @Test
    @DisplayName("등록된 모든 사용자가 정상적으로 조회되는지 테스트합니다.")
    @Transactional
    void findAllUsersTest() {
        // given
        EatzUser user1 = EatzUser.create("1heextory", "1heextory@icloud.com", "1q2w3e4r!", Role.MEMBER);
        EatzUser user2 = EatzUser.create("2heextory", "2heextory@icloud.com", "1q2w3e4r!", Role.MEMBER);
        EatzUser user3 = EatzUser.create("3heextory", "3heextory@icloud.com", "1q2w3e4r!", Role.MEMBER);
        userRepository.save(user1);
        userRepository.save(user2);
        userRepository.save(user3);

        // when
        Page<EatzUserResponseDto> users = userService.findAllUsers(null, null);

        // then
        Assertions.assertThat(users.getTotalElements()).isEqualTo(3);
        Assertions.assertThat(users.getContent().get(0).getUsername()).isEqualTo(user1.getUsername());
        Assertions.assertThat(users.getContent().get(1).getUsername()).isEqualTo(user2.getUsername());
        Assertions.assertThat(users.getContent().get(2).getUsername()).isEqualTo(user3.getUsername());
    }

}
