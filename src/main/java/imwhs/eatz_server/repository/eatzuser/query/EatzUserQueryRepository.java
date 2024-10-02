package imwhs.eatz_server.repository.eatzuser.query;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class EatzUserQueryRepository {

    private final EntityManager em;

}
