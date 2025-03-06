package imwhs.eatz_server.service;

import imwhs.eatz_server.domain.Report;
import imwhs.eatz_server.domain.eatzuser.EatzUser;
import imwhs.eatz_server.domain.eatzuser.EatzUserRole;
import imwhs.eatz_server.domain.liked.EntityType;
import imwhs.eatz_server.dto.ReportDto;
import imwhs.eatz_server.exception.EatzUserNotFoundException;
import imwhs.eatz_server.exception.ReportNotFoundException;
import imwhs.eatz_server.exception.UnauthorizedEatzUserException;
import imwhs.eatz_server.repository.ReportRepository;
import imwhs.eatz_server.repository.comment.CommentRepository;
import imwhs.eatz_server.repository.eatzuser.EatzUserRepository;
import imwhs.eatz_server.repository.recipe.RecipeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class ReportService {

    private final ReportRepository reportRepository;

    private final EatzUserRepository userRepository;

    private final RecipeRepository recipeRepository;

    private final CommentRepository commentRepository;

    @Transactional
    public Long register(Long userId, Long entityId, EntityType type, String content) {
        EatzUser user = findUser(userId);
        validateEntityById(entityId, type);
        Report report = Report.of(user, entityId, type, content);
        reportRepository.save(report);
        return report.getId();
    }

    @Transactional
    public void markAsResolved(Long adminId, Long reportId) {
        EatzUser admin = findUser(adminId);

        if (admin.getEatzUserRole().equals(EatzUserRole.ROLE_MEMBER)) {
            throw new UnauthorizedEatzUserException("관리자 권한이 없는 사용자입니다.");
        }

        Report report = reportRepository.findById(reportId).orElseThrow(
                () -> new ReportNotFoundException(reportId));

        report.resolve(admin);
    }

    public Page<ReportDto> findByType(EntityType type, Pageable pageable) {
        return reportRepository.findByType(type, pageable);
    }

    private EatzUser findUser(Long userId) {
        EatzUser user = userRepository.findById(userId).orElseThrow(() -> new EatzUserNotFoundException(userId));
        return user;
    }

    private void validateEntityById(Long entityId, EntityType type) {
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
