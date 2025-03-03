package imwhs.eatz_server.service;

import imwhs.eatz_server.auth.EatzUserAuthUtil;
import imwhs.eatz_server.domain.eatzuser.EatzUser;
import imwhs.eatz_server.domain.liked.Liked;
import imwhs.eatz_server.domain.liked.LikedType;
import imwhs.eatz_server.dto.liked.LikedDetailDto;
import imwhs.eatz_server.dto.liked.LikedDto;
import imwhs.eatz_server.dto.eatzuser.EatzUserBasicDto;
import imwhs.eatz_server.exception.EatzUserNotFoundException;
import imwhs.eatz_server.repository.comment.CommentRepository;
import imwhs.eatz_server.repository.eatzuser.EatzUserRepository;
import imwhs.eatz_server.repository.liked.LikedQueryRepository;
import imwhs.eatz_server.repository.liked.LikedRepository;
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
public class LikedService {

    private final LikedRepository likedRepository;

    private final EatzUserRepository userRepository;

    private final RecipeRepository recipeRepository;

    private final CommentRepository commentRepository;

    @Transactional
    public LikedDto toggleLikeOf(Long entityId, LikedType type) {
        String username = EatzUserAuthUtil.getUsername();
        EatzUser user = userRepository.findByUsername(username).orElseThrow(() ->
                new EatzUserNotFoundException(username + "에 해당하는 사용자가 존재하지 않아요."));

        log.info("{} ID: {}", type.name(), entityId);
        log.info("username: {} | user id: {}", username, user.getId());

        validateEntityById(entityId, type);
        Optional<Liked> existingLike = likedRepository.findByUserIdAndEntityIdAndType(user.getId(), entityId, type);

        boolean isLiked;
        Long likeId;

        if (existingLike.isPresent()) {
            // 좋아요 레코드가 존재하는 경우 - isLiked 토글 처리 진행
            isLiked = existingLike.get().toggleIsLiked();
            likeId = existingLike.get().getId();
        } else {
            // 좋아요 레코드가 없는 경우 - isLiked가 true인 새 좋아요 레코드 생성
            Liked liked = Liked.of(user, entityId, type);
            likedRepository.save(liked);
            isLiked = liked.getIsLiked();
            likeId = liked.getId();
        }

        long likedCount = likedRepository.countAllLikeds(entityId, type);

        return new LikedDto(likeId, entityId, type, isLiked, likedCount);
    }

    public boolean isLikedByUser(Long userId, Long entityId, LikedType type) {
        if (!userRepository.existsById(userId)) {
            throw new EatzUserNotFoundException("사용자(" + userId + ")가 존재하지 않아요.");
        }

        validateEntityById(entityId, type);
        return likedRepository.existsByUserIdAndEntityIdAndTypeAndIsLikedIsTrue(userId, entityId, type);
    }

    public LikedDetailDto getLikedDetails(Long entityId, LikedType type) {
        validateEntityById(entityId, type);

        LikedDetailDto dto = likedRepository.findAllByEntityIdAndType(entityId, type);
        List<EatzUserBasicDto> likedUsersDto = likedRepository.findLikedUsersByEntityIdAndType(entityId, type);
        dto.setLikedUsers(likedUsersDto);
        return dto;
    }

    private void validateEntityById(Long entityId, LikedType type) {
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
