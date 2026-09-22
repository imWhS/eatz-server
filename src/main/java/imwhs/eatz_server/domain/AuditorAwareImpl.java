package imwhs.eatz_server.domain;

import imwhs.eatz_server.auth.EatzUserAuth;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class AuditorAwareImpl implements AuditorAware<Long> {

    @Override
    public Optional<Long> getCurrentAuditor() {
        return EatzUserAuth.getId();
    }

}
