package imwhs.eatz_server.service;

import imwhs.eatz_server.domain.Report;
import imwhs.eatz_server.domain.ReportCategory;
import imwhs.eatz_server.domain.ReportResourceType;
import imwhs.eatz_server.domain.eatzuser.EatzUser;
import imwhs.eatz_server.dto.report.ReportDto;
import imwhs.eatz_server.dto.report.ReportCreationInfoResponse;
import imwhs.eatz_server.dto.report.ReportCategoryCreationInfoResponse;
import imwhs.eatz_server.dto.report.ReportCategoryDto;
import imwhs.eatz_server.exception.ReportNotFoundException;
import imwhs.eatz_server.repository.ReportCategoryRepository;
import imwhs.eatz_server.repository.ReportRepository;
import imwhs.eatz_server.repository.comment.CommentRepository;
import imwhs.eatz_server.repository.EatzUserRepository;
import imwhs.eatz_server.repository.rating.RatingRepository;
import imwhs.eatz_server.repository.recipe.RecipeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class ReportService {

    private final ReportRepository reportRepository;
    private final EatzUserRepository userRepository;
    private final RecipeRepository recipeRepository;
    private final CommentRepository commentRepository;
    private final RatingRepository ratingRepository;
    private final ReportCategoryRepository reportCategoryRepository;

    @Transactional(rollbackFor = Exception.class)
    public ReportCreationInfoResponse register(
            Long userId,
            Long resourceId,
            ReportResourceType resourceType,
            Long categoryId,
            String resourceContent,
            String description) {
        EatzUser user = userRepository.get(userId);
        validateResourceById(resourceId, resourceType);
        ReportCategory category = reportCategoryRepository.get(categoryId);
        Report report = Report.of(user, resourceId, resourceType, category, resourceContent, description);
        reportRepository.save(report);
        return new ReportCreationInfoResponse(report);
    }

    @Transactional(rollbackFor = Exception.class)
    public ReportCategoryCreationInfoResponse registerReason(Long adminId, String code, String description) {
        userRepository.validateExistsAsAdmin(adminId);
        ReportCategory category = ReportCategory.create(code, description);
        reportCategoryRepository.save(category);
        return new ReportCategoryCreationInfoResponse(category);
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateCategory(Long adminId, Long id, String code, String description) {
        userRepository.validateExistsAsAdmin(adminId);
        ReportCategory category = reportCategoryRepository.get(id);
        category.updateCode(code);
        category.updateDescription(description);
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateCategoryCode(Long adminId, Long id, String code) {
        userRepository.validateExistsAsAdmin(adminId);
        ReportCategory category = reportCategoryRepository.get(id);
        category.updateCode(code);
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateCategoryDescription(Long adminId, Long id, String description) {
        userRepository.validateExistsAsAdmin(adminId);
        ReportCategory category = reportCategoryRepository.get(id);
        category.updateDescription(description);
    }

    @Transactional(rollbackFor = Exception.class)
    public void markAsResolved(Long adminId, Long reportId) {
        EatzUser admin = userRepository.getAdmin(adminId);
        Report report = reportRepository.findById(reportId).orElseThrow(
                () -> new ReportNotFoundException(reportId));

        report.resolve(admin);
    }

    public Page<ReportDto> getAllByResourceType(ReportResourceType resourceType, boolean resolved, Pageable pageable) {
        return reportRepository.findAllByResource(resourceType, resolved, pageable);
    }

    public List<ReportCategoryDto> getAllCategories() {
        return reportCategoryRepository.findAllActiveCategories();
    }

    private void validateResourceById(Long id, ReportResourceType resource) {
        switch (resource) {
            case RECIPE:
                recipeRepository.validateExists(id);
                break;
            case COMMENT:
                commentRepository.validateExists(id);
                break;
            case RATING:
                ratingRepository.validateExists(id);
                break;
        }
    }

}
