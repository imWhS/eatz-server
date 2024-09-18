package imwhs.eatz_server.service;

import imwhs.eatz_server.domain.EatzUser;
import imwhs.eatz_server.domain.Recipe;
import imwhs.eatz_server.domain.reaction.Comment;
import imwhs.eatz_server.domain.reaction.Rating;
import imwhs.eatz_server.domain.reaction.Reaction;
import imwhs.eatz_server.dto.*;
import imwhs.eatz_server.exception.EatzUserNotFoundException;
import imwhs.eatz_server.exception.ReactionNotFoundException;
import imwhs.eatz_server.exception.RecipeNotFoundException;
import imwhs.eatz_server.repository.EatzUserRepository;
import imwhs.eatz_server.repository.ReactionRepository;
import imwhs.eatz_server.repository.RecipeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class ReactionService {

    private final ReactionRepository reactionRepository;

    private final EatzUserRepository userRepository;

    private final RecipeRepository recipeRepository;

    @Transactional
    public Long registerReaction(Long recipeId, Long userId, CreateReactionDto dto) {
        Recipe recipe = recipeRepository.findById(recipeId).orElseThrow(
                () -> new RecipeNotFoundException("id가 " + recipeId + "인 레시피를 찾을 수 없습니다."));
        EatzUser user = userRepository.findById(userId).orElseThrow(
                () -> new EatzUserNotFoundException("id가 " + userId + "인 사용자를 찾을 수 없습니다."));

        // 동적 바인딩: CreateCommentDto.toReactionEntity() 또는 CreateRatingDto.toReactionEntity()
        Reaction reaction = dto.toReactionEntity(recipe, user);

        reactionRepository.save(reaction);

        // TODO: rating이 이미 등록된 경우, 추가(중복)되지 않도록 예외 처리

        return reaction.getId();
    }

    @Transactional
    public void updateReaction(Long id, Long userId, UpdateReactionDto dto) {
        Reaction reaction = reactionRepository.findById(id).orElseThrow(
                () -> new ReactionNotFoundException("id가 " + id + "인 반응을 찾지 못했습니다."));

        if (!userRepository.existsById(userId)) {
            throw new EatzUserNotFoundException("id가 " + userId + "인 사용자를 찾지 못했습니다.");
        }

        if (reaction instanceof Comment comment) {
            comment.update((UpdateCommentDto) dto);
        } else if (reaction instanceof Rating rating) {
            rating.update((UpdateRatingDto) dto);
        } else {
            throw new IllegalArgumentException("올바르지 않은 요청 데이터입니다.");
        }
    }

}
