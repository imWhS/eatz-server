package imwhs.eatz_server.config;

import imwhs.eatz_server.config.properties.AdminConfigProperties;
import imwhs.eatz_server.domain.eatzuser.EatzUser;
import imwhs.eatz_server.domain.eatzuser.EatzUserRole;
import imwhs.eatz_server.repository.eatzuser.EatzUserRepository;
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

            if (userRepository.findByEmail(email).isEmpty()) {
                EatzUser admin = new EatzUser("admin", email, passwordEncoder.encode(password), EatzUserRole.ROLE_ADMIN);
                userRepository.save(admin);
                log.info("관리자 계정을 생성했어요!");
            }
        };
    }
    }
