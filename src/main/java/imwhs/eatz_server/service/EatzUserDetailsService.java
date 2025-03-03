package imwhs.eatz_server.service;

import imwhs.eatz_server.domain.eatzuser.EatzUser;
import imwhs.eatz_server.dto.eatzuser.userdetail.EatzUserDetails;
import imwhs.eatz_server.repository.eatzuser.EatzUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EatzUserDetailsService implements UserDetailsService {

    private final EatzUserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("이메일 주소 '{}'에 해당하는 사용자를 조회할게요!", username);
        EatzUser user = userRepository.findByEmail(username).orElseThrow(
                () -> new UsernameNotFoundException(username));
        return new EatzUserDetails(user);
    }

}
