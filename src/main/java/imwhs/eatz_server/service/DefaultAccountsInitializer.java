package imwhs.eatz_server.service;

import imwhs.eatz_server.config.properties.AdminConfigProperties;
import imwhs.eatz_server.domain.eatzuser.EatzUser;
import imwhs.eatz_server.repository.EatzUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Order(1)
@Slf4j
@RequiredArgsConstructor
@Component
public class DefaultAccountsInitializer {

    private final EatzUserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final AdminConfigProperties adminConfigProperties;

    @Bean
    public ApplicationRunner accountInitializer() {
        return args -> {
            String email = adminConfigProperties.getEmail();
            String password = adminConfigProperties.getPassword();

            if (userRepository.findByEmailAndDeletedAtIsNull(email).isEmpty()) {
                EatzUser admin = EatzUser.createAdmin("admin", email, passwordEncoder.encode(password));
                userRepository.save(admin);
                log.info("관리자 계정을 생성했어요!");
            }

            // 초기 레시피 작성 및 실사용 일반 계정(heextory) 생성
            String heextoryEmail = "heextory@eatz.io";
            String heextoryPassword = "dnjsgml9^^";

            if (userRepository.findByEmailAndDeletedAtIsNull(heextoryEmail).isEmpty()) {
                // 이전 DataSeeder에서 사용하시던 스펙(이름, 이메일, 비밀번호)에 맞춰 생성합니다.
                // 만약 프로필 이미지 URL도 넣을 수 있다면 더 좋습니다!
                EatzUser heextory = EatzUser.createMember("heextory", heextoryEmail, passwordEncoder.encode(heextoryPassword));
                userRepository.save(heextory);
                log.info("초기 레시피 작성용 계정을 생성했어요!");
            }
        };
    }
}
