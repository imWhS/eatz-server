package imwhs.eatz_server.service.comment;

import imwhs.eatz_server.dto.comment.CommentBasicDto;
import imwhs.eatz_server.dto.comment.CommentEssentialWithRecipeDto;
import imwhs.eatz_server.repository.comment.CommentRepository;
import imwhs.eatz_server.repository.EatzUserRepository;
import imwhs.eatz_server.repository.recipe.RecipeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 댓글(Comment) 관련 정보를 조회하기 위한 서비스입니다.
 * Comment에 대한 읽기 전용 쿼리 메서드를 제공합니다.
 */
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class CommentQueryService {

    private final CommentRepository commentRepository;
    private final RecipeRepository recipeRepository;
    private final EatzUserRepository userRepository;

    /**
     * 레시피에 달린 댓글의 기본 정보 목록을 가져옵니다.
     * <ul>
     *     <li> 회원 탈퇴한 사용자가 작성한 레시피에 달린 댓글도 목록에 포함합니다. </li>
     *     <li> 차단한 사용자의 댓글은 조회 대상에서 제외합니다. </li>
     * </ul>
     * @param id 레시피의 ID
     * @param userId 사용자의 ID
     * @return 조회된 댓글의 기본 정보 목록 및 페이징 정보
     */
    public Page<CommentBasicDto> getAllBasicsByRecipeId(Long id, Long userId, Pageable pageable) {
        recipeRepository.validateExists(id);
        return commentRepository.findAllBasicsByRecipeId(id, userId, pageable);
    }

    /**
     * 특정 사용자가 작성한 모든 댓글의 기본 정보 목록을 가져옵니다.
     * <ul>
     *     <li> 회원 탈퇴한 사용자가 작성한 레시피에 달린 댓글도 목록에 포함합니다. </li>
     * </ul>
     * @param id 레시피의 ID
     * @return 조회된 댓글의 기본 정보 목록 및 페이징 정보
     */
    public Page<CommentEssentialWithRecipeDto> getAllEssentialsByAuthorId(Long id, Pageable pageable) {
        userRepository.validateExists(id);
        return commentRepository.findAllEssentialsWithRecipeByAuthorId(id, pageable);
    }

}
