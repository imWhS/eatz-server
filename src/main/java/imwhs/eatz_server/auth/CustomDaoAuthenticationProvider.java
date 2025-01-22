package imwhs.eatz_server.auth;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

// TODO
@Slf4j
public class CustomDaoAuthenticationProvider extends DaoAuthenticationProvider {

    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken authentication) {
        log.info("CustomDaoAuthenticationProvider.additionalAuthenticationChecks()");
        try {
            super.additionalAuthenticationChecks(userDetails, authentication);
        } catch (UsernameNotFoundException e) {
            log.warn("User {} not found", userDetails.getUsername());
            throw e;
        }
    }
}
