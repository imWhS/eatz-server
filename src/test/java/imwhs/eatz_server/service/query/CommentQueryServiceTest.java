package imwhs.eatz_server.service.query;

import imwhs.eatz_server.domain.*;
import imwhs.eatz_server.dto.PagedApiResponse;
import imwhs.eatz_server.dto.comment.CommentByUserResponseDto;
import imwhs.eatz_server.dto.comment.CommentDetailResponseDto;
import imwhs.eatz_server.dto.comment.CommentByRecipeResponseDto;
import imwhs.eatz_server.repository.comment.CommentRepository;
import imwhs.eatz_server.repository.eatzuser.EatzUserRepository;
import imwhs.eatz_server.repository.recipe.RecipeRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
@SpringBootTest
public class CommentQueryServiceTest {

    @Autowired
    CommentQueryService commentQueryService;

    @Autowired
    CommentRepository commentRepository;

    @Autowired
    EatzUserRepository userRepository;

    @Autowired
    RecipeRepository recipeRepository;

    @Test
    @Transactional
    void findCommentDetailTest() {
        // given
        EatzUser recipeWriter = EatzUser.createMember("heextoryAA", "heextoryA@icloud.com", "1q2w3e4r!", Role.MEMBER);
        userRepository.save(recipeWriter);

        Recipe recipe = Recipe.of(
                recipeWriter,
                "Kimchi Pasta",
                "https://www.naver.com/",
                "https://www.naver.com/test.jpg",
                "맛있는 김치 파스타를 즐겨보세요!");
        recipeRepository.save(recipe);

        EatzUser commentWriter = EatzUser.createMember("heextoryBBB", "heextoryB@icloud.com", "1q2w3e4r!", Role.MEMBER);
        userRepository.save(commentWriter);

        Recipe recipeA = Recipe.of(
                commentWriter,
                "Kimchi Pasta",
                "https://www.naver.com/",
                "https://www.naver.com/test.jpg",
                "맛있는 김치 파스타를 즐겨보세요!");
        recipeRepository.save(recipeA);

        Recipe recipeB = Recipe.of(
                commentWriter,
                "Kimchi Pasta",
                "https://www.naver.com/",
                "https://www.naver.com/test.jpg",
                "맛있는 김치 파스타를 즐겨보세요!");
        recipeRepository.save(recipeB);

        String commentContent = "이런 존맛 레시피 발견한 나 럭키비키쟌앙~";

        Comment comment = new Comment(commentWriter, recipe, commentContent);
        commentRepository.save(comment);

        // when
        CommentDetailResponseDto commentDetailResponseDto = commentQueryService.findCommentDetail(comment.getId());

        // then
        Assertions.assertNotNull(commentDetailResponseDto);
        Assertions.assertEquals(comment.getId(), commentDetailResponseDto.getId());
        Assertions.assertEquals(commentWriter.getId(), commentDetailResponseDto.getUser().getId());
        Assertions.assertEquals(2, commentDetailResponseDto.getUser().getRecipeCount());
        Assertions.assertEquals(recipe.getId(), commentDetailResponseDto.getRecipe().getId());
        Assertions.assertEquals(recipe.getTitle(), commentDetailResponseDto.getRecipe().getTitle());
        Assertions.assertEquals(comment.getContent(), commentDetailResponseDto.getContent());
    }

    @Test
    @Transactional
    void findCommentsByRecipeTest() {
        // given
        EatzUser recipeWriter = EatzUser.createMember("heextory", "heextory@icloud.com", "1q2w3e4r!", Role.MEMBER);
        userRepository.save(recipeWriter);

        Recipe recipe = Recipe.of(
                recipeWriter,
                "Kimchi Pasta",
                "https://www.naver.com/",
                "https://www.naver.com/test.jpg",
                "맛있는 김치 파스타를 즐겨보세요!");
        recipeRepository.save(recipe);
        Long recipeId = recipe.getId();

        EatzUser commentWriterA = EatzUser.createMember("commentWriterA", "heextoryA@icloud.com", "1q2w3e4r!", Role.MEMBER);
        userRepository.save(commentWriterA);

        Comment commentA = new Comment(commentWriterA, recipe, "이런 존맛 레시피 발견한 나 럭키비키쟌앙~");
        commentRepository.save(commentA);
        CommentByRecipeResponseDto commentAResponseDto = new CommentByRecipeResponseDto(commentA);

        EatzUser commentWriterB = EatzUser.createMember("commentWriterB", "heextoryB@icloud.com", "1q2w3e4r!", Role.MEMBER);
        userRepository.save(commentWriterB);

        Comment commentB = new Comment(commentWriterB, recipe, "헉 이거 뭐야?");
        commentRepository.save(commentB);
        CommentByRecipeResponseDto commentBResponseDto = new CommentByRecipeResponseDto(commentB);

        // when
        PagedApiResponse<CommentByRecipeResponseDto> pagedComments = commentQueryService.findCommentsByRecipe(recipeId, null, null);

        // then
        Assertions.assertEquals(1, pagedComments.getTotalPages());
        Assertions.assertEquals(2, pagedComments.getTotalItems());
        List<CommentByRecipeResponseDto> comments = pagedComments.getData();
        Assertions.assertEquals(2, comments.size());
        Assertions.assertTrue(comments.contains(commentAResponseDto));
        Assertions.assertTrue(comments.contains(commentBResponseDto));
    }

    @Test
    @Transactional
    void findCommentsByUserTest() {
        // given
        EatzUser recipeWriterA = EatzUser.createMember("heextoryA", "heextoryA@icloud.com", "1q2w3e4r!", Role.MEMBER);
        userRepository.save(recipeWriterA);

        Recipe recipeKimchi = Recipe.of(
                recipeWriterA,
                "Kimchi Pasta",
                "https://www.naver.com/",
                "https://www.naver.com/test.jpg",
                "맛있는 김치 파스타를 즐겨보세요!");
        recipeRepository.save(recipeKimchi);

        EatzUser recipeWriterB = EatzUser.createMember("heextoryB", "heextoryB@icloud.com", "1q2w3e4r!", Role.MEMBER);
        userRepository.save(recipeWriterB);

        Recipe recipeGarlic = Recipe.of(
                recipeWriterB,
                "Garlic BBOKKEUMBOB",
                "https://www.naver.com/",
                "https://www.naver.com/test.jpg",
                "마늘 듬뿍 볶음밥입니당");
        recipeRepository.save(recipeGarlic);

        EatzUser commentWriter = EatzUser.createMember("commentWriter", "writer@icloud.com", "1q2w3e4r!", Role.MEMBER);
        userRepository.save(commentWriter);
        Long commentWriterId = commentWriter.getId();

        Comment commentA = new Comment(commentWriter, recipeKimchi, "이런 존맛 레시피 발견한 나 럭키비키쟌앙~");
        commentRepository.save(commentA);
        CommentByUserResponseDto commentAResponseDto = new CommentByUserResponseDto(commentA);

        Comment commentB = new Comment(commentWriter, recipeGarlic, "헉 이거 뭐야?");
        commentRepository.save(commentB);
        CommentByUserResponseDto commentBResponseDto = new CommentByUserResponseDto(commentB);

        // when
        PagedApiResponse<CommentByUserResponseDto> pagedComments = commentQueryService.findCommentsByUser(commentWriterId, null, null);

        // then
        Assertions.assertEquals(1, pagedComments.getTotalPages());
        Assertions.assertEquals(2, pagedComments.getTotalItems());
        List<CommentByUserResponseDto> comments = pagedComments.getData();
        Assertions.assertEquals(2, comments.size());
        Assertions.assertTrue(comments.contains(commentAResponseDto));
        Assertions.assertTrue(comments.contains(commentBResponseDto));
    }

}
