package imwhs.eatz_server.repository.recipe;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import imwhs.eatz_server.domain.eatzuser.QEatzUser;
import imwhs.eatz_server.domain.likes.QLikes;
import imwhs.eatz_server.domain.recipe.*;
import imwhs.eatz_server.dto.eatzuser.EatzUserSummaryDto;
import imwhs.eatz_server.dto.rating.RatingSummaryDto;
import imwhs.eatz_server.dto.recipe.CategoryDto;
import imwhs.eatz_server.dto.recipe.RecipeDetailDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * RecipeQueryRepository 클래스입니다.
 * <p>
 *     Recipe 엔티티의 복잡한 조회 쿼리를 처리하는 리포지토리입니다.
 *     QueryDSL을 기반으로 조회 쿼리를 생성, 실행합니다.
 * </p>
 */
@Slf4j
@RequiredArgsConstructor
@Repository
public class RecipeQueryRepository {

    private final JPAQueryFactory queryFactory;

    /**
     * ID에 해당하는 레시피의 상세 정보를 조회합니다.
     * 삭제 처리된 레시피는 조회 대상에서 제외됩니다.
     * @param id 레시피 식별자.
     * @return Optional로 wrapping된 RecipeDetailResponseDto. 레시피의 상세 정보를 담은 DTO입니다.
     */
    public Optional<RecipeDetailDto> findRecipeDetailById(Long id) {
        QRecipe recipe = QRecipe.recipe;
        QEatzUser user = QEatzUser.eatzUser;
        QComment comment = QComment.comment;
        QRating rating = QRating.rating;
        QLikes likes = QLikes.likes;

        return Optional.ofNullable(
                queryFactory.select(
                        Projections.constructor(RecipeDetailDto.class,
                                recipe.id,
                                Projections.constructor(EatzUserSummaryDto.class,
                                        user.id,
                                        user.username,
                                        queryFactory.select(recipe.count().intValue())
                                                .from(recipe)
                                                .where(recipe.user.eq(user))),
                                recipe.title,
                                recipe.url,
                                recipe.imageUrl,
                                recipe.description,
                                comment.id.countDistinct().intValue(),
                                Projections.constructor(RatingSummaryDto.class,
                                        // 사용자는 레시피에 하나의 평가만 남길 수 있기 때문에, 평가 식별자 값 기준으로 distinct를 적용합니다.
                                        rating.id.countDistinct().intValue(),
                                        rating.score.avg().doubleValue()),
                                queryFactory.select(likes.count().longValue())
                                        .from(likes)
                                        .where(likes.entityId.eq(recipe.id))
                        )
                )
                .from(recipe)
                .leftJoin(recipe.user, user)
                .leftJoin(recipe.comments, comment)
                .leftJoin(recipe.ratings, rating)
                .where(recipe.id.eq(id)
                        .and(recipe.deletedAt.isNull())
                )
                .groupBy(recipe.id)
                .fetchOne());
    }
        /*
        [COMMENT]
        id      recipe_id   content         user_id
        --      -------     -------         -------
        0       0           조보아씨 내려와봐요  1
        1       0           존맛인걸요         0
        1       1           나쁘지 않아용      2
        2       2           치즈 대박         2

        [RECIPE_CATEGORY]
        id      recipe_id   category_id
        --      ---------   -----------
        0       0           0
        1       0           1
        2       1           0

        [CATEGORY]
        id      name
        --      ----
        0       한식
        1       중식

        [RECIPE inner join EATZ_USER]
        id      title       username
        --      -----       ---------
        0       Kimchi      user_a
        1       Mandu       user_b
        2       Pizza       user_b

        [RECIPE left join EATZ_USER left join COMMENT]
        id      title       username        comment_content
        --      -----       --------        ---------------
        0       Kimchi      user_b          조보아씨 내려와봐요
        0       Kimchi      user_a          존맛인걸요
        1       Mandu       user_c          나쁘지 않아용
        2       Pizza       user_c          치즈 대박

        [RECIPE left join EATZ_USER left join COMMENT left join RECIPE_CATEGORY
        id      title       username        comment_content     category_id
        --      -----       --------        ---------------     -----------
        0       Kimchi      user_b          조보아씨 내려와봐요      0
        0       Kimchi      user_b          조보아씨 내려와봐요      1
        0       Kimchi      user_a          존맛인걸요             0
        0       Kimchi      user_a          존맛인걸요             1
        1       Mandu       user_c          나쁘지 않아용           0
        2       Pizza       user_c          치즈 대박             null

        [RECIPE left join EATZ_USER left join COMMENT left join RECIPE_CATEGORY left join CATEGORY
        id      title       username        comment_content     name
        --      -----       --------        ---------------     -----------
        0       Kimchi      user_b          조보아씨 내려와봐요      한식
        0       Kimchi      user_b          조보아씨 내려와봐요      중식
        0       Kimchi      user_a          존맛인걸요             한식
        0       Kimchi      user_a          존맛인걸요             중식
        1       Mandu       user_c          나쁘지 않아용           한식
        2       Pizza       user_c          치즈 대박              null

        [RECIPE left join EATZ_USER e left join COMMENT left join RECIPE_CATEGORY left join CATEGORY where e.id = 0]
        id      title       username        comment_content     name
        --      -----       --------        ---------------     -----------
        0       Kimchi      user_b          조보아씨 내려와봐요      한식
        0       Kimchi      user_b          조보아씨 내려와봐요      중식
        0       Kimchi      user_a          존맛인걸요             한식
        0       Kimchi      user_a          존맛인걸요             중식


        [RECIPE left join EATZ_USER e left join COMMENT left join RECIPE_CATEGORY left join CATEGORY where e.id = 0 group by e.id]
        id      title       username            comment_content             name
        --      -----       --------            ---------------             ----
        0       Kimchi      user_a, user_b      조보아씨 내려와봐요, 존맛인걸요     한식, 중식
         */



//    public Optional<RecipeDetailDto> testFindById2(Long id) {
//        QRecipe recipe = QRecipe.recipe;
//        QEatzUser user = QEatzUser.eatzUser;
//        QComment comment = QComment.comment;
//        QRating rating = QRating.rating;
//        QLikes likes = QLikes.likes;
//        QRecipeCategory recipeCategory = QRecipeCategory.recipeCategory;
//        QCategory category = QCategory.category;
//
//        queryFactory.from(recipe)
//                .leftJoin(recipe.user, user)
//                .leftJoin(recipe.comments, comment) //
//                .leftJoin(recipe.ratings, rating) //
//                .leftJoin(recipe.recipeCategories, recipeCategory) //
//                .leftJoin(recipeCategory.category, category)
//                .where(recipe.id.eq(id))
//                .groupBy(recipe.id)
//                .select(Projections.constructor(RecipeDetailDto.class,
//                        recipe.id,
//                        Projections.constructor(EatzUserSummaryDto.class,
//                                user.id,
//                                user.username,
//                                queryFactory.select(recipe.count().intValue()) // TODO: alias 분리?
//                                        .from(recipe)
//                                        .where(recipe.user.eq(user))
//                                )),
//                        recipe.title,
//                        recipe.url,
//                        recipe.imageUrl,
//                        recipe.description,
//                        comment.id.countDistinct().intValue(),
//                        Projections.constructor(RatingSummaryDto.class,
//                                rating.id.countDistinct().intValue(),
//                                rating.score.avg().doubleValue()
//                                ),
//                        queryFactory.select(likes.count().longValue())
//                                .from(likes)
//                                .where(likes.entityId.eq(id)),
//                        GroupBy.groupBy(
//                                Expressions.list(Projections.constructor(CategoryMinimumDto.class,
//                                        category.id,
//                                        category.name
//                                )))
//
//                .fetchOne();
//
//
//        return null;
//    }


    public Optional<RecipeDetailDto> testFindById(Long id) {
            QRecipe recipe = QRecipe.recipe;
            QEatzUser user = QEatzUser.eatzUser;
            QComment comment = QComment.comment;
            QRating rating = QRating.rating;
            QLikes likes = QLikes.likes;
            QRecipeCategory recipeCategory = QRecipeCategory.recipeCategory;
            QCategory category = QCategory.category;

        RecipeDetailDto recipeDetailDto =
                queryFactory.select(
                        Projections.constructor(RecipeDetailDto.class,
                                recipe.id,
                                Projections.constructor(EatzUserSummaryDto.class,
                                        user.id,
                                        user.username,
                                        queryFactory.select(recipe.count().intValue())
                                                .from(recipe)
                                                .where(recipe.user.eq(user))),
                                recipe.title,
                                recipe.url,
                                recipe.imageUrl,
                                recipe.description,
                                comment.id.countDistinct().intValue(),
                                Projections.constructor(RatingSummaryDto.class,
                                        rating.id.countDistinct().intValue(),
                                        rating.score.avg().doubleValue()),
                                queryFactory.select(likes.count().longValue())
                                        .from(likes)
                                        .where(likes.entityId.eq(recipe.id)),
                                Projections.list(Projections.constructor(CategoryDto.class,
                                        category.id.longValue(),
                                        category.name
                                )
                                )
                        )
                )
                .from(recipe)
                .leftJoin(recipe.user, user)
                .leftJoin(recipe.comments, comment)
                .leftJoin(recipe.ratings, rating)
                .leftJoin(recipe.recipeCategories, recipeCategory)
                .leftJoin(recipeCategory.category, category)
                .where(recipe.id.eq(id).and(recipe.deletedAt.isNull()))
                .groupBy(recipe.id)
                .fetchOne();

            ObjectMapper objectMapper = new ObjectMapper();

            try {
            // DTO를 JSON 문자열로 변환
            String jsonString = objectMapper.writeValueAsString(recipeDetailDto);

            // JSON 문자열을 콘솔에 출력
            log.info(jsonString);
        } catch (JsonProcessingException e) {
            log.error("Error from ObjectMapper: {}", e.getMessage());
            e.printStackTrace();
        }

        return Optional.of(recipeDetailDto);
    }

}
