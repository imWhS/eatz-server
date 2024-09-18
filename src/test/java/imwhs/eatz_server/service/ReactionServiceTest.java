package imwhs.eatz_server.service;

import imwhs.eatz_server.domain.EatzUser;
import imwhs.eatz_server.domain.Recipe;
import imwhs.eatz_server.domain.Role;
import imwhs.eatz_server.domain.reaction.Comment;
import imwhs.eatz_server.domain.reaction.Rating;
import imwhs.eatz_server.domain.reaction.Reaction;
import imwhs.eatz_server.dto.*;
import imwhs.eatz_server.repository.EatzUserRepository;
import imwhs.eatz_server.repository.ReactionRepository;
import imwhs.eatz_server.repository.RecipeRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
class ReactionServiceTest {

    @Autowired
    private ReactionService reactionService;

    @Autowired
    private ReactionRepository reactionRepository;

    @Autowired
    private EatzUserRepository userRepository;

    @Autowired
    private RecipeRepository recipeRepository;

    @Test
    @DisplayName("레시피에 댓글이 정상적으로 등록되는지 테스트합니다.")
    void registerCommentTest() {
        // given
        EatzUser user = EatzUser.create(
                "testuser",
                "testuser@example.com",
                "password123",
                Role.MEMBER);
        userRepository.save(user);
        Long userId = user.getId();

        Recipe recipe = Recipe.create(
                "Sample Recipe",
                "http://example.com/recipe",
                "http://example.com/image.jpg",
                "This is a sample recipe.");
        recipe.setUser(user);
        recipeRepository.save(recipe);
        Long recipeId = recipe.getId();

        CreateCommentDto dto = new CreateCommentDto("What a awesome recipe!");

        // when
        Long reactonId = reactionService.registerReaction(recipeId, userId, dto);

        // then
        Reaction foundReaction = reactionRepository.findById(reactonId)
                .orElseThrow(() -> new RuntimeException("반응을 찾을 수 없습니다."));
        Assertions.assertThat(foundReaction).isInstanceOf(Comment.class);
        Comment foundComment = (Comment) foundReaction;

        Assertions.assertThat(foundComment.getUser()).isEqualTo(user);
        Assertions.assertThat(foundComment.getRecipe()).isEqualTo(recipe);
        Assertions.assertThat(foundComment.getContent()).isEqualTo(dto.getContent());
    }

    @Test
    void registerRatingTest() {
        // given
        EatzUser user = EatzUser.create(
                "testuser",
                "testuser@example.com",
                "password123",
                Role.MEMBER);
        userRepository.save(user);
        Long userId = user.getId();

        Recipe recipe = Recipe.create(
                "Sample Recipe",
                "http://example.com/recipe",
                "http://example.com/image.jpg",
                "This is a sample recipe.");
        recipe.setUser(user);
        recipeRepository.save(recipe);
        Long recipeId = recipe.getId();

        CreateRatingDto dto = new CreateRatingDto(4);

        // when
        Long reactionId = reactionService.registerReaction(recipeId, userId, dto);

        // then
        Reaction foundReaction = reactionRepository.findById(reactionId)
                .orElseThrow(() -> new RuntimeException("반응을 찾을 수 없습니다."));
        Assertions.assertThat(foundReaction).isInstanceOf(Rating.class);
        Rating foundRating = (Rating) foundReaction;
        Assertions.assertThat(foundRating.getUser()).isEqualTo(user);
        Assertions.assertThat(foundRating.getRecipe()).isEqualTo(recipe);
        Assertions.assertThat(foundRating.getScore()).isEqualTo(dto.getScore());
    }

    @Test
    @DisplayName("레시피에 등록된 댓글이 정상적으로 수정되는지 테스트합니다.")
    void updateCommentTest() {
        // given
        EatzUser user = EatzUser.create(
                "testuser",
                "testuser@example.com",
                "password123",
                Role.MEMBER);
        userRepository.save(user);
        Long userId = user.getId();

        Recipe recipe = Recipe.create(
                "Sample Recipe",
                "http://example.com/recipe",
                "http://example.com/image.jpg",
                "This is a sample recipe.");
        recipe.setUser(user);
        recipeRepository.save(recipe);

        String content = "What a awesome recipe! It's my fav!";
        Comment comment = new Comment(recipe, user, content);
        reactionRepository.save(comment);
        Long commentId = comment.getId();

        // when
        UpdateCommentDto dto = new UpdateCommentDto("It's a terrible content.");
        reactionService.updateReaction(commentId, userId, dto);

        // then
        Reaction foundReaction = reactionRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("반응을 찾을 수 없습니다."));
        Assertions.assertThat(foundReaction).isInstanceOf(Comment.class);
        Comment foundComment = (Comment) foundReaction;
        Assertions.assertThat(foundComment.getUser()).isEqualTo(user);
        Assertions.assertThat(foundComment.getRecipe()).isEqualTo(recipe);
        Assertions.assertThat(foundComment.getContent()).isEqualTo(dto.getContent());
    }

    @Test
    @DisplayName("레시피에 등록된 평가가 정상적으로 수정되는지 테스트합니다.")
    void updateRatingTest() {
        // given
        EatzUser user = EatzUser.create(
                "testuser",
                "testuser@example.com",
                "password123",
                Role.MEMBER);
        userRepository.save(user);
        Long userId = user.getId();

        Recipe recipe = Recipe.create(
                "Sample Recipe",
                "http://example.com/recipe",
                "http://example.com/image.jpg",
                "This is a sample recipe.");
        recipe.setUser(user);
        recipeRepository.save(recipe);

        int score = 4;
        Rating rating = new Rating(recipe, user, score);
        reactionRepository.save(rating);
        Long ratingId = rating.getId();

        // when
        UpdateRatingDto dto = new UpdateRatingDto(1);
        reactionService.updateReaction(ratingId, userId, dto);

        // then
        Reaction foundReaction = reactionRepository.findById(ratingId)
                .orElseThrow(() -> new RuntimeException("반응을 찾을 수 없습니다."));
        Assertions.assertThat(foundReaction).isInstanceOf(Rating.class);
        Rating foundRating = (Rating) foundReaction;
        Assertions.assertThat(foundRating.getUser()).isEqualTo(user);
        Assertions.assertThat(foundRating.getRecipe()).isEqualTo(recipe);
        Assertions.assertThat(foundRating.getScore()).isEqualTo(dto.getScore());
    }

}