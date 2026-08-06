package imwhs.eatz_server.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import imwhs.eatz_server.auth.*;
import imwhs.eatz_server.config.properties.JwtConfigProperties;
import imwhs.eatz_server.repository.RefreshTokenRepository;
import imwhs.eatz_server.service.eatzuser.EatzUserDetailsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

@Slf4j
@EnableWebSecurity
@Configuration
public class SecurityConfig {

    private final ObjectMapper objectMapper;
    private final AuthenticationConfiguration authenticationConfiguration;
    private final TokenManager tokenManager;
    private final EatzUserDetailsService userDetailsService;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    private final RefreshTokenRepository refreshTokenRepository;
    private final HandlerExceptionResolver handlerExceptionResolver;

    public SecurityConfig(
            ObjectMapper objectMapper,
            AuthenticationConfiguration authenticationConfiguration,
            TokenManager tokenManager,
            EatzUserDetailsService userDetailsService,
            CustomAuthenticationEntryPoint customAuthenticationEntryPoint,
            RefreshTokenRepository refreshTokenRepository,
            // HandlerExceptionResolverComposite를 DI 받습니다.
            @Qualifier("handlerExceptionResolver") HandlerExceptionResolver handlerExceptionResolver
    ) {
        this.objectMapper = objectMapper;
        this.authenticationConfiguration = authenticationConfiguration;
        this.tokenManager = tokenManager;
        this.userDetailsService = userDetailsService;
        this.customAuthenticationEntryPoint = customAuthenticationEntryPoint;
        this.refreshTokenRepository = refreshTokenRepository;
        this.handlerExceptionResolver = handlerExceptionResolver;
    }

    private static final String[] PUBLIC_URLS = {
            "/",
            "/api/v0/system/**",
            "/error",
            "/api/v0/auth/**",
            "/login",
            "/api/v0/sign-up/**",
            "/api/v0/reissue-token",
            "/api/v0/sign-out",
            "/api/v0/recipes/urls/**",
            "/hello-admin",
            "/css/**",
            "/js/**",
            "/images/**",
            "/webjars/**",
    };

    private static final String[] PUBLIC_URLS_READ_METHOD = {
            "/api/v0/recipes/**",
            "/api/v0/ingredients/**",
            "/api/v0/kitchenwares/**",
            "/api/v0/tags/**",
            "/api/v0/themes/**",
            "/api/v0/users/search/**",
            "/uploads/**"
    };

    @Bean
    protected AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        JsonAuthenticationFilter jsonAuthenticationfilter = new JsonAuthenticationFilter(
                handlerExceptionResolver,
                objectMapper,
                tokenManager,
                authenticationManager(authenticationConfiguration),
                refreshTokenRepository);

        AccessTokenFilter accessTokenFilter = new AccessTokenFilter(
                handlerExceptionResolver,
                tokenManager,
                userDetailsService);

        http
                .csrf(csrf -> csrf.disable())
                .formLogin(form -> form.disable())
                .httpBasic(httpBasic -> httpBasic.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(PUBLIC_URLS).permitAll()
                        .requestMatchers(HttpMethod.GET, PUBLIC_URLS_READ_METHOD).permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN") // "ROLE_ADMIN"
                        .anyRequest().authenticated())
                .logout(logout -> logout.disable())
                .addFilterBefore(
                        accessTokenFilter,
                        JsonAuthenticationFilter.class)
                .addFilterAt(
                        jsonAuthenticationfilter,
                        UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(customAuthenticationEntryPoint))
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
