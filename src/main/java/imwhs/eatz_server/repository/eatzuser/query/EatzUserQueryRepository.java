package imwhs.eatz_server.repository.eatzuser.query;

import imwhs.eatz_server.dto.eatzuser.EatzUserActivityDto;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class EatzUserQueryRepository {

    private final EntityManager em;

    public List<EatzUserActivityDto> findAllWithActivity(Pageable pageable) {
        return em.createQuery(
                "select new imwhs.eatz_server.dto.eatzuser.EatzUserActivityDto(" +
                        "e.id, e.username, count(r)) from EatzUser e " +
                        "join e.recipes r " +
                        "group by e.id", EatzUserActivityDto.class)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();
    }

}
