package imwhs.eatz_server.config;

import imwhs.eatz_server.config.properties.AdminProperties;
import imwhs.eatz_server.domain.EatzUser;
import imwhs.eatz_server.domain.Role;
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

    private final AdminProperties adminProperties;

    @Bean
    public ApplicationRunner adminAccountInitializer() {
        return args -> {
            String email = adminProperties.getEmail();
            String password = adminProperties.getPassword();

            if (userRepository.findByEmail(email).isEmpty()) {
                EatzUser admin = new EatzUser("admin", email, passwordEncoder.encode(password), Role.ADMIN);
                userRepository.save(admin);
                log.info("관리자 계정을 생성했어요!");
            } else {
                log.info("관리자 계정이 이미 존재해서 생성하지 않았어요!");
            }
        };
    }
    }
