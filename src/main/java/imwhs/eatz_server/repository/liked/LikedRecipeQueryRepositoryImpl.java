package imwhs.eatz_server.repository.liked;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Set;

@RequiredArgsConstructor
@Repository
public class LikedRecipeQueryRepositoryImpl implements LikedRecipeQueryRepository {

    private final JPAQueryFactory queryFactory;

}
