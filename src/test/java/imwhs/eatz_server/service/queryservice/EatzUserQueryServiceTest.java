package imwhs.eatz_server.service.queryservice;

import imwhs.eatz_server.domain.EatzUser;
import imwhs.eatz_server.domain.Role;
import imwhs.eatz_server.dto.EatzUserResponseDto;
import imwhs.eatz_server.exception.EatzUserNotFoundException;
import imwhs.eatz_server.repository.EatzUserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;

@SpringBootTest
class EatzUserQueryServiceTest {

    @Autowired
    EatzUserQueryService userQueryService;

    @Autowired
    EatzUserRepository userRepository;

    @Test
    @DisplayName("등록된 사용자가 식별자로 정상적으로 조회되는지 테스트합니다.")
    void findUserByIdTest() {
        // given
        EatzUser user = EatzUser.create("heextory", "heextory@icloud.com", "1q2w3e4r!", Role.MEMBER);
        userRepository.save(user);

        // when
        EatzUserResponseDto dto = userQueryService.findById(user.getId());

        // then
        Assertions.assertThat(dto.getUsername()).isEqualTo(user.getUsername());
        Assertions.assertThat(dto.getEmail()).isEqualTo(user.getEmail());
        Assertions.assertThat(dto.getRole()).isEqualTo(user.getRole());
    }

    @Test
    @DisplayName("등록되지 않은 사용자가 식별자로 조회되지 않는지 테스트합니다.")
    void findInvalidUserByIdTest() {
        // given
        EatzUser user = EatzUser.create("heextory", "heextory@icloud.com", "1q2w3e4r!", Role.MEMBER);
        userRepository.save(user);

        // when, then
        Assertions.assertThatThrownBy(() -> userQueryService.findById(99999L)).isInstanceOf(EatzUserNotFoundException.class);
    }

    @Test
    @DisplayName("등록된 사용자가 이메일로 정상적으로 조회되는지 테스트합니다.")
    void findUserByEmailTest() {
        // given
        String email = "heextory@icloud.com";
        EatzUser user = EatzUser.create("heextory", email, "1q2w3e4r!", Role.MEMBER);
        userRepository.save(user);

        // when
        EatzUserResponseDto dto = userQueryService.findByEmail(email);

        // then
        Assertions.assertThat(dto.getUsername()).isEqualTo(user.getUsername());
        Assertions.assertThat(dto.getEmail()).isEqualTo(user.getEmail());
        Assertions.assertThat(dto.getRole()).isEqualTo(user.getRole());
    }

    @Test
    @DisplayName("등록된 모든 사용자가 정상적으로 조회되는지 테스트합니다.")
    void findAllUsersTest() {
        // given
        EatzUser user1 = EatzUser.create("1heextory", "1heextory@icloud.com", "1q2w3e4r!", Role.MEMBER);
        EatzUser user2 = EatzUser.create("2heextory", "2heextory@icloud.com", "1q2w3e4r!", Role.MEMBER);
        EatzUser user3 = EatzUser.create("3heextory", "3heextory@icloud.com", "1q2w3e4r!", Role.MEMBER);
        userRepository.save(user1);
        userRepository.save(user2);
        userRepository.save(user3);

        // when
        Page<EatzUserResponseDto> users = userQueryService.findAll(null, null);

        // then
        Assertions.assertThat(users.getTotalElements()).isEqualTo(3);
        Assertions.assertThat(users.getContent().get(0).getUsername()).isEqualTo(user1.getUsername());
        Assertions.assertThat(users.getContent().get(1).getUsername()).isEqualTo(user2.getUsername());
        Assertions.assertThat(users.getContent().get(2).getUsername()).isEqualTo(user3.getUsername());
    }

}