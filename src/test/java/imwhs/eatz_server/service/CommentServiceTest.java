//package imwhs.eatz_server.service;
//
//import imwhs.eatz_server.domain.Comment;
//import imwhs.eatz_server.domain.eatzuser.EatzUser;
//import imwhs.eatz_server.domain.recipe.Recipe;
//import imwhs.eatz_server.exception.CommentNotFoundException;
//import imwhs.eatz_server.exception.UnauthorizedEatzUserException;
//import imwhs.eatz_server.repository.comment.CommentRepository;
//import imwhs.eatz_server.repository.EatzUserRepository;
//import imwhs.eatz_server.repository.recipe.RecipeRepository;
//import org.assertj.core.api.Assertions;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.transaction.annotation.Transactional;
//
//@Transactional(readOnly = true)
//@SpringBootTest
//public class CommentServiceTest {
//
//    @Autowired
//    private CommentService commentService;
//
//    @Autowired
//    private CommentRepository commentRepository;
//
//    @Autowired
//    private EatzUserRepository userRepository;
//
//    @Autowired
//    private RecipeRepository recipeRepository;
//
////    @Test
////    @Transactional
////    void commentRegisterTest() {
////        // given
////        EatzUser user = EatzUser.createMember("heextory", "heextory@icloud.com", "1q2w3e4r!");
////        userRepository.save(user);
////        Long userId = user.getId();
////
////        Recipe recipe = Recipe.create(user, "Kimchi pasta", "https://www.naver.com/", "https://www.naver.com/img.png", "맛있는 김치 파스타를 즐겨볼까요?");
////        recipeRepository.save(recipe);
////        Long recipeId = recipe.getId();
////
////        String commentContent = "내 맘 속에 저장~";
////
////        // when
////        Long commentId = commentService.register(recipeId, commentContent);
////
////        // then
////        boolean present = commentRepository.findById(commentId).isPresent();
////        Assertions.assertThat(present).isTrue();
////        Comment commentFound = commentRepository.findById(commentId).get();
////        Assertions.assertThat(commentFound.getContent()).isEqualTo(commentContent);
////        Assertions.assertThat(commentFound.getCreatedAt()).isNotNull();
////        Assertions.assertThat(commentFound.getUpdatedAt()).isEqualTo(commentFound.getCreatedAt());
////        Assertions.assertThat(commentFound.getDeletedAt()).isNull();
////    }
//
//    @Test
//    @Transactional
//    void updateTest() {
//        // given
//        EatzUser user = EatzUser.createMember("heextory", "heextory@icloud.com", "1q2w3e4r!");
//        userRepository.save(user);
//        Long userId = user.getId();
//
//        Recipe recipe = Recipe.create(
//                user,
//                "Kimchi pasta",
//                "https://www.naver.com/",
//                "https://www.naver.com/img.png",
//                "맛있는 김치 파스타를 즐겨볼까요?");
//        recipeRepository.save(recipe);
//
//        String commentContentBefore = "내 맘 속에 저장~";
//        String commentContentAfter = "나는 별루,, 내 맘 속의 별루,,,,";
//
//        Comment comment = new Comment(user, recipe, commentContentBefore);
//        commentRepository.save(comment);
//        Long commentId = comment.getId();
//
//        // when
//        commentService.update(commentId, userId, commentContentAfter);
//        commentRepository.flush();
//
//        // then
//        Comment editedComment = commentRepository.findById(commentId)
//                .orElseThrow(CommentNotFoundException::new);
//        Assertions.assertThat(editedComment.getContent()).isEqualTo(commentContentAfter);
//        Assertions.assertThat(editedComment.getId()).isEqualTo(commentId);
//        Assertions.assertThat(editedComment.getCreatedAt()).isNotNull();
//        Assertions.assertThat(editedComment.getUpdatedAt()).isNotEqualTo(editedComment.getCreatedAt());
//        Assertions.assertThat(editedComment.getDeletedAt()).isNull();
//    }
//
//    @Test
//    @Transactional
//    void updateByInvalidUserTest() {
//        // given
//        EatzUser user = EatzUser.createMember("heextoryA", "heextory@icloud.com", "1q2w3e4r!");
//        userRepository.save(user);
//
//        Long invalidUserId = 99999L;
//
//        Recipe recipe = Recipe.create(
//                user,
//                "Kimchi pasta",
//                "https://www.naver.com/",
//                "https://www.naver.com/img.png",
//                "맛있는 김치 파스타를 즐겨볼까요?");
//        recipeRepository.save(recipe);
//
//        String commentContentBefore = "내 맘 속에 저장~";
//        String commentContentAfter = "나는 별루,, 내 맘 속의 별루,,,,";
//
//        Comment comment = new Comment(user, recipe, commentContentBefore);
//        commentRepository.save(comment);
//        Long commentId = comment.getId();
//
//        // when, then
//        Assertions.assertThatThrownBy(() ->
//                        commentService.update(commentId, invalidUserId, commentContentAfter))
//                .isInstanceOf(UnauthorizedEatzUserException.class);
//    }
//
//    @Test
//    @Transactional
//    void deleteTest() {
//        // given
//        EatzUser user = EatzUser.createMember("heextoryB", "heextory@icloud.com", "1q2w3e4r!");
//        userRepository.save(user);
//        Long userId = user.getId();
//
//        Recipe recipe = Recipe.create(
//                user,
//                "Kimchi pasta",
//                "https://www.naver.com/",
//                "https://www.naver.com/img.png",
//                "맛있는 김치 파스타를 즐겨볼까요?");
//        recipeRepository.save(recipe);
//
//        String commentContent = "내 맘 속에 저장~";
//
//        Comment comment = new Comment(user, recipe, commentContent);
//        commentRepository.save(comment);
//        Long commentId = comment.getId();
//
//        // when
//        commentService.delete(commentId, userId);
//
//        // then
//        Assertions.assertThat(commentRepository.findByIdAndDeletedAtIsNull(commentId)).isEmpty();
//    }
//
//    @Test
//    @Transactional
//    void deleteByInvalidUserTest() {
//        // given
//        EatzUser user = EatzUser.createMember("heextoryC", "heextory@icloud.com", "1q2w3e4r!");
//        userRepository.save(user);
//        Long invalidUserId = 99999L;
//
//        Recipe recipe = Recipe.create(
//                user,
//                "Kimchi pasta",
//                "https://www.naver.com/",
//                "https://www.naver.com/img.png",
//                "맛있는 김치 파스타를 즐겨볼까요?");
//        recipeRepository.save(recipe);
//
//        String commentContent = "내 맘 속에 저장~";
//
//        Comment comment = new Comment(user, recipe, commentContent);
//        commentRepository.save(comment);
//        Long commentId = comment.getId();
//
//        // when, then
//        Assertions.assertThatThrownBy(() ->
//                commentService.delete(commentId, invalidUserId))
//                .isInstanceOf(UnauthorizedEatzUserException.class);
//    }
//
////    @Test
////    @Transactional
////    void findCommentByIdTest() {
////        // given
////        EatzUser user = EatzUser.createMember(
////                "heextoryAA",
////                "heextory@icloud.com",
////                "1q2w3e4r!");
////        userRepository.save(user);
////
////        Recipe recipe = Recipe.create(
////                user,
////                "Kimchi Pasta",
////                "https://www.naver.com/",
////                "https://www.naver.com/test.jpg",
////                "맛있는 김치 파스타를 즐겨보세요!");
////        recipeRepository.save(recipe);
////
////        String content = "이런 존맛 레시피 발견한 나 럭키비키쟌앙~";
////
////        Comment comment = new Comment(user, recipe, content);
////        commentRepository.save(comment);
////
////        // when
////        CommentByRecipeResponseDto commentDto = commentService.findComment(comment.getId());
////
////        // then
////        Assertions.assertThat(commentDto).isNotNull();
////        Assertions.assertThat(commentDto.getId()).isEqualTo(comment.getId());
////        Assertions.assertThat(commentDto.getContent()).isEqualTo(comment.getContent());
////        Assertions.assertThat(commentDto.getUser()).isEqualTo(new CommentUserDto(comment.getUser()));
////    }
//
////    @Test
////    @Transactional
////    void findCommentsByRecipeByUserByUserAndRecipeTest() {
////        // given
////        EatzUser user = EatzUser.createMember(
////                "heextoryBB",
////                "heextory@icloud.com",
////                "1q2w3e4r!");
////        userRepository.save(user);
////
////        Recipe recipe = Recipe.create(
////                user,
////                "Kimchi Pasta",
////                "https://www.naver.com/",
////                "https://www.naver.com/test.jpg",
////                "맛있는 김치 파스타를 즐겨보세요!");
////        recipeRepository.save(recipe);
////
////        String content1 = "이런 존맛 레시피 발견한 나 럭키비키쟌앙~";
////        Comment comment1 = new Comment(user, recipe, content1);
////        commentRepository.save(comment1);
////
////        String content2 = "이거 완전 별루.. 내 맘 속의 별루,,,,,,";
////        Comment comment2 = new Comment(user, recipe, content2);
////        commentRepository.save(comment2);
////
////        // when
////        List<CommentWithUserResponseDto> comments = commentService.findCommentsByUser(
////                user.getId(),
////                recipe.getId(),
////                null,
////                null);
////
////        // then
////        Assertions.assertThat(comments.getTotalElements()).isEqualTo(2);
////    }
//
//}
