package imwhs.eatz_server.service;

import imwhs.eatz_server.domain.Role;
import imwhs.eatz_server.dto.CreateEatzUserDto;
import imwhs.eatz_server.dto.EatzUserResponseDto;
import imwhs.eatz_server.dto.UpdateEatzUserDto;
import imwhs.eatz_server.exception.EatzUserNotFoundException;
import imwhs.eatz_server.queryservice.EatzUserQueryService;
import imwhs.eatz_server.repository.EatzUserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class EatzUserServiceTest {

    @Autowired
    private EatzUserService userService;

    @Autowired
    private EatzUserQueryService userQueryService;

    @Autowired
    private EatzUserRepository eatzUserRepository;

    @Test
    @DisplayName("새 사용자를 등록했을 때, ID로 해당 사용자가 정상적으로 조회되는지 테스트합니다.")
    void userRegisterAndFindByIdTest() {
        // given
        String userEmail = "heextory@icloud.com";
        CreateEatzUserDto createEatzUserDto = createEatzUserDto("test", userEmail);

        // when
        EatzUserResponseDto eatzUserResponseDto = userService.registerUser(createEatzUserDto);

        // then
        EatzUserResponseDto registeredUserResponseDto = userQueryService.findById(eatzUserResponseDto.getId());
        Assertions.assertThat(createEatzUserDto.getUsername()).isEqualTo(registeredUserResponseDto.getUsername());
        Assertions.assertThat(createEatzUserDto.getEmail()).isEqualTo(registeredUserResponseDto.getEmail());
        Assertions.assertThat(registeredUserResponseDto).isEqualTo(eatzUserResponseDto);
        Assertions.assertThat(userEmail).isEqualTo(registeredUserResponseDto.getEmail());
    }

    @Test
    @DisplayName("중복된 사용자 이름으로 새 사용자를 등록하려고 할 때, 예외가 발생하는지 테스트합니다.")
    void usernameDuplicatesTest() {
        // given
        CreateEatzUserDto createEatzUserDtoA = createEatzUserDto("test", "heextoryA@icloud.com");
        CreateEatzUserDto createEatzUserDtoB = createEatzUserDto("test", "heextoryB@icloud.com");

        // when
        userService.registerUser(createEatzUserDtoA);

        // then
        Assertions.assertThatThrownBy(() -> userService.registerUser(createEatzUserDtoB))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("새 사용자를 등록했을 때, 사용자 이름으로 해당 사용자가 정상적으로 조회되는지 테스트합니다.")
    void userRegisterAndFindByUsernameTest() {
        // given
        String username = "john";
        CreateEatzUserDto createEatzUserDto = createEatzUserDto(username, "heextory@icloud.com");

        // when
        EatzUserResponseDto registeredEatzUserResponseDto = userService.registerUser(createEatzUserDto);

        // then
        EatzUserResponseDto foundEatzUserResponseDto = userQueryService.findByUsername(username);
        Assertions.assertThat(userQueryService.findById(foundEatzUserResponseDto.getId())).isNotNull();
        Assertions.assertThat(foundEatzUserResponseDto).isEqualTo(registeredEatzUserResponseDto);
        Assertions.assertThat(registeredEatzUserResponseDto.getEmail()).isEqualTo(foundEatzUserResponseDto.getEmail());
    }

    @Test
    @DisplayName("등록한 여러 명의 사용자 모두를 정상적으로 조회할 수 있는지 테스트합니다.")
    void findAllTest() {
        // given
        CreateEatzUserDto createEatzUserDtoA = createEatzUserDto("heextoryA", "heextoryA@gmail.com");
        CreateEatzUserDto createEatzUserDtoB = createEatzUserDto("heextoryB", "heextoryB@gmail.com");
        CreateEatzUserDto createEatzUserDtoC = createEatzUserDto("heextoryC", "heextoryC@gmail.com");

        // when
        userService.registerUser(createEatzUserDtoA);
        userService.registerUser(createEatzUserDtoB);
        userService.registerUser(createEatzUserDtoC);

        // then
        Page<EatzUserResponseDto> allUsers = userQueryService.findAll(null, null);
        Assertions.assertThat(allUsers.getContent().size()).isEqualTo(3);
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

        CreateEatzUserDto dto = CreateEatzUserDto.builder()
                .username(username)
                .email(email)
                .password(password)
                .role(Role.MEMBER)
                .build();

        // when
        EatzUserResponseDto eatzUserResponseDto = userService.registerUser(dto);

        // then
        UpdateEatzUserDto updateEatzUserDto = new UpdateEatzUserDto();
        updateEatzUserDto.setUsername(updatedUsername);
        updateEatzUserDto.setEmail(updatedEmail);
        updateEatzUserDto.setPassword(updatedPassword);
        userService.updateUser(eatzUserResponseDto.getId(), updateEatzUserDto);

        // then
        EatzUserResponseDto updatedUser = userQueryService.findById(eatzUserResponseDto.getId());
        Assertions.assertThat(updatedUser.getUsername()).isEqualTo(updatedUsername);
        Assertions.assertThat(updatedUser.getEmail()).isEqualTo(updatedEmail);
    }

    @Test
    @DisplayName("사용자가 정상적으로 삭제되는지 테스트합니다.")
    void deleteUserTest() {
        // given
        CreateEatzUserDto dto = CreateEatzUserDto.builder()
                .username("test")
                .email("heextory@icloud.com")
                .password("1q2w3e4r!")
                .role(Role.MEMBER)
                .build();

        // when
        EatzUserResponseDto registeredUser = userService.registerUser(dto);
        Page<EatzUserResponseDto> allUsersInitial = userQueryService.findAll(null, null);

        // then
        userService.deleteUser(registeredUser.getId());
        Page<EatzUserResponseDto> allUsers = userQueryService.findAll(null, null);
        Assertions.assertThat(allUsers.getContent().size()).isEqualTo(allUsersInitial.getContent().size() - 1);
        Assertions.assertThatThrownBy(() -> userQueryService.findById(registeredUser.getId()))
                .isInstanceOf(EatzUserNotFoundException.class);
    }

    private CreateEatzUserDto createEatzUserDto(
            String username,
            String email) {
        return CreateEatzUserDto.builder()
                .username(username)
                .email(email)
                .password("1q2w3e4r!")
                .role(Role.MEMBER)
                .build();
    }

}