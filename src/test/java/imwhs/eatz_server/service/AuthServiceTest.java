package imwhs.eatz_server.service;

import imwhs.eatz_server.repository.EatzUserRepository;
import imwhs.eatz_server.service.auth.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class AuthServiceTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private EatzUserRepository userRepository;

//    @Test
//    @DisplayName("새 사용자가 정상적으로 등록되는지 테스트합니다.")
//    @Transactional
//    void userRegisterTest() {
//        // given
//        SignUpRequestDto dto = new SignUpRequestDto(
//                "heextory",
//                "imwhs@icloud.com",
//                "1q2w3e4r!");
//
//        // when
//        Long userId = authService.signUp(dto);
//
//        // then
//        EatzUser user = userRepository.findById(userId).orElseThrow(EatzUserNotFoundException::new);
//        Assertions.assertThat(user).isNotNull();
//        Assertions.assertThat(user.getUsername()).isEqualTo("heextory");
//        Assertions.assertThat(user.getEmail()).isEqualTo("imwhs@icloud.com");
//        Assertions.assertThat(user.getCreatedAt()).isNotNull();
//        Assertions.assertThat(user.getUpdatedAt()).isEqualTo(user.getCreatedAt());
//        Assertions.assertThat(user.getDeletedAt()).isNull();
//    }
//
//    @Test
//    @DisplayName("중복된 사용자 이름으로 새 사용자를 등록하려고 할 때 등록이 실패하는지 테스트합니다.")
//    @Transactional
//    void duplicatedNameUserRegisterTest() {
//        String username = "heextory";
//
//        // given
//        SignUpRequestDto user1Dto = new SignUpRequestDto(username, "imwhs1@icloud.com", "1q2w3e4r!");
//        SignUpRequestDto user2Dto = new SignUpRequestDto(username, "imwhs2@icloud.com", "1q2w3e4r!");
//
//        // when, then
//        authService.signUp(user1Dto);
//        Assertions.assertThatThrownBy(() -> authService.signUp(user2Dto))
//                .isInstanceOf(DuplicatedEatzUserException.class);
//    }
//
//    @Test
//    @DisplayName("중복된 이메일로 새 사용자를 등록하려고 할 때 등록이 실패하는지 테스트합니다.")
//    @Transactional
//    void duplicatedEmailUserRegisterTest() {
//        // given
//        String email = "imwhs@icloud.com";
//        SignUpRequestDto user1Dto = new SignUpRequestDto("hee1xtory", email, "1q2w3e4r!");
//        SignUpRequestDto user2Dto = new SignUpRequestDto("hee2xtory", email, "1q2w3e4r!");
//
//        // when, then
//        authService.signUp(user1Dto);
//        Assertions.assertThatThrownBy(() -> authService.signUp(user2Dto))
//                .isInstanceOf(DuplicatedEatzUserException.class);
//    }

}
