package imwhs.eatz_server.config;

import imwhs.eatz_server.config.properties.AdminConfigProperties;
import imwhs.eatz_server.domain.eatzuser.EatzUser;
import imwhs.eatz_server.repository.EatzUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class AdminInitializer {

    private final EatzUserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final AdminConfigProperties adminConfigProperties;

    @Bean
    public ApplicationRunner adminAccountInitializer() {
        return args -> {
            String email = adminConfigProperties.getEmail();
            String password = adminConfigProperties.getPassword();

            if (userRepository.findByEmailAndDeletedAtIsNull(email).isEmpty()) {
                EatzUser admin = EatzUser.createAdmin("admin", email, passwordEncoder.encode(password));
                userRepository.save(admin);
                log.info("관리자 계정을 생성했어요!");
            }
        };
    }
}
