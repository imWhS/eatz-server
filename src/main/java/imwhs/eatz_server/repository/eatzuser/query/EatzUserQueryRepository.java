package imwhs.eatz_server.repository.eatzuser.query;

import imwhs.eatz_server.dto.PagedResponse;
import imwhs.eatz_server.dto.eatzuser.EatzUserSummaryDto;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class EatzUserQueryRepository {

    private final EntityManager em;

    public PagedResponse<EatzUserSummaryDto> findAllWithActivity(int page, int size) {
        List<EatzUserSummaryDto> items = em.createQuery(
                        "select new imwhs.eatz_server.dto.eatzuser.EatzUserSummaryDto(" +
                                "e.id, e.username, count(r)) from EatzUser e " +
                                "left join e.recipes r " +
                                "group by e.id", EatzUserSummaryDto.class)
                .setFirstResult(page * size)
                .setMaxResults(size)
                .getResultList();

        Long totalItems = em.createQuery(
                "select count(e.id) from EatzUser e", Long.class
        ).getSingleResult();

        int totalPages = (int) Math.ceil((double) totalItems / size);

        return PagedResponse.of(items, totalItems, totalPages, page, size);
    }

}
