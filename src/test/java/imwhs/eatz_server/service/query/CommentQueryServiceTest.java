package imwhs.eatz_server.service.query;

import imwhs.eatz_server.domain.*;
import imwhs.eatz_server.dto.PagedResponse;
import imwhs.eatz_server.dto.comment.CommentByUserResponseDto;
import imwhs.eatz_server.dto.comment.CommentDetailResponseDto;
import imwhs.eatz_server.dto.comment.CommentByRecipeResponseDto;
import imwhs.eatz_server.dto.comment.CommentUserDto;
import imwhs.eatz_server.repository.comment.CommentQueryRepository;
import imwhs.eatz_server.repository.comment.CommentRepository;
import imwhs.eatz_server.repository.eatzuser.EatzUserRepository;
import imwhs.eatz_server.repository.recipe.RecipeRepository;
import imwhs.eatz_server.service.CommentService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
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
    @Autowired
    private CommentService commentService;
    @Autowired
    private CommentQueryRepository commentQueryRepository;

    @Test
    @Transactional
    void findCommentByIdTest() {
        // given
        EatzUser user = EatzUser.create("heextoryAA", "heextory@icloud.com", "1q2w3e4r!", Role.MEMBER);
        userRepository.save(user);

        Recipe recipe = Recipe.of(
                user,
                "Kimchi Pasta",
                "https://www.naver.com/",
                "https://www.naver.com/test.jpg",
                "맛있는 김치 파스타를 즐겨보세요!");
        recipeRepository.save(recipe);

        String content = "이런 존맛 레시피 발견한 나 럭키비키쟌앙~";

        Comment comment = new Comment(user, recipe, content);
        commentRepository.save(comment);

        // when
        CommentByRecipeResponseDto commentDto = commentService.findComment(comment.getId());

        // then
        Assertions.assertNotNull(commentDto);
        Assertions.assertEquals(comment.getId(), commentDto.getId());
        Assertions.assertEquals(comment.getContent(), commentDto.getContent());
        Assertions.assertEquals(new CommentUserDto(comment.getUser()), commentDto.getUser());
    }



    @Test
    @Transactional
    void findCommentDetailTest() {
        // given
        EatzUser recipeWriter = EatzUser.create("heextoryAA", "heextoryA@icloud.com", "1q2w3e4r!", Role.MEMBER);
        userRepository.save(recipeWriter);

        Recipe recipe = Recipe.of(
                recipeWriter,
                "Kimchi Pasta",
                "https://www.naver.com/",
                "https://www.naver.com/test.jpg",
                "맛있는 김치 파스타를 즐겨보세요!");
        recipeRepository.save(recipe);

        EatzUser commentWriter = EatzUser.create("heextoryBBB", "heextoryB@icloud.com", "1q2w3e4r!", Role.MEMBER);
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
        EatzUser recipeWriter = EatzUser.create("heextory", "heextory@icloud.com", "1q2w3e4r!", Role.MEMBER);
        userRepository.save(recipeWriter);

        Recipe recipe = Recipe.of(
                recipeWriter,
                "Kimchi Pasta",
                "https://www.naver.com/",
                "https://www.naver.com/test.jpg",
                "맛있는 김치 파스타를 즐겨보세요!");
        recipeRepository.save(recipe);
        Long recipeId = recipe.getId();

        EatzUser commentWriterA = EatzUser.create("commentWriterA", "heextoryA@icloud.com", "1q2w3e4r!", Role.MEMBER);
        userRepository.save(commentWriterA);

        Comment commentA = new Comment(commentWriterA, recipe, "이런 존맛 레시피 발견한 나 럭키비키쟌앙~");
        commentRepository.save(commentA);
        CommentByRecipeResponseDto commentAResponseDto = new CommentByRecipeResponseDto(commentA);

        EatzUser commentWriterB = EatzUser.create("commentWriterB", "heextoryB@icloud.com", "1q2w3e4r!", Role.MEMBER);
        userRepository.save(commentWriterB);

        Comment commentB = new Comment(commentWriterB, recipe, "헉 이거 뭐야?");
        commentRepository.save(commentB);
        CommentByRecipeResponseDto commentBResponseDto = new CommentByRecipeResponseDto(commentB);

        // when
        PagedResponse<CommentByRecipeResponseDto> pagedComments = commentQueryService.findCommentsByRecipe(recipeId, null, null);
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
        EatzUser recipeWriterA = EatzUser.create("heextoryA", "heextoryA@icloud.com", "1q2w3e4r!", Role.MEMBER);
        userRepository.save(recipeWriterA);

        Recipe recipeKimchi = Recipe.of(
                recipeWriterA,
                "Kimchi Pasta",
                "https://www.naver.com/",
                "https://www.naver.com/test.jpg",
                "맛있는 김치 파스타를 즐겨보세요!");
        recipeRepository.save(recipeKimchi);

        EatzUser recipeWriterB = EatzUser.create("heextoryB", "heextoryB@icloud.com", "1q2w3e4r!", Role.MEMBER);
        userRepository.save(recipeWriterB);

        Recipe recipeGarlic = Recipe.of(
                recipeWriterB,
                "Garlic BBOKKEUMBOB",
                "https://www.naver.com/",
                "https://www.naver.com/test.jpg",
                "마늘 듬뿍 볶음밥입니당");
        recipeRepository.save(recipeGarlic);

        EatzUser commentWriter = EatzUser.create("commentWriter", "writer@icloud.com", "1q2w3e4r!", Role.MEMBER);
        userRepository.save(commentWriter);
        Long commentWriterId = commentWriter.getId();

        Comment commentA = new Comment(commentWriter, recipeKimchi, "이런 존맛 레시피 발견한 나 럭키비키쟌앙~");
        commentRepository.save(commentA);
        CommentByUserResponseDto commentAResponseDto = new CommentByUserResponseDto(commentA);

        Comment commentB = new Comment(commentWriter, recipeGarlic, "헉 이거 뭐야?");
        commentRepository.save(commentB);
        CommentByUserResponseDto commentBResponseDto = new CommentByUserResponseDto(commentB);

        // when
        PagedResponse<CommentByUserResponseDto> pagedComments = commentQueryService.findCommentsByUser(commentWriterId, null, null);
        Assertions.assertEquals(1, pagedComments.getTotalPages());
        Assertions.assertEquals(2, pagedComments.getTotalItems());
        List<CommentByUserResponseDto> comments = pagedComments.getData();
        System.out.println("comments.size() = " + comments.size());
        for (CommentByUserResponseDto comment : comments) {
            System.out.println("comment.getContent() = " + comment.getContent());
        }
        Assertions.assertEquals(2, comments.size());
        Assertions.assertTrue(comments.contains(commentAResponseDto));
        Assertions.assertTrue(comments.contains(commentBResponseDto));
    }


    @Test
    @Transactional
    void findCommentsByUserAndRecipeTest() {
        // given
        EatzUser user = EatzUser.create("heextoryBB", "heextory@icloud.com", "1q2w3e4r!", Role.MEMBER);
        userRepository.save(user);

        Recipe recipe = Recipe.of(
                user,
                "Kimchi Pasta",
                "https://www.naver.com/",
                "https://www.naver.com/test.jpg",
                "맛있는 김치 파스타를 즐겨보세요!");
        recipeRepository.save(recipe);

        String content1 = "이런 존맛 레시피 발견한 나 럭키비키쟌앙~";
        Comment comment1 = new Comment(user, recipe, content1);
        commentRepository.save(comment1);

        String content2 = "이거 완전 별루.. 내 맘 속의 별루,,,,,,";
        Comment comment2 = new Comment(user, recipe, content2);
        commentRepository.save(comment2);

        // when
        Page<CommentByRecipeResponseDto> comments = commentService.findComments(user.getId(), recipe.getId(), null, null);

        // then
        Assertions.assertEquals(comments.getTotalElements(), 2);
    }

}
