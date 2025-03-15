package imwhs.eatz_server.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import imwhs.eatz_server.auth.*;
import imwhs.eatz_server.config.properties.JwtConfigProperties;
import imwhs.eatz_server.repository.RefreshTokenRepository;
import imwhs.eatz_server.service.EatzUserDetailsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Slf4j
@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final AuthenticationConfiguration authenticationConfiguration;

    private final ObjectMapper objectMapper;

    private final TokenManager tokenManager;

    private final JwtConfigProperties jwtConfigProperties;

    private final EatzUserDetailsService userDetailsService;

    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

    private final RefreshTokenRepository refreshTokenRepository;

    private final String[] publicUrls = {
            "/",
            "/auth/**",
            "/login",
            "/sign-up/**",
            "/reissue-token",
            "/sign-out",
            "/hello-admin",
            "/css/**",
            "/js/**",
            "/images/**",
            "/webjars/**"
    };

    @Bean
    protected AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        JsonAuthenticationFilter jsonAuthenticationfilter = new JsonAuthenticationFilter(
                objectMapper,
                tokenManager,
                jwtConfigProperties,
                authenticationManager(authenticationConfiguration),
                refreshTokenRepository
        );

        http
                .csrf(csrf -> csrf.disable())
                .formLogin(form -> form.disable())
                .httpBasic(httpBasic -> httpBasic.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(publicUrls).permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated())
                .logout(logout -> logout.disable())
                .addFilterBefore(
                        new AccessTokenFilter(tokenManager, userDetailsService),
                        JsonAuthenticationFilter.class)
                .addFilterAt(
                        jsonAuthenticationfilter,
                        UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(customAuthenticationEntryPoint))
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        ;

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
