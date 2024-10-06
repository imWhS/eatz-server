package imwhs.eatz_server.service.command;

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
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class EatzUserCommandServiceTest {

    @Autowired
    EatzUserCommandService userService;

    @Autowired
    EatzUserRepository userRepository;

    @Test
    @DisplayName("새 사용자가 정상적으로 등록되는지 테스트합니다.")
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
    }

    @Test
    @DisplayName("중복된 사용자 이름으로 새 사용자를 등록하려고 할 때 등록이 실패하는지 테스트합니다.")
    void duplicatedNameUserRegisterTest() {
        // given
        CreateEatzUserDto user1Dto = new CreateEatzUserDto(
                "heextory",
                "imwhs1@icloud.com",
                "1q2w3e4r!",
                Role.MEMBER);

        Long user1Id = userService.registerUser(user1Dto);

        // given
        CreateEatzUserDto user2Dto = new CreateEatzUserDto(
                "heextory",
                "imwhs2@icloud.com",
                "1q2w3e4r!",
                Role.MEMBER);

        // when, then
        Assertions.assertThatThrownBy(() -> userService.registerUser(user2Dto))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("중복된 이메일로 새 사용자를 등록하려고 할 때 등록이 실패하는지 테스트합니다.")
    void duplicatedEmailUserRegisterTest() {
        // given
        CreateEatzUserDto user1Dto = new CreateEatzUserDto(
                "hee1xtory",
                "imwhs@icloud.com",
                "1q2w3e4r!",
                Role.MEMBER);

        Long user1Id = userService.registerUser(user1Dto);

        // given
        CreateEatzUserDto user2Dto = new CreateEatzUserDto(
                "hee2xtory",
                "imwhs@icloud.com",
                "1q2w3e4r!",
                Role.MEMBER);

        // when, then
        Assertions.assertThatThrownBy(() -> userService.registerUser(user2Dto))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("사용자가 정상적으로 수정되는지 테스트합니다.")
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
        Long updatedUserId = userService.updateUser(user.getId(), updateDto);
        EatzUser updatedUser = userRepository.findById(updatedUserId).orElseThrow(EatzUserNotFoundException::new);

        // then
        Assertions.assertThat(updatedUser).isNotNull();
        Assertions.assertThat(updatedUser.getUsername()).isEqualTo("2heextory");
        Assertions.assertThat(updatedUser.getEmail()).isEqualTo("2imwhs@icloud.com");
    }

    @Test
    @DisplayName("유효하지 않은 사용자의 수정이 실패하는지 테스트합니다.")
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