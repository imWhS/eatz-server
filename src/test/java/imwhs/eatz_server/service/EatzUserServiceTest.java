package imwhs.eatz_server.service;

import imwhs.eatz_server.domain.EatzUser;
import imwhs.eatz_server.domain.Role;
import imwhs.eatz_server.dto.UpdateEatzUserDto;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@SpringBootTest
@Transactional
class EatzUserServiceTest {

    @Autowired
    private EatzUserService userService;

    @Test
    @DisplayName("새 사용자를 등록했을 때, ID로 해당 사용자가 정상적으로 조회되는지 테스트합니다.")
    void userRegisterAndFindByIdTest() {
        // given
        String userEmail = "heextory@icloud.com";
        EatzUser user = EatzUser.builder()
                .email(userEmail)
                .password("1q2w3e4r!")
                .role(Role.MEMBER)
                .build();

        // when
        userService.registerUser(user);

        // then
        EatzUser registeredUser = userService.findUserById(user.getId());
        Assertions.assertThat(registeredUser).isEqualTo(user);
        Assertions.assertThat(userEmail).isEqualTo(registeredUser.getEmail());
    }

    @Test
    @DisplayName("새 사용자를 등록했을 때, 사용자 이름으로 해당 사용자가 정상적으로 조회되는지 테스트합니다.")
    void userRegisterAndFindByUsernameTest() {
        // given
        String username = "john";
        EatzUser user = EatzUser.builder()
                .username(username)
                .email("heextory@icloud.com")
                .password("1q2w3e4r!")
                .role(Role.MEMBER)
                .build();

        // when
        userService.registerUser(user);

        // then
        EatzUser registeredUser = userService.findUserByUsername(username);
        Assertions.assertThat(registeredUser).isEqualTo(user);
        Assertions.assertThat(user.getEmail()).isEqualTo(registeredUser.getEmail());
    }

    @Test
    @DisplayName("등록한 여러 명의 사용자 모두를 정상적으로 조회할 수 있는지 테스트합니다.")
    void findAllTest() {
        // given
        EatzUser userA = EatzUser.builder()
                .username("userA")
                .email("userA@email.com")
                .password("1q2w3e4r!")
                .role(Role.MEMBER)
                .build();

        EatzUser userB = EatzUser.builder()
                .username("userB")
                .email("userB@email.com")
                .password("1q2w3e4r!")
                .role(Role.MEMBER)
                .build();

        EatzUser userC = EatzUser.builder()
                .username("userC")
                .email("userC@email.com")
                .password("1q2w3e4r!")
                .role(Role.MEMBER)
                .build();

        // when
        userService.registerUser(userA);
        userService.registerUser(userB);
        userService.registerUser(userC);

        // then
        List<EatzUser> allUsers = userService.findAllUsers();
        Assertions.assertThat(allUsers.size()).isEqualTo(3);
    }

    @Test
    @DisplayName("사용자의 정보가 정상적으로 수정되는지 테스트합니다.")
    void updateUserTest() {
        // given
        String username = "heextory";
        String updatedUsername = "mystory";
        String email = "heextory@icloud.com";
        String updatedEmail = "mystory@google.com";
        String password = "1q2w3e4r!";
        String updatedPassword = "9876";


        EatzUser user = EatzUser.builder()
                .username(username)
                .email(email)
                .password(password)
                .build();

        // when
        userService.registerUser(user);

        // then
        UpdateEatzUserDto updateEatzUserDTO = new UpdateEatzUserDto();
        updateEatzUserDTO.setUsername(updatedUsername);
        updateEatzUserDTO.setEmail(updatedEmail);
        updateEatzUserDTO.setPassword(updatedPassword);
        userService.updateUser(user.getId(), updateEatzUserDTO);

        // then
        EatzUser updatedUser = userService.findUserById(user.getId());
        Assertions.assertThat(updatedUser.getUsername()).isEqualTo(updatedUsername);
        Assertions.assertThat(updatedUser.getEmail()).isEqualTo(updatedEmail);
        Assertions.assertThat(updatedUser.getPassword()).isEqualTo(updatedPassword);
    }

    @Test
    @DisplayName("사용자가 정상적으로 삭제되는지 테스트합니다.")
    void deleteUserTest() {
        // given
        EatzUser user = EatzUser.builder()
                .username("userA")
                .email("userA@email.com")
                .password("1q2w3e4r!")
                .role(Role.MEMBER)
                .build();

        // when
        userService.registerUser(user);
        List<EatzUser> allUsersInitial = userService.findAllUsers();

        // then
        userService.deleteUser(user.getId());
        List<EatzUser> allUsers = userService.findAllUsers();
        Assertions.assertThat(allUsers.size()).isEqualTo(allUsersInitial.size() - 1);
    }
}