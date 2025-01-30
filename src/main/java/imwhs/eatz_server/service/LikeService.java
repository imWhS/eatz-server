package imwhs.eatz_server.service;

import imwhs.eatz_server.auth.EatzUserAuthUtil;
import imwhs.eatz_server.domain.EatzUser;
import imwhs.eatz_server.domain.Likes;
import imwhs.eatz_server.domain.LikesType;
import imwhs.eatz_server.dto.LikeDetailDto;
import imwhs.eatz_server.dto.LikeDto;
import imwhs.eatz_server.dto.eatzuser.EatzUserMinimumDto;
import imwhs.eatz_server.exception.EatzUserNotFoundException;
import imwhs.eatz_server.repository.comment.CommentRepository;
import imwhs.eatz_server.repository.eatzuser.EatzUserRepository;
import imwhs.eatz_server.repository.like.LikeQueryRepository;
import imwhs.eatz_server.repository.like.LikeRepository;
import imwhs.eatz_server.repository.recipe.RecipeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class LikeService {

    private final LikeRepository likeRepository;

    private final LikeQueryRepository likeQueryRepository;

    private final EatzUserRepository userRepository;

    private final RecipeRepository recipeRepository;

    private final CommentRepository commentRepository;

    @Transactional
    public LikeDto toggleLikeOf(Long entityId, LikesType type) {
        String username = EatzUserAuthUtil.getUsername();
        EatzUser user = userRepository.findByUsername(username).orElseThrow(() ->
                new EatzUserNotFoundException(username + "에 해당하는 사용자가 존재하지 않아요."));

        log.info("{} ID: {}", type.name(), entityId);
        log.info("username: {} | user id: {}", username, user.getId());

        validateEntityById(entityId, type);
        Optional<Likes> existingLike = likeRepository.findByUserIdAndEntityIdAndType(user.getId(), entityId, type);

        boolean isLiked;
        Long likeId;

        if (existingLike.isPresent()) {
            // 좋아요 레코드가 존재하는 경우 - isLiked 토글 처리 진행
            isLiked = existingLike.get().toggleIsLiked();
            likeId = existingLike.get().getId();
        } else {
            // 좋아요 레코드가 없는 경우 - isLiked가 true인 새 좋아요 레코드 생성
            Likes likes = Likes.of(user, entityId, type);
            likeRepository.save(likes);
            isLiked = likes.getIsLiked();
            likeId = likes.getId();
        }

        long likesCount = likeRepository.countAllLikes(entityId, type);

        return new LikeDto(likeId, entityId, type, isLiked, likesCount);
    }

    public boolean isLikedByUser(Long userId, Long entityId, LikesType type) {
        if (!userRepository.existsById(userId)) {
            throw new EatzUserNotFoundException("사용자(" + userId + ")가 존재하지 않아요.");
        }

        validateEntityById(entityId, type);
        return likeRepository.existsByUserIdAndEntityIdAndTypeAndIsLikedIsTrue(userId, entityId, type);
    }

    public LikeDetailDto getLikeDetails(Long entityId, LikesType type) {
        validateEntityById(entityId, type);

        LikeDetailDto dto = likeRepository.findAllByEntityIdAndType(entityId, type);
        List<EatzUserMinimumDto> likedUsersDto = likeRepository.findLikedUsersByEntityIdAndType(entityId, type);
        dto.setLikedUsers(likedUsersDto);
        return dto;
    }

    private void validateEntityById(Long entityId, LikesType type) {
        switch (type) {
            case RECIPE:
                validateRecipeById(entityId);
                break;
            case COMMENT:
                validateCommentById(entityId);
                break;
        }
    }

    private void validateCommentById(Long entityId) {
        if (!commentRepository.existsById(entityId)) {
            throw new IllegalArgumentException("댓글(" + entityId + ")이 존재하지 않아요.");
        }
    }

    private void validateRecipeById(Long entityId) {
        if (!recipeRepository.existsById(entityId)) {
            throw new IllegalArgumentException("레시피(" + entityId + ")가 존재하지 않아요.");
        }
    }

}
