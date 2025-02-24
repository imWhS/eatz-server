package imwhs.eatz_server.repository.recipe.savedrecipe;

import com.querydsl.jpa.impl.JPAQueryFactory;
import imwhs.eatz_server.domain.recipe.QNSavedRecipe;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class SavedRecipeCustomRepositoryImpl implements SavedRecipeCustomRepository {

    private final JPAQueryFactory queryFactory;

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteSavedRecipe(Long userId, Long id) {
        QNSavedRecipe savedRecipe = QNSavedRecipe.nSavedRecipe;

        queryFactory
                .delete(savedRecipe)
                .where(savedRecipe.user.id.eq(userId)
                        .and(savedRecipe.recipe.id.eq(id)))
                .execute();
    }

}
