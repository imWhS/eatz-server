package imwhs.eatz_server.repository;

import imwhs.eatz_server.domain.user.EatzUser;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class EatzUserRepository {

    @PersistenceContext
    private EntityManager em;

    public void save(EatzUser eatzUser) {
        em.persist(eatzUser);
    }

    public EatzUser findOne(Long id) {
        EatzUser eatzUser = em.find(EatzUser.class, id);
        return eatzUser;
    }

    public List<EatzUser> findAll() {
        return em.createQuery("select e from EatzUser e", EatzUser.class)
                .getResultList();
    }

    public List<EatzUser> findByUsername(String username) {
        return em.createQuery("select e from EatzUser e where e.username = :username", EatzUser.class)
                .setParameter("username", username)
                .getResultList();

    }

}
