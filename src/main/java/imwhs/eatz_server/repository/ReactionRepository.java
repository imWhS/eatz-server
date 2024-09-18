package imwhs.eatz_server.repository;

import imwhs.eatz_server.domain.reaction.Reaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReactionRepository extends JpaRepository<Reaction, Long> {



}
