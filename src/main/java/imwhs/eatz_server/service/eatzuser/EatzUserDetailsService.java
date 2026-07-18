package imwhs.eatz_server.service.eatzuser;

import imwhs.eatz_server.domain.eatzuser.EatzUser;
import imwhs.eatz_server.dto.eatzuser.userdetail.EatzUserDetails;
import imwhs.eatz_server.repository.EatzUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class EatzUserDetailsService implements UserDetailsService {

    private final EatzUserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        EatzUser user = userRepository.findByEmailAndDeletedAtIsNull(username).orElseThrow(
                () -> new UsernameNotFoundException(username));
        return new EatzUserDetails(user);
    }

}
