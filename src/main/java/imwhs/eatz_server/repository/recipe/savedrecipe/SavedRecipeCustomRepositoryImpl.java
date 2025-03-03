package imwhs.eatz_server.repository.recipe.savedrecipe;

import com.querydsl.jpa.impl.JPAQueryFactory;
import imwhs.eatz_server.domain.recipe.QSavedRecipe;
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
        QSavedRecipe savedRecipe = QSavedRecipe.savedRecipe;

        queryFactory
                .delete(savedRecipe)
                .where(savedRecipe.user.id.eq(userId)
                        .and(savedRecipe.recipe.id.eq(id)))
                .execute();
    }

}
